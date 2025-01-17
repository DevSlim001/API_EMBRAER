package com.slim.manual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.Cleanup;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletContext;

import com.slim.manual.domain.model.Arquivo;
import com.slim.manual.domain.model.Bloco;
import com.slim.manual.domain.model.BlocoRevisao;
import com.slim.manual.domain.model.Codelist;
import com.slim.manual.domain.model.CodelistLinha;
import com.slim.manual.domain.model.Manual;
import com.slim.manual.domain.model.ModificacaoBloco;
import com.slim.manual.domain.model.Revisao;
import com.slim.manual.domain.model.Secao;
import com.slim.manual.domain.model.SubSecao;
import com.slim.manual.domain.model.Traco;
import com.slim.manual.domain.repository.ArquivoRepository;
import com.slim.manual.domain.repository.BlocoRepository;
import com.slim.manual.domain.repository.BlocoRevisaoRepository;
import com.slim.manual.domain.repository.ManualRepository;
import com.slim.manual.domain.repository.RevisaoRepository;
import com.slim.manual.domain.repository.SecaoRepository;
import com.slim.manual.domain.repository.SubSecaoRepository;
import com.slim.manual.domain.repository.TracoRepository;
import com.slim.manual.exception.ManualNotFoundException;
import com.slim.manual.exception.UploadArquivoBlocoException;
import com.slim.manual.exception.UploadCodelistException;
import com.slim.manual.rest.dto.ArquivoDeltaDTO;
import com.slim.manual.rest.dto.ArquivoFullDTO;
import com.slim.manual.rest.dto.ArquivoLepDTO;
import com.slim.manual.rest.dto.ManualDTO;

@Service
public class ManualService {

    @Autowired
    private TracoRepository tracoRepository;

    @Autowired
    private ManualRepository manualRepository;

    @Autowired
    private BlocoRepository blocoRepository;

    @Autowired
    private SecaoRepository secaoRepository;

    @Autowired
    private SubSecaoRepository subSecaoRepository;

    @Autowired
    private RevisaoRepository revisaoRepository;

    @Autowired
    private ArquivoRepository arquivoRepository;

    @Autowired
    private BlocoRevisaoRepository blocoRevisaoRepository;

    @Autowired
    private ServletContext servletContext;

    File raiz = new File("./src/main/resources/arquivos");

    public List<ManualDTO> getManuais() {
        List<ManualDTO> manuais = new ArrayList<ManualDTO>();
        manualRepository.findAll().forEach(manual -> {
            manuais.add(manual.toManualDTO());
        });
        return manuais;
    }

    public Manual getManualById(Integer codManual) {
        return manualRepository.findById(codManual)
                .orElseThrow(() -> new ManualNotFoundException("Manual não encontrado."));
    }

    public Manual deleteBloco(Integer codManual, Integer codBloco) {
        return manualRepository.findById(codManual).map((manual) -> {
            manual.getSecoes().forEach(secao -> {
                blocoRepository.findById(codBloco).ifPresentOrElse((bloco) -> {
                    if (secao.getBlocos().contains(bloco)) {
                        secao.getBlocos().remove(bloco);
                    } else {
                        secao.getSubSecoes().forEach(subSecao -> {
                            if (subSecao.getBlocos().contains(bloco)) {
                                subSecao.getBlocos().remove(bloco);
                            }
                        });
                    }
                }, () -> {
                    throw new ManualNotFoundException("Bloco não encontrado.");
                });
            });
            manualRepository.save(manual);
            return manual;
        }).orElseThrow(() -> new ManualNotFoundException("Manual não encontrado."));
    }

    public ManualDTO createManual(ManualDTO manual) {
        return manualRepository.save(manual.toEntityInsert()).toManualDTO();
    }

    private String findDiretorio(String path, String numberSecao) {

        File raizDoc = new File(path);
        List<String> diretorios = new ArrayList<String>(Arrays.asList(raizDoc.list()));
        Iterator<String> diretoriosIterator = diretorios.iterator();
        String res = "";
        while (diretoriosIterator.hasNext()) {
            String diretorio = diretoriosIterator.next();
            boolean isPresent = diretorio.split(" ")[0].equals(numberSecao);
            if (isPresent) {
                res = diretorio;
                break;
            } else {
                res = "false";
            }

        }
        return res;
    }

    public Manual importCodelist(MultipartFile arquivo, Integer codManual) throws IOException {
        Optional<Manual> manualBD = manualRepository.findById(codManual);
        return manualBD.map((manual) -> {
            try {
                String manualPath = manual.getNome() + "-" + manual.getPartNumber();
                List<String> diretoriosArquivos = Arrays.asList(raiz.list());

                if (diretoriosArquivos.contains(manualPath)) {
                    String diretorioManualPath = raiz.getPath() + "/" + manualPath + "/";
                    File diretorioManualMaster = new File(diretorioManualPath + "Master/");

                    @Cleanup
                    FileInputStream codelistFile = (FileInputStream) arquivo.getInputStream();
                    @Cleanup
                    Workbook workbook = new XSSFWorkbook(codelistFile);
                    Sheet sheet = workbook.getSheetAt(0);
                    List<Row> linhas = IteratorUtils.toList(sheet.iterator());
                    List<Traco> tracos = new ArrayList<Traco>();
                    List<Secao> secoes = new ArrayList<Secao>();
                    String[] ultimoNumSecao = { "" };
                    String[] ultimoNumSubSecao = { "" };
                    linhas.forEach(linha -> {

                        List<Cell> celulas = IteratorUtils.toList(linha.cellIterator());
                        if (linhas.indexOf(linha) != 0) {
                            // pega todos os tracos
                            if (linhas.indexOf(linha) == 1) {
                                celulas.forEach(celula -> {
                                    Integer index = celulas.indexOf(celula);
                                    if (index >= 6) {
                                        List<String> tracoCelula = Arrays.asList(celula.toString().split(" - "));
                                        Traco traco = Traco.builder().traco(Integer.valueOf(tracoCelula.get(0)))
                                                .nome(tracoCelula.get(1)).manual(manual).build();
                                        tracos.add(tracoRepository.save(traco));
                                    }
                                });
                            } else {
                                String[] filePath = { "" };

                                // Começo seção
                                Secao secao = new Secao();
                                secaoRepository.findByNumSecaoAndManual(celulas.get(0).toString(), manual)
                                        .ifPresentOrElse(s -> {
                                            secao.setManual(manual);
                                            secao.setCodSecao(s.getCodSecao());
                                            secao.setNumSecao(s.getNumSecao());
                                            secao.setSubSecoes(s.getSubSecoes());
                                            secao.setBlocos(s.getBlocos());
                                        }, () -> {
                                            secao.setManual(manual);
                                            secao.setNumSecao(celulas.get(0).toString());
                                            secao.setCodSecao(secaoRepository.save(secao).getCodSecao());
                                        });

                                // Fim seção
                                filePath[0] = diretorioManualMaster.getPath() + "/";
                                String findDiretorioSecao = findDiretorio(filePath[0], secao.getNumSecao());
                                if (!findDiretorioSecao.equals("false")) {
                                    filePath[0] = filePath[0].concat(findDiretorioSecao + "/");
                                }

                                // Começo sub-seção
                                SubSecao subSecao = new SubSecao();
                                if (!celulas.get(1).toString().isBlank()) {
                                    subSecaoRepository.findByNumSubSecaoAndSecao(celulas.get(1).toString(), secao)
                                            .ifPresentOrElse(s -> {
                                                subSecao.setCodSubSecao(s.getCodSubSecao());
                                                subSecao.setNumSubSecao(s.getNumSubSecao());
                                                subSecao.setSecao(secao);
                                                subSecao.setBlocos(s.getBlocos());
                                            }, () -> {
                                                subSecao.setNumSubSecao(celulas.get(1).toString());
                                                subSecao.setSecao(secao);
                                                subSecao.setCodSubSecao(
                                                        subSecaoRepository.save(subSecao).getCodSubSecao());

                                            });
                                }

                                // Fim sub-seção

                                // Começo bloco
                                Bloco bloco;

                                if (!subSecao.equals(new SubSecao())) {
                                    String findDiretorioSubSecao = findDiretorio(filePath[0],
                                            subSecao.getNumSubSecao());
                                    if (!findDiretorioSubSecao.equals("false")) {
                                        filePath[0] = filePath[0].concat(findDiretorioSubSecao + "/");
                                    }
                                }

                                // Começo traços do bloco
                                String tracoCelula = celulas.get(5).toString();
                                final List<Traco> tracosBloco = new ArrayList<Traco>();
                                if (tracoCelula.equals("ALL")) {
                                    tracosBloco.addAll(tracos);
                                } else {
                                    // traços presentes naquele bloco no arquivo do codelist
                                    List<Integer> tracosInteger = new ArrayList<Integer>();

                                    Arrays.asList(tracoCelula.split(",")).forEach(traco -> {
                                        Double valorDouble = Math.abs(Double.valueOf(traco));
                                        Integer valor = valorDouble.intValue();
                                        tracosInteger.add(valor);
                                    });

                                    tracos.forEach(tr -> {
                                        Integer traco = tr.getTraco();
                                        if (tracosInteger.contains(traco)) {
                                            tracosBloco.add(tr);
                                        }
                                    });
                                }
                                // Fim traços do bloco
                                if (subSecao.equals(new SubSecao())) {
                                    bloco = Bloco.builder().numBloco(celulas.get(2).toString())
                                            .nomeBloco(celulas.get(3).toString())
                                            .codBlocoCodelist(celulas.get(4).toString()).tracos(tracosBloco)
                                            .secao(secao).build();
                                } else {
                                    bloco = Bloco.builder().numBloco(celulas.get(2).toString())
                                            .nomeBloco(celulas.get(3).toString())
                                            .codBlocoCodelist(celulas.get(4).toString()).tracos(tracosBloco)
                                            .subSecao(subSecao).build();
                                }

                                String findDiretorioBloco = findDiretorio(filePath[0], bloco.getNumBloco());
                                if (!findDiretorioBloco.equals("false")) {
                                    filePath[0] = filePath[0].concat(findDiretorioBloco + "/");
                                }
                                if (subSecao.equals(new SubSecao())) {
                                    filePath[0] = filePath[0].concat(manualPath + "-" + secao.getNumSecao() + "-"
                                            + bloco.getNumBloco() + "c" + bloco.getCodBlocoCodelist() + ".pdf");
                                } else {
                                    filePath[0] = filePath[0].concat(manualPath + "-" + secao.getNumSecao() + "-"
                                            + subSecao.getNumSubSecao() + "-" + bloco.getNumBloco() + "c"
                                            + bloco.getCodBlocoCodelist() + ".pdf");
                                }
                                File fileBloco = new File(filePath[0]);

                                if (fileBloco.exists()) {
                                    bloco.setArquivo(Arquivo.builder().nomeArquivo(filePath[0]).build());
                                }
                                // Fim bloco
                                if (secoes.size() == 0) {
                                    ultimoNumSecao[0] = secao.getNumSecao();
                                    if (!subSecao.equals(new SubSecao())) {

                                        ultimoNumSubSecao[0] = subSecao.getNumSubSecao();
                                        subSecao.getBlocos().add(bloco);

                                        secao.getSubSecoes().add(subSecao);
                                        secoes.add(secao);
                                    } else {

                                        secao.getBlocos().add(bloco);

                                        secoes.add(secao);
                                    }
                                } else {
                                    if (!secao.getNumSecao().equals(ultimoNumSecao[0])) {
                                        ultimoNumSecao[0] = secao.getNumSecao();
                                        ultimoNumSubSecao[0] = "";
                                        if (!subSecao.equals(new SubSecao())) {

                                            ultimoNumSubSecao[0] = subSecao.getNumSubSecao();
                                            subSecao.getBlocos().add(bloco);
                                            secao.getSubSecoes().add(subSecao);
                                            secoes.add(secao);
                                        } else {

                                            secao.getBlocos().add(bloco);
                                            secoes.add(secao);
                                        }
                                    } else {
                                        Integer ultimoIndexSecao = secoes.size() - 1;
                                        Integer ultimoIndexSubSecao = secoes.get(ultimoIndexSecao).getSubSecoes().size()
                                                - 1;
                                        if (!subSecao.equals(new SubSecao())) {

                                            if (!subSecao.getNumSubSecao().equals(ultimoNumSubSecao[0])) {
                                                ultimoNumSubSecao[0] = subSecao.getNumSubSecao();

                                                subSecao.getBlocos().add(bloco);
                                                secoes.get(ultimoIndexSecao).getSubSecoes().add(subSecao);
                                            } else {
                                                secoes.get(ultimoIndexSecao).getSubSecoes().get(ultimoIndexSubSecao)
                                                        .getBlocos().add(bloco);
                                            }
                                        } else {

                                            secoes.get(ultimoIndexSecao).getBlocos().add(bloco);
                                        }
                                    }
                                }

                            }

                        }

                    });
                    manual.setSecoes(secoes);
                } else {
                    throw new ManualNotFoundException(
                            "Copie os arquivos para o servidor antes de cadastrar o codelist.");
                }
            } catch (IOException e) {
                throw new UploadCodelistException(e.getMessage());
            }
            return manualRepository.save(manual);
        }).orElseThrow(() -> new ManualNotFoundException("Manual não encontrado."));
    }

    public Manual createCodelist(Codelist codelist, Integer codManual) {
        Optional<Manual> manualBD = manualRepository.findById(codManual);
        return manualBD.map((manual) -> {
            List<Traco> tracos = new ArrayList<Traco>();
            List<Secao> secoes = new ArrayList<Secao>();
            String[] ultimoNumSecao = { "" };
            String[] ultimoNumSubSecao = { "" };

            codelist.getCodelist().forEach(linha -> {
                if (tracos.size() != 0) {
                    List<String> tracoList = new ArrayList<String>();
                    tracos.forEach(t -> {
                        tracoList.add(t.getTraco().toString());
                    });

                    linha.getTracos().forEach(tracoReq -> {
                        if (!tracoList.contains(tracoReq)) {
                            Traco t = Traco.builder().traco(Integer.valueOf(tracoReq)).build();
                            tracos.add(tracoRepository.save(t));
                        }
                    });
                } else {
                    linha.getTracos().forEach(tracoReq -> {

                        Traco t = Traco.builder().traco(Integer.valueOf(tracoReq)).build();
                        tracos.add(tracoRepository.save(t));
                    });
                }

                // Começo seção
                Secao secao = new Secao();
                secao.setNumSecao(linha.getNumSecao());
                // Fim seção

                // Começo sub-seção
                SubSecao subSecao = new SubSecao();
                subSecao.setNumSubSecao(linha.getNumSubSecao());
                // Fim sub-seção

                // Começo bloco
                Bloco bloco;

                // Começo traços do bloco
                List<String> tracosReq = linha.getTracos();
                final List<Traco> tracosBloco = new ArrayList<Traco>();
                tracos.forEach(traco -> {
                    if (tracosReq.contains(String.valueOf(traco.getTraco()))) {
                        tracosBloco.add(traco);
                    }
                });
                // Fim traços do bloco

                bloco = Bloco.builder().numBloco(linha.getNumBloco()).nomeBloco(linha.getNomeBloco())
                        .codBlocoCodelist(linha.getCodBlocoCodelist()).tracos(tracosBloco).build();
                // Fim bloco
                if (secoes.size() == 0) {
                    ultimoNumSecao[0] = secao.getNumSecao();
                    if (!subSecao.equals(new SubSecao())) {
                        ultimoNumSubSecao[0] = subSecao.getNumSubSecao();
                        subSecao.getBlocos().add(bloco);
                        secao.getSubSecoes().add(subSecao);
                        secoes.add(secao);
                    } else {
                        secao.getBlocos().add(bloco);

                        secoes.add(secao);
                    }
                } else {

                    if (!secao.getNumSecao().equals(ultimoNumSecao[0])) {
                        ultimoNumSecao[0] = secao.getNumSecao();
                        ultimoNumSubSecao[0] = "";

                        if (!subSecao.getNumSubSecao().equals("")) {
                            ultimoNumSubSecao[0] = subSecao.getNumSubSecao();
                            subSecao.getBlocos().add(bloco);
                            secao.getSubSecoes().add(subSecao);
                            secoes.add(secao);
                        } else {
                            secao.getBlocos().add(bloco);
                            secoes.add(secao);
                        }
                    } else {
                        Integer ultimoIndexSecao = secoes.size() - 1;
                        Integer ultimoIndexSubSecao = secoes.get(ultimoIndexSecao).getSubSecoes().size() - 1;
                        if (!subSecao.getNumSubSecao().equals("")) {
                            if (!subSecao.getNumSubSecao().equals(ultimoNumSubSecao[0])) {
                                ultimoNumSubSecao[0] = subSecao.getNumSubSecao();

                                subSecao.getBlocos().add(bloco);
                                secoes.get(ultimoIndexSecao).getSubSecoes().add(subSecao);
                            } else {
                                secoes.get(ultimoIndexSecao).getSubSecoes().get(ultimoIndexSubSecao).getBlocos()
                                        .add(bloco);
                            }
                        } else {
                            secoes.get(ultimoIndexSecao).getBlocos().add(bloco);
                        }
                    }

                }
            });
            manual.setSecoes(secoes);

            return manualRepository.save(manual);

        }).orElseThrow(() -> new ManualNotFoundException("Manual não encontrado."));
    }

    /*
     * public void importArquivoBloco(MultipartFile arquivo, Integer codBloco)
     * throws IOException {
     * blocoRepository.findById(codBloco).ifPresentOrElse((bloco) -> { try { if
     * (arquivo != null && !arquivo.isEmpty()) {
     * 
     * String filePath = "./Downloads/" + arquivo.getOriginalFilename(); try {
     * 
     * File file = new File(filePath);
     * 
     * FileOutputStream saida = new FileOutputStream(file);
     * copiar(arquivo.getInputStream(), saida); Arquivo arquivoBloco =
     * Arquivo.builder().nomeArquivo(arquivo.getOriginalFilename()).build();
     * bloco.setArquivo(arquivoBloco); blocoRepository.save(bloco); } catch
     * (IOException e) { e.printStackTrace(); } }
     * 
     * } catch (Exception e) { new UploadArquivoBlocoException(e.getMessage()); }
     * 
     * }, () -> { throw new ManualNotFoundException("Bloco não encontrado."); }); }
     */

    private void cadRevisao(Integer codManual) {
        manualRepository.findById(codManual).ifPresentOrElse((manual) -> {
            File raizRevisao = new File(
                    raiz.getPath() + "/" + manual.getNome() + "-" + manual.getPartNumber() + "/Rev/");
            List<String> listRevisoes = Arrays.asList(raizRevisao.list());

            listRevisoes.forEach((rev) -> {
                if (!revisaoRepository.findByNomeRevisaoAndManual(rev, manual).isPresent()) {
                    File revDiret = new File(raizRevisao.getPath() + "/" + rev + "/Edição/");
                    if (revDiret.exists()) {
                        Revisao revisao = Revisao.builder().nomeRevisao(rev).manual(manual).build();
                        revisao.setCodRevisao(revisaoRepository.save(revisao).getCodRevisao());
                        List<String> secaoList = Arrays.asList(revDiret.list());
                        if (secaoList.size() > 0) {
                            secaoList.forEach((s) -> {
                                secaoRepository.findByNumSecaoAndManual(s.split(" ")[0], manual)
                                        .ifPresentOrElse((secao) -> {
                                            File secaoDiret = new File(revDiret.getPath() + "/" + s + "/");
                                            List<String> itemsSecaoDiret = Arrays.asList(secaoDiret.list());
                                            itemsSecaoDiret.forEach(i -> {
                                                File itemDiret = new File(secaoDiret.getPath() + "/" + i + "/");
                                                List<String> items = Arrays.asList(itemDiret.list());
                                                items.forEach(it -> {

                                                    File item = new File(itemDiret.getPath() + "/" + it);
                                                    if (item.isDirectory()) {
                                                        // item vai ser uma subseção

                                                        List<String> arquivos = Arrays.asList(item.list());
                                                        arquivos.forEach(arquivoNome -> {
                                                            String nomeArquivoBloco = (item.getPath() + "/"
                                                                    + arquivoNome).replaceAll("Rev/" + rev + "/Edição",
                                                                            "Master");

                                                            arquivoRepository.findByNomeArquivoLike(nomeArquivoBloco)
                                                                    .ifPresentOrElse((arquivo) -> {
                                                                        blocoRepository.findByArquivo(arquivo)
                                                                                .ifPresentOrElse((bloco) -> {
                                                                                    Arquivo arquivoBlocoRev = Arquivo
                                                                                            .builder()
                                                                                            .nomeArquivo(item.getPath()
                                                                                                    + "/" + arquivoNome)
                                                                                            .build();
                                                                                    BlocoRevisao blocoRevisao = BlocoRevisao
                                                                                            .builder()
                                                                                            .arquivo(arquivoBlocoRev)
                                                                                            .bloco(bloco)
                                                                                            .revisao(revisao).build();

                                                                                    blocoRevisaoRepository
                                                                                            .save(blocoRevisao);
                                                                                }, () -> {

                                                                                });
                                                                    }, () -> {

                                                                    });
                                                        });
                                                    } else {
                                                        String nomeArquivoBloco = (item.getPath())
                                                                .replaceAll("Rev/" + rev + "/Edição", "Master");
                                                        arquivoRepository.findByNomeArquivoLike(nomeArquivoBloco)
                                                                .ifPresentOrElse((arquivo) -> {
                                                                    blocoRepository.findByArquivo(arquivo)
                                                                            .ifPresentOrElse((bloco) -> {
                                                                                Arquivo arquivoBlocoRev = Arquivo
                                                                                        .builder()
                                                                                        .nomeArquivo(item.getPath())
                                                                                        .build();
                                                                                BlocoRevisao blocoRevisao = BlocoRevisao
                                                                                        .builder()
                                                                                        .arquivo(arquivoBlocoRev)
                                                                                        .bloco(bloco).revisao(revisao)
                                                                                        .build();

                                                                                blocoRevisaoRepository
                                                                                        .save(blocoRevisao);
                                                                            }, () -> {

                                                                            });
                                                                }, () -> {

                                                                });
                                                    }
                                                });

                                            });

                                        }, () -> {

                                        });
                            });
                        }
                    }
                }
            });
        }, () -> {
            throw new ManualNotFoundException("Manual não encontrado.");
        });
    }

    public List<Revisao> getRevisoesByCodManual(Integer codManual) {
        List<Revisao> revisoes = new ArrayList<Revisao>();
        manualRepository.findById(codManual).ifPresentOrElse((manual) -> {
            revisoes.addAll(revisaoRepository.findByManual(manual));
        }, () -> {
            throw new ManualNotFoundException("Manual não encontrado.");
        });
        return revisoes;
    }

    public void gerarDocumentoDelta(Integer codManual, Integer codRevisao, Integer traco) {
        manualRepository.findById(codManual).ifPresentOrElse((manual) -> {
            revisaoRepository.findById(codRevisao).ifPresentOrElse((revisao) -> {
                String caminhoDeltaTemp = raiz.getPath() + "/" + manual.getNome() + "-" + manual.getPartNumber()
                        + "/Rev/" + revisao.getNomeRevisao() + "/" + manual.getNome() + "-" + manual.getPartNumber()
                        + "-" + traco.toString() + "-" + revisao.getNomeRevisao().toUpperCase() + "-" + "DELTA.pdf";
                PDDocument documentoDelta = new PDDocument();

                List<ModificacaoBloco> modificacaoBlocos = new ArrayList<ModificacaoBloco>();
                blocoRevisaoRepository.findByRevisaoOrderByBloco(revisao).forEach(blocoRevisao -> {
                    List<Integer> tracos = new ArrayList<Integer>();
                    blocoRevisao.getBloco().getTracos().forEach(t -> {
                        tracos.add(t.getTraco());
                    });
                    if (tracos.contains(traco)) {
                        try {
                            File arquivoRev = new File(blocoRevisao.getArquivo().getNomeArquivo());
                            File arquivoMaster = new File(blocoRevisao.getBloco().getArquivo().getNomeArquivo());
                            RandomAccessBufferedFileInputStream aRev = new RandomAccessBufferedFileInputStream(
                                    arquivoRev);
                            RandomAccessBufferedFileInputStream aMaster = new RandomAccessBufferedFileInputStream(
                                    arquivoMaster);

                            PDFParser parserRev = new PDFParser(aRev);
                            PDFParser parserMaster = new PDFParser(aMaster);
                            parserRev.parse();
                            parserMaster.parse();

                            COSDocument cosDocRev = parserRev.getDocument();
                            COSDocument cosDocMaster = parserMaster.getDocument();

                            PDFTextStripper pdfStripperRev = new PDFTextStripper();
                            PDFTextStripper pdfStripperMaster = new PDFTextStripper();

                            PDDocument pdDocRev = new PDDocument(cosDocRev);
                            PDDocument pdDocMaster = new PDDocument(cosDocMaster);

                            for (int i = 1; i <= pdDocMaster.getNumberOfPages(); i++) {
                                pdfStripperRev.setStartPage(i);
                                pdfStripperRev.setEndPage(i);

                                pdfStripperMaster.setStartPage(i);
                                pdfStripperMaster.setEndPage(i);

                                String parsedTextRev = pdfStripperRev.getText(pdDocRev);
                                String parsedTextMaster = pdfStripperMaster.getText(pdDocMaster);
                                if (parsedTextMaster.equals(parsedTextRev)) {

                                    modificacaoBlocos.add(ModificacaoBloco.builder().blocoRevisao(blocoRevisao)
                                            .paginaBloco(String.valueOf(i))
                                            .revisaoNome("REVISION 0" + revisao.getNomeRevisao().split("Rev")[1])
                                            .build());
                                } else {
                                    if (pdDocRev.getNumberOfPages() < pdDocMaster.getNumberOfPages()) {
                                        if (i > pdDocRev.getNumberOfPages()) {
                                            modificacaoBlocos.add(ModificacaoBloco.builder().blocoRevisao(blocoRevisao)
                                                    .paginaBloco(String.valueOf(i))
                                                    .revisaoNome(
                                                            "REVISION 0" + revisao.getNomeRevisao().split("Rev")[1])
                                                    .operacao("* del").build());
                                        } else {
                                            modificacaoBlocos.add(ModificacaoBloco.builder().blocoRevisao(blocoRevisao)
                                                    .paginaBloco(String.valueOf(i))
                                                    .revisaoNome(
                                                            "REVISION 0" + revisao.getNomeRevisao().split("Rev")[1])
                                                    .operacao("*").build());
                                        }
                                    } else {
                                        modificacaoBlocos.add(ModificacaoBloco.builder().blocoRevisao(blocoRevisao)
                                                .paginaBloco(String.valueOf(i))
                                                .revisaoNome("REVISION 0" + revisao.getNomeRevisao().split("Rev")[1])
                                                .operacao("*").build());
                                    }

                                }

                            }
                            if (pdDocRev.getNumberOfPages() > pdDocMaster.getNumberOfPages()) {
                                for (int i = pdDocMaster.getNumberOfPages() + 1; i <= pdDocRev
                                        .getNumberOfPages(); i++) {
                                    modificacaoBlocos.add(ModificacaoBloco.builder().blocoRevisao(blocoRevisao)
                                            .paginaBloco(String.valueOf(i))
                                            .revisaoNome("REVISION 0" + revisao.getNomeRevisao().split("Rev")[1])
                                            .operacao("* new").build());
                                }
                            }
                            pdDocRev.close();
                            pdDocMaster.close();
                        } catch (Exception e) {
                        }
                    }

                });
                List<PDDocument> documents = new ArrayList<PDDocument>();

                modificacaoBlocos.forEach(m -> {
                    System.out.println(m.getBlocoRevisao().getBloco().getCodBloco() + "|"
                            + m.getBlocoRevisao().getBloco().getNomeBloco() + "|"
                            + m.getBlocoRevisao().getBloco().getCodBlocoCodelist() + "|" + m.getPaginaBloco() + "|"
                            + m.getOperacao() + "|" + m.getRevisaoNome());
                    if (m.getOperacao() != null) {
                        if (!m.getOperacao().equals("* del")) {
                            try {
                                if (documentoDelta.getNumberOfPages() == 1 || documentoDelta.getNumberOfPages() == 3) {
                                    PDPage pagina = new PDPage();
                                    PDRectangle size = documentoDelta.getPage(0).getMediaBox();
                                    pagina.setMediaBox(size);
                                    documentoDelta.addPage(pagina);
                                }
                                // Pega a pagina
                                Integer numeroPagina = Integer.valueOf(m.getPaginaBloco()) - 1;
                                File file = new File(m.getBlocoRevisao().getArquivo().getNomeArquivo());
                                PDDocument document = PDDocument.load(file);

                                PDPage pagina = document.getPage(numeroPagina);
                                documentoDelta.addPage(pagina);
                                documents.add(document);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                });
                try {
                    documentoDelta.save(caminhoDeltaTemp);
                    documents.forEach(d -> {
                        try {
                            d.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    documentoDelta.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, () -> {

            });
        }, () -> {
            throw new ManualNotFoundException("Manual não encontrado.");
        });
    }

    public List<Traco> getTracosByCodManual(Integer codManual) {
        List<Traco> tracos = new ArrayList<Traco>();
        manualRepository.findById(codManual).ifPresentOrElse((manual) -> {
            tracos.addAll(tracoRepository.findByManual(manual));
        }, () -> {
            throw new ManualNotFoundException("Manual não encontrado.");
        });
        return tracos;
    }

    public ArquivoDeltaDTO getManualDelta(Integer codManual, Integer traco, Integer codRevisao) {
        ArquivoDeltaDTO deltaDTO = new ArquivoDeltaDTO();
        manualRepository.findById(codManual).ifPresentOrElse((manual) -> {
            revisaoRepository.findById(codRevisao).ifPresentOrElse((revisao) -> {
                try {
                    String nomeManual = manual.getNome() + "-" + manual.getPartNumber();
                    String pathDelta = raiz.getPath() + "/" + nomeManual + "/" + "Rev/" + revisao.getNomeRevisao() + "/"
                            + nomeManual + "-" + traco + "-" + revisao.getNomeRevisao().toUpperCase() + "-DELTA.pdf";
                    if (!new File(pathDelta).exists()) {
                        gerarDocumentoDelta(manual.getCodManual(), revisao.getCodRevisao(), traco);
                    }
                    File arquivoDelta = new File(pathDelta);
                    InputStream obj = new FileInputStream(arquivoDelta);
                    byte[] content = IOUtils.toByteArray(obj);
                    obj.close();
                    deltaDTO.setConteudo(content);
                    deltaDTO.setNomeArquivo(arquivoDelta.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, () -> {

            });
        }, () -> {
            throw new ManualNotFoundException("Manual não encontrado.");
        });
        return deltaDTO;

    }
    public void atualizarRev(Integer codManual) {
        cadRevisao(codManual);

    }

    public Traco getTracoById(Integer codTraco){
        return tracoRepository.findById(codTraco).map((traco) -> {
            return traco;
        }).orElseThrow(() -> new ManualNotFoundException("Traco não encontrado"));
    }

    public void updateTraco(String nome, Integer codTraco){
        tracoRepository.findById(codTraco).map((t) -> {
            t.setNome(nome);
            return tracoRepository.save(t);
        }).orElseThrow(() -> new ManualNotFoundException("Traco não encontrado"));
    }
    public ArquivoFullDTO getManualFull(Integer codManual, Integer traco, String revType) {
        ArquivoFullDTO fullDTO = new ArquivoFullDTO();
        manualRepository.findById(codManual).ifPresentOrElse((manual) -> {
            if(revType.equals("master")){
                try {
                    String nomeManual = manual.getNome() + "-" + manual.getPartNumber();
                    String pathFull = raiz.getPath() + "/" + nomeManual + "/" + "Master/"+nomeManual + "-" + traco +"-FULL.pdf";
                    if (!new File(pathFull).exists()) {
                        //gerarDocumentoDelta(manual.getCodManual(), revisao.getCodRevisao(), traco);
                        gerarDocumentoFullMaster(manual.getCodManual(),traco);
                    }
                    File arquivoFull = new File(pathFull);
                    InputStream obj = new FileInputStream(arquivoFull);
                    byte[] content = IOUtils.toByteArray(obj);
                    obj.close();
                    fullDTO.setConteudo(content);
                    fullDTO.setNomeArquivo(arquivoFull.getName());
                } catch (Exception e) {
                    e.printStackTrace();

                }
            } else {
                Integer codRevisao = Integer.valueOf(revType);
                revisaoRepository.findById(codRevisao).ifPresentOrElse((revisao) -> {
                    try {
                        String nomeManual = manual.getNome() + "-" + manual.getPartNumber();
                        String pathFull = raiz.getPath() + "/" + nomeManual + "/" + "Rev/" + revisao.getNomeRevisao() + "/"
                                + nomeManual + "-" + traco + "-" + revisao.getNomeRevisao().toUpperCase() + "-FULL.pdf";
                        if (!new File(pathFull).exists()) {
                            gerarDocumentoFullRev(manual.getCodManual(), revisao.getCodRevisao(), traco);
                            //gerarDocumentoFullRev
                        }
                        File arquivoFull = new File(pathFull);
                        InputStream obj = new FileInputStream(arquivoFull);
                        byte[] content = IOUtils.toByteArray(obj);
                        obj.close();
                        fullDTO.setConteudo(content);
                        fullDTO.setNomeArquivo(arquivoFull.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, () -> {
    
                });
            }
            
        }, () -> {
            throw new ManualNotFoundException("Manual não encontrado.");
        });
        return fullDTO;

    }

    public void gerarDocumentoFullMaster(Integer codManual, Integer traco) {
        manualRepository.findById(codManual).ifPresentOrElse((manual) -> {
                String caminhoFullTemp = raiz.getPath() + "/" + manual.getNome() + "-" + manual.getPartNumber()
                        + "/Master/"+ manual.getNome() + "-" + manual.getPartNumber()+ "-" + traco.toString() + "-" 
                        +"FULL.pdf";
                PDDocument documentoFull = new PDDocument();
                List<PDDocument> documents = new ArrayList<PDDocument>();

                manual.getSecoes().forEach(secao -> {
                    if(secao.getSubSecoes().size()>0){
                        secao.getSubSecoes().forEach(subSecao -> {
                            subSecao.getBlocos().forEach(bloco -> {
                                List<Integer> tracos = new ArrayList<Integer>();
                                bloco.getTracos().forEach(t -> {
                                    tracos.add(t.getTraco());
                                });
                                if (tracos.contains(traco)) {
                                    try {
                                        File arquivoMaster = new File(bloco.getArquivo().getNomeArquivo());
                                        RandomAccessBufferedFileInputStream aMaster = new RandomAccessBufferedFileInputStream(arquivoMaster);
                                    
                                        PDFParser parserMaster = new PDFParser(aMaster);
                                        parserMaster.parse();
                                        PDDocument pdDocMaster = PDDocument.load(arquivoMaster);
                                        
                                        for (int i = 1; i <= pdDocMaster.getNumberOfPages(); i++) {

                                            Integer numeroPagina = Integer.valueOf(i) - 1;
                                            PDPage pagina = pdDocMaster.getPage(numeroPagina);
                                            documentoFull.addPage(pagina);   
                                            documents.add(pdDocMaster);                                   

                                        }
                                    } catch (Exception e) {
                                    }
                                } 
                            });
                        });
                    } else {
                        secao.getBlocos().forEach(bloco -> {
                            List<Integer> tracos = new ArrayList<Integer>();
                            bloco.getTracos().forEach(t -> {
                                tracos.add(t.getTraco());
                            });
                            if (tracos.contains(traco)) {
                                try {
                                    File arquivoMaster = new File(bloco.getArquivo().getNomeArquivo());
                                    RandomAccessBufferedFileInputStream aMaster = new RandomAccessBufferedFileInputStream(arquivoMaster);
                                    
                                    PDFParser parserMaster = new PDFParser(aMaster);
                                    parserMaster.parse();

                                    PDDocument pdDocMaster = PDDocument.load(arquivoMaster);
                                    
                                    for (int i = 1; i <= pdDocMaster.getNumberOfPages(); i++) {
                                        Integer numeroPagina = Integer.valueOf(i) - 1;
                                        PDPage pagina = pdDocMaster.getPage(numeroPagina);
                                        documentoFull.addPage(pagina);     
                                        documents.add(pdDocMaster);                                   
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } 
                        });
                    }
                });
                try {
                    documentoFull.removePage(1);
                    documentoFull.removePage(0);
                    documentoFull.save(caminhoFullTemp);
                    documentoFull.close();
                    documents.forEach(d -> {
                        try {
                            d.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }, () -> {
            throw new ManualNotFoundException("Manual não encontrado.");
        });
    }
    public void gerarDocumentoFullRev(Integer codManual, Integer codRevisao, Integer traco) {
        manualRepository.findById(codManual).ifPresentOrElse((manual) -> {
            revisaoRepository.findById(codRevisao).ifPresentOrElse((revisao) -> {
                List<PDDocument> documents = new ArrayList<PDDocument>();

                String caminhoFullTemp = raiz.getPath() + "/" + manual.getNome() + "-" + manual.getPartNumber()
                        + "/Rev/" + revisao.getNomeRevisao() + "/" + manual.getNome() + "-" + manual.getPartNumber()
                        + "-" + traco.toString() + "-" + revisao.getNomeRevisao().toUpperCase() + "-" + "FULL.pdf";
                PDDocument documentoFull = new PDDocument();
                manual.getSecoes().forEach(secao -> {
                    if(secao.getSubSecoes().size()>0){

                        secao.getSubSecoes().forEach(subSecao -> {
                            subSecao.getBlocos().forEach(bloco -> {
                                List<BlocoRevisao> blocosRevisao = blocoRevisaoRepository.findByRevisaoAndBloco(revisao,bloco);
                                if(blocosRevisao.size()>0){
                                    blocosRevisao.forEach(blocoRevisao -> {
                                        List<Integer> tracos = new ArrayList<Integer>();
                                        blocoRevisao.getBloco().getTracos().forEach(t -> {
                                            tracos.add(t.getTraco());
                                        });
                                        if (tracos.contains(traco)) {
                                            try {
                                                File arquivoRev = new File(blocoRevisao.getArquivo().getNomeArquivo());
                                                File arquivoMaster = new File(bloco.getArquivo().getNomeArquivo());
                                                RandomAccessBufferedFileInputStream aRev = new RandomAccessBufferedFileInputStream(
                                                        arquivoRev);
                                                RandomAccessBufferedFileInputStream aMaster = new RandomAccessBufferedFileInputStream(
                                                        arquivoMaster);
                    
                                                PDFParser parserRev = new PDFParser(aRev);
                                                PDFParser parserMaster = new PDFParser(aMaster);
                                                parserRev.parse();
                                                parserMaster.parse();
                    
                                                COSDocument cosDocRev = parserRev.getDocument();
                                                COSDocument cosDocMaster = parserMaster.getDocument();
                    
                                                PDFTextStripper pdfStripperRev = new PDFTextStripper();
                                                PDFTextStripper pdfStripperMaster = new PDFTextStripper();
                    
                                                PDDocument pdDocRev = new PDDocument(cosDocRev);
                                                PDDocument pdDocMaster = new PDDocument(cosDocMaster);
                                                
                                                pdfStripperRev.setStartPage(1);
                                                pdfStripperRev.setEndPage(pdDocRev.getNumberOfPages());
                    
                                                pdfStripperMaster.setStartPage(1);
                                                pdfStripperMaster.setEndPage(pdDocMaster.getNumberOfPages());
                    
                                                String parsedTextRev = pdfStripperRev.getText(pdDocRev);
                                                String parsedTextMaster = pdfStripperMaster.getText(pdDocMaster);
                    
                                                if (parsedTextMaster.equals(parsedTextRev)) {
                                                    for (int i = 1; i <= pdDocMaster.getNumberOfPages(); i++) {
                                                        Integer numeroPagina = Integer.valueOf(i) - 1;
                                                        PDPage pagina = pdDocMaster.getPage(numeroPagina);
                                                        documentoFull.addPage(pagina);     
                                                        documents.add(pdDocMaster); 
                                                        documents.add(pdDocRev); 

                                                    }
                                                } else {
                                                    for (int i = 1; i <= pdDocRev.getNumberOfPages(); i++) {
                                                        Integer numeroPagina = Integer.valueOf(i) - 1;
                                                        PDPage pagina = pdDocRev.getPage(numeroPagina);
                                                        documentoFull.addPage(pagina);     
                                                        documents.add(pdDocRev); 
                                                        documents.add(pdDocMaster); 

                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                    
                                    });
                                } else {
                                    List<Integer> tracos = new ArrayList<Integer>();
                                    bloco.getTracos().forEach(t -> {
                                        tracos.add(t.getTraco());
                                    });
                                    if (tracos.contains(traco)) {
                                        try {
                                            File arquivoMaster = new File(bloco.getArquivo().getNomeArquivo());
                
                                            PDDocument pdDocMaster = PDDocument.load(arquivoMaster);
                
                                                for (int i = 1; i <= pdDocMaster.getNumberOfPages(); i++) {
                                                    Integer numeroPagina = Integer.valueOf(i) - 1;
                                                    PDPage pagina = pdDocMaster.getPage(numeroPagina);
                                                    documentoFull.addPage(pagina);     
                                                    documents.add(pdDocMaster); 
                                                }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                
                            });
                        });
                        
                    } else {
                        secao.getBlocos().forEach(bloco -> {
                            List<BlocoRevisao> blocosRevisao = blocoRevisaoRepository.findByRevisaoAndBloco(revisao,bloco);
                            if(blocosRevisao.size()>0){
                                blocosRevisao.forEach(blocoRevisao -> {
                                    List<Integer> tracos = new ArrayList<Integer>();
                                    blocoRevisao.getBloco().getTracos().forEach(t -> {
                                        tracos.add(t.getTraco());
                                    });
                                    if (tracos.contains(traco)) {
                                        try {
                                            File arquivoRev = new File(blocoRevisao.getArquivo().getNomeArquivo());
                                            File arquivoMaster = new File(bloco.getArquivo().getNomeArquivo());
                                            RandomAccessBufferedFileInputStream aRev = new RandomAccessBufferedFileInputStream(
                                                    arquivoRev);
                                            RandomAccessBufferedFileInputStream aMaster = new RandomAccessBufferedFileInputStream(
                                                    arquivoMaster);
                
                                            PDFParser parserRev = new PDFParser(aRev);
                                            PDFParser parserMaster = new PDFParser(aMaster);
                                            parserRev.parse();
                                            parserMaster.parse();
                
                                            COSDocument cosDocRev = parserRev.getDocument();
                                            COSDocument cosDocMaster = parserMaster.getDocument();
                
                                            PDFTextStripper pdfStripperRev = new PDFTextStripper();
                                            PDFTextStripper pdfStripperMaster = new PDFTextStripper();
                
                                            PDDocument pdDocRev = new PDDocument(cosDocRev);
                                            PDDocument pdDocMaster = new PDDocument(cosDocMaster);
                                            
                                            pdfStripperRev.setStartPage(1);
                                            pdfStripperRev.setEndPage(pdDocRev.getNumberOfPages());
                
                                            pdfStripperMaster.setStartPage(1);
                                            pdfStripperMaster.setEndPage(pdDocMaster.getNumberOfPages());
                
                                            String parsedTextRev = pdfStripperRev.getText(pdDocRev);
                                            String parsedTextMaster = pdfStripperMaster.getText(pdDocMaster);
                
                                            if (parsedTextMaster.equals(parsedTextRev)) {
                                                for (int i = 1; i <= pdDocMaster.getNumberOfPages(); i++) {
                                                    Integer numeroPagina = Integer.valueOf(i) - 1;
                                                    PDPage pagina = pdDocMaster.getPage(numeroPagina);
                                                    documentoFull.addPage(pagina);     
                                                    documents.add(pdDocMaster); 
                                                }
                                            } else {
                                                for (int i = 1; i <= pdDocRev.getNumberOfPages(); i++) {
                                                    Integer numeroPagina = Integer.valueOf(i) - 1;
                                                    PDPage pagina = pdDocRev.getPage(numeroPagina);
                                                    documentoFull.addPage(pagina);     
                                                    documents.add(pdDocRev); 
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                
                                });
                            } else {
                                List<Integer> tracos = new ArrayList<Integer>();
                                bloco.getTracos().forEach(t -> {
                                    tracos.add(t.getTraco());
                                });
                                if (tracos.contains(traco)) {
                                    try {
                                        File arquivoMaster = new File(bloco.getArquivo().getNomeArquivo());
            
                                        PDDocument pdDocMaster = PDDocument.load(arquivoMaster);
            
                                            for (int i = 1; i <= pdDocMaster.getNumberOfPages(); i++) {
                                                Integer numeroPagina = Integer.valueOf(i) - 1;
                                                PDPage pagina = pdDocMaster.getPage(numeroPagina);
                                                documentoFull.addPage(pagina);     
                                                documents.add(pdDocMaster); 
                                            }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
                

                try {
                    documentoFull.removePage(1);
                    documentoFull.removePage(0);
                    documentoFull.save(caminhoFullTemp);
                    documentoFull.close();
                    documents.forEach(d -> {
                        try {
                            d.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }, () -> {

            });
        }, () -> {
            throw new ManualNotFoundException("Manual não encontrado.");
        });
    }

    public ArquivoLepDTO gerarLep(Integer codManual, Integer traco, Integer codRevisao) {
        ArquivoLepDTO lepDTO = new ArquivoLepDTO();
        manualRepository.findById(codManual).ifPresentOrElse((manual) -> {

            revisaoRepository.findById(codRevisao).ifPresentOrElse((revisao) -> {

                List<ModificacaoBloco> modificacaoBlocos = new ArrayList<ModificacaoBloco>();
                blocoRevisaoRepository.findByRevisaoOrderByBloco(revisao).forEach(blocoRevisao -> {
                    
                    List<Integer> tracos = new ArrayList<Integer>();
                    blocoRevisao.getBloco().getTracos().forEach(t -> {
                        tracos.add(t.getTraco());
                    });
                    if (tracos.contains(traco)) {
                        try {
                            File arquivoRev = new File(blocoRevisao.getArquivo().getNomeArquivo());
                            File arquivoMaster = new File(blocoRevisao.getBloco().getArquivo().getNomeArquivo());
                            RandomAccessBufferedFileInputStream aRev = new RandomAccessBufferedFileInputStream(
                                    arquivoRev);
                            RandomAccessBufferedFileInputStream aMaster = new RandomAccessBufferedFileInputStream(
                                    arquivoMaster);

                            PDFParser parserRev = new PDFParser(aRev);
                            PDFParser parserMaster = new PDFParser(aMaster);
                            parserRev.parse();
                            parserMaster.parse();

                            COSDocument cosDocRev = parserRev.getDocument();
                            COSDocument cosDocMaster = parserMaster.getDocument();

                            PDFTextStripper pdfStripperRev = new PDFTextStripper();
                            PDFTextStripper pdfStripperMaster = new PDFTextStripper();

                            PDDocument pdDocRev = new PDDocument(cosDocRev);
                            PDDocument pdDocMaster = new PDDocument(cosDocMaster);

                            for (int i = 1; i <= pdDocMaster.getNumberOfPages(); i++) {
                                pdfStripperRev.setStartPage(i);
                                pdfStripperRev.setEndPage(i);

                                pdfStripperMaster.setStartPage(i);
                                pdfStripperMaster.setEndPage(i);

                                String parsedTextRev = pdfStripperRev.getText(pdDocRev);
                                String parsedTextMaster = pdfStripperMaster.getText(pdDocMaster);
                                if (parsedTextMaster.equals(parsedTextRev)) {

                                    modificacaoBlocos.add(ModificacaoBloco.builder().blocoRevisao(blocoRevisao)
                                            .paginaBloco(String.valueOf(i))
                                            .revisaoNome("REVISION 0" + revisao.getNomeRevisao().split("Rev")[1])
                                            .build());
                                } else {
                                    if (pdDocRev.getNumberOfPages() < pdDocMaster.getNumberOfPages()) {
                                        if (i > pdDocRev.getNumberOfPages()) {
                                            modificacaoBlocos.add(ModificacaoBloco.builder().blocoRevisao(blocoRevisao)
                                                    .paginaBloco(String.valueOf(i))
                                                    .revisaoNome(
                                                            "REVISION 0" + revisao.getNomeRevisao().split("Rev")[1])
                                                    .operacao("* del").build());
                                        } else {
                                            modificacaoBlocos.add(ModificacaoBloco.builder().blocoRevisao(blocoRevisao)
                                                    .paginaBloco(String.valueOf(i))
                                                    .revisaoNome(
                                                            "REVISION 0" + revisao.getNomeRevisao().split("Rev")[1])
                                                    .operacao("*").build());
                                        }
                                    } else {
                                        modificacaoBlocos.add(ModificacaoBloco.builder().blocoRevisao(blocoRevisao)
                                                .paginaBloco(String.valueOf(i))
                                                .revisaoNome("REVISION 0" + revisao.getNomeRevisao().split("Rev")[1])
                                                .operacao("*").build());
                                    }

                                }

                            }
                            if (pdDocRev.getNumberOfPages() > pdDocMaster.getNumberOfPages()) {
                                for (int i = pdDocMaster.getNumberOfPages() + 1; i <= pdDocRev
                                        .getNumberOfPages(); i++) {
                                    modificacaoBlocos.add(ModificacaoBloco.builder().blocoRevisao(blocoRevisao)
                                            .paginaBloco(String.valueOf(i))
                                            .revisaoNome("REVISION 0" + revisao.getNomeRevisao().split("Rev")[1])
                                            .operacao("* new").build());
                                }
                            }
                            pdDocRev.close();
                            pdDocMaster.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
                List<PDDocument> documents = new ArrayList<PDDocument>();
                String caminhoLepTemp = raiz.getPath() + "/" + manual.getNome() + "-" + manual.getPartNumber()
                + "/Rev/" + revisao.getNomeRevisao() + "/" + manual.getNome() + "-" + manual.getPartNumber()
                + "-" + traco.toString() + "-" + revisao.getNomeRevisao().toUpperCase() + "-" + "LEP.txt";
                try {
                    FileWriter arqLep = new FileWriter(caminhoLepTemp);
                    PrintWriter gravarArqLep = new PrintWriter(arqLep);
                    gravarArqLep.printf("Código | Nome | Página | Operação | Revisão%n");

                    modificacaoBlocos.forEach(m -> {
                        String linhaLep = m.getBlocoRevisao().getBloco().getCodBlocoCodelist() + "|"
                        + m.getBlocoRevisao().getBloco().getNomeBloco() + "|"
                        + m.getPaginaBloco() + "|" + m.getOperacao() + "|" + m.getRevisaoNome();
                        if(m.getOperacao()!= null){
                            gravarArqLep.printf(linhaLep+"%n");

                        }
                    });
                    arqLep.close();
                    File arquivoLep = new File(caminhoLepTemp);
                    InputStream obj = new FileInputStream(arquivoLep);
                    byte[] content = IOUtils.toByteArray(obj);
                    obj.close();
                    lepDTO.setConteudo(content);
                    lepDTO.setNomeArquivo(arquivoLep.getName());
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                documents.forEach(d -> {
                    try {
                        d.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }, () -> {

            });

        }, () -> {
            throw new ManualNotFoundException("Manual não encontrado.");
        });
        return lepDTO;

    }

}
