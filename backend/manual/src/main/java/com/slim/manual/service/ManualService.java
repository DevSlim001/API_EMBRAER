package com.slim.manual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.Cleanup;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import com.slim.manual.domain.model.Codelist;
import com.slim.manual.domain.model.CodelistLinha;
import com.slim.manual.domain.model.Manual;
import com.slim.manual.domain.model.Secao;
import com.slim.manual.domain.model.SubSecao;
import com.slim.manual.domain.model.Traco;
import com.slim.manual.domain.repository.BlocoRepository;
import com.slim.manual.domain.repository.ManualRepository;
import com.slim.manual.domain.repository.TracoRepository;
import com.slim.manual.exception.ManualNotFoundException;
import com.slim.manual.exception.UploadArquivoBlocoException;
import com.slim.manual.exception.UploadCodelistException;
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
        /*
         * return diretorios.map((diretorio) -> { boolean isPresent =
         * diretorio.split(" ")[0].equals(numberSecao); return
         * diretorio.split(" ")[0].equals(numberSecao) ? diretorio : "false"; });
         */
        File raizDoc = new File(path);
        List<String> diretorios = new ArrayList<String>(Arrays.asList(raizDoc.list()));
        Iterator<String> diretoriosIterator = diretorios.iterator();
        String res = "" ;
        while (diretoriosIterator.hasNext()) {
            String diretorio = diretoriosIterator.next();
            boolean isPresent = diretorio.split(" ")[0].equals(numberSecao);
            if(isPresent){
                res = diretorio;
                break;
            } else {
                res = "false";
            }
            /* System.out.println(res); */

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
                                                .nome(tracoCelula.get(1)).build();
                                        tracos.add(tracoRepository.save(traco));
                                    }
                                });
                            } else {
                                String[] filePath = { "" };

                                // Começo seção
                                Secao secao = new Secao();
                                secao.setNumSecao(celulas.get(0).toString());
                                // Fim seção
                                // /+"/(numSecao)(vai precisar arrumar algum jeito de identificar pq eu nao
                                // salvo o nome da
                                // secao)/(numBloco+nomeBloco)/(nomeManual+pn)-(numSecao)-(numBloco)c(codBlocoCodelist)
                                filePath[0] = diretorioManualMaster.getPath() + "/";
                                String findDiretorioSecao = findDiretorio(filePath[0], secao.getNumSecao());
                                if (!findDiretorioSecao.equals("false")) {
                                    filePath[0] = filePath[0].concat(findDiretorioSecao + "/");
                                }

                                // Começo sub-seção
                                SubSecao subSecao = new SubSecao();
                                subSecao.setNumSubSecao(celulas.get(1).toString());
                                // Fim sub-seção

                                // Começo bloco
                                Bloco bloco;

                                if(!subSecao.getNumSubSecao().equals("")){
                                    String findDiretorioSubSecao = findDiretorio(filePath[0], subSecao.getNumSubSecao());
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

                                bloco = Bloco.builder().numBloco(celulas.get(2).toString())
                                        .nomeBloco(celulas.get(3).toString())
                                        .codBlocoCodelist(celulas.get(4).toString()).tracos(tracosBloco).build();
                                String findDiretorioBloco= findDiretorio(filePath[0], bloco.getNumBloco());
                                if (!findDiretorioBloco.equals("false")) {
                                    filePath[0] = filePath[0].concat(findDiretorioBloco + "/");
                                }
                                if(subSecao.getNumSubSecao().equals("")){
                                    filePath[0] = filePath[0].concat(manualPath + "-" + secao.getNumSecao()+"-"+bloco.getNumBloco()+"c"+bloco.getCodBlocoCodelist()+".pdf");
                                } else {
                                    filePath[0] = filePath[0].concat(manualPath + "-" + secao.getNumSecao()+"-"+subSecao.getNumSubSecao()+"-"+bloco.getNumBloco()+"c"+bloco.getCodBlocoCodelist()+".pdf");
                                }
                                File fileBloco = new File(filePath[0]);
                                if(fileBloco.exists()){
                                    bloco.setArquivo(Arquivo.builder().nomeArquivo(filePath[0]).build());
                                }
                                // Fim bloco
                                if (secoes.size() == 0) {
                                    ultimoNumSecao[0] = secao.getNumSecao();
                                    if (!subSecao.getNumSubSecao().equals("")) {
                                        /* File diretorio = new File(filePath[0]);
                                        String findDiretorioSubSecao = findDiretorio(Arrays.asList(diretorio.list()),
                                                subSecao.getNumSubSecao());
                                        if (!findDiretorioSubSecao.equals("false")) {
                                            filePath[0] = filePath[0].concat(findDiretorioSubSecao + "/");
                                        } */

                                        /* File diretorioBlocos = new File(filePath[0]);
                                        String findDiretorioBloco = findDiretorio(Arrays.asList(diretorioBlocos.list()),
                                                bloco.getCodBlocoCodelist());
                                        if (!findDiretorioBloco.equals("false")) {
                                            filePath[0] = filePath[0].concat(findDiretorioBloco + "/");
                                        } */

                                        ultimoNumSubSecao[0] = subSecao.getNumSubSecao();
                                        subSecao.getBlocos().add(bloco);
                                        
                                        secao.getSubSecoes().add(subSecao);
                                        secoes.add(secao);
                                    } else {

                                        /* File diretorioBlocos = new File(filePath[0]);
                                        String findDiretorioBloco = findDiretorio(Arrays.asList(diretorioBlocos.list()),
                                                bloco.getCodBlocoCodelist());
                                        if (!findDiretorioBloco.equals("false")) {
                                            filePath[0] = filePath[0].concat(findDiretorioBloco + "/");
                                        } */
                                        secao.getBlocos().add(bloco);
                                        

                                        secoes.add(secao);
                                    }
                                } else {
                                    if (!secao.getNumSecao().equals(ultimoNumSecao[0])) {
                                        ultimoNumSecao[0] = secao.getNumSecao();
                                        ultimoNumSubSecao[0] = "";
                                        if (!subSecao.getNumSubSecao().equals("")) {
                                            /* File diretorio = new File(filePath[0]);

                                            String findDiretorioSubSecao = findDiretorio(
                                                    Arrays.asList(diretorio.list()), subSecao.getNumSubSecao());
                                            if (!findDiretorioSubSecao.equals("false")) {
                                                filePath[0] = filePath[0].concat(findDiretorioSubSecao + "/");
                                            }

                                            File diretorioBlocos = new File(filePath[0]);
                                            String findDiretorioBloco = findDiretorio(
                                                    Arrays.asList(diretorioBlocos.list()), bloco.getCodBlocoCodelist());
                                            if (!findDiretorioBloco.equals("false")) {
                                                filePath[0] = filePath[0].concat(findDiretorioBloco + "/");
                                            } */
                                            ultimoNumSubSecao[0] = subSecao.getNumSubSecao();
                                            subSecao.getBlocos().add(bloco);
                                            secao.getSubSecoes().add(subSecao);
                                            secoes.add(secao);
                                        } else {

                                            /* File diretorioBlocos = new File(filePath[0]);
                                            String findDiretorioBloco = findDiretorio(
                                                    Arrays.asList(diretorioBlocos.list()), bloco.getCodBlocoCodelist());
                                            if (!findDiretorioBloco.equals("false")) {
                                                filePath[0] = filePath[0].concat(findDiretorioBloco + "/");
                                            } */
                                            secao.getBlocos().add(bloco);
                                            secoes.add(secao);
                                        }
                                    } else {
                                        Integer ultimoIndexSecao = secoes.size() - 1;
                                        Integer ultimoIndexSubSecao = secoes.get(ultimoIndexSecao).getSubSecoes().size()
                                                - 1;
                                        if (!subSecao.getNumSubSecao().equals("")) {
                                            /* File diretorio = new File(filePath[0]);
                                            String findDiretorioSubSecao = findDiretorio(
                                                    Arrays.asList(diretorio.list()), subSecao.getNumSubSecao());
                                            if (!findDiretorioSubSecao.equals("false")) {
                                                filePath[0] = filePath[0].concat(findDiretorioSubSecao + "/");
                                            } */

                                            /* File diretorioBlocos = new File(filePath[0]);
                                            String findDiretorioBloco = findDiretorio(
                                                    Arrays.asList(diretorioBlocos.list()), bloco.getCodBlocoCodelist());
                                            if (!findDiretorioBloco.equals("false")) {
                                                filePath[0] = filePath[0].concat(findDiretorioBloco + "/");
                                            } */
                                            if (!subSecao.getNumSubSecao().equals(ultimoNumSubSecao[0])) {
                                                ultimoNumSubSecao[0] = subSecao.getNumSubSecao();

                                                subSecao.getBlocos().add(bloco);
                                                secoes.get(ultimoIndexSecao).getSubSecoes().add(subSecao);
                                            } else {
                                                secoes.get(ultimoIndexSecao).getSubSecoes().get(ultimoIndexSubSecao)
                                                        .getBlocos().add(bloco);
                                            }
                                        } else {

                                            /* File diretorioBlocos = new File(filePath[0]);
                                            String findDiretorioBloco = findDiretorio(
                                                    Arrays.asList(diretorioBlocos.list()), bloco.getCodBlocoCodelist());
                                            if (!findDiretorioBloco.equals("false")) {
                                                filePath[0] = filePath[0].concat(findDiretorioBloco + "/");
                                            } */
                                            secoes.get(ultimoIndexSecao).getBlocos().add(bloco);
                                        }
                                    }
                                }
                                /* if(subSecao.getNumSubSecao().equals("")){
                                    filePath[0] = filePath[0].concat(manualPath + "-" + secao.getNumSecao()+"-"+bloco.getNumBloco()+"c"+bloco.getCodBlocoCodelist());

                                } else {
                                    filePath[0] = filePath[0].concat(manualPath + "-" + secao.getNumSecao()+"-"+subSecao.getNumSubSecao()+"-"+bloco.getNumBloco()+"c"+bloco.getCodBlocoCodelist());

                                } */
                                System.out.println(filePath[0]);

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

    public void importArquivoBloco(MultipartFile arquivo, Integer codBloco) throws IOException {
        blocoRepository.findById(codBloco).ifPresentOrElse((bloco) -> {
            try {
                if (arquivo != null && !arquivo.isEmpty()) {
                    /*
                     * String path = servletContext.getRealPath("/") + "resources/arquivos/" +
                     * arquivo.getOriginalFilename(); System.out.println(path); Path diretorioPath =
                     * Paths.get(path); Path arquivoPath =
                     * diretorioPath.resolve(arquivo.getOriginalFilename());
                     */
                    String filePath = "./Downloads/" + arquivo.getOriginalFilename();
                    try {
                        /*
                         * Files.createDirectories(diretorioPath);
                         * arquivo.transferTo(arquivoPath.toFile());
                         */
                        File file = new File(filePath);

                        FileOutputStream saida = new FileOutputStream(file);
                        copiar(arquivo.getInputStream(), saida);
                        Arquivo arquivoBloco = Arquivo.builder().nomeArquivo(arquivo.getOriginalFilename()).build();
                        bloco.setArquivo(arquivoBloco);
                        blocoRepository.save(bloco);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                new UploadArquivoBlocoException(e.getMessage());
            }

        }, () -> {
            throw new ManualNotFoundException("Bloco não encontrado.");
        });
    }

    private void copiar(InputStream origem, OutputStream destino) {
        int bite = 0;
        byte[] tamanhoMaximo = new byte[1024 * 100 * 1024]; // 8KB
        try {
            // enquanto bytes forem sendo lidos
            while ((bite = origem.read(tamanhoMaximo)) >= 0) {
                // pegue o byte lido e escreva no destino
                destino.write(tamanhoMaximo, 0, bite);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
    }
    /* ----------------------------------------------------------------------- */

    public String[] listDiretorioManuais() throws IOException {
        return raiz.list();

    }

    public void createDiretorioManual(ManualDTO manual) throws IOException {
        File diretorioManual = new File(raiz.getPath() + "/" + manual.getNome() + "-" + manual.getPartNumber());
        diretorioManual.mkdir();

    }

    public String[] listDiretorioSecao(String manual) throws IOException {
        System.out.println(raiz.getPath() + "/" + manual + "/Master/");
        File diretorioManual = new File(raiz.getPath() + "/" + manual + "/Master/");
        return diretorioManual.list();

    }
}
