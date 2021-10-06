package com.slim.manual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.Cleanup;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.slim.manual.domain.model.Bloco;
import com.slim.manual.domain.model.Codelist;
import com.slim.manual.domain.model.CodelistLinha;
import com.slim.manual.domain.model.Secao;
import com.slim.manual.domain.model.SubSecao;
import com.slim.manual.domain.model.Traco;
import com.slim.manual.domain.repository.ManualRepository;
import com.slim.manual.domain.repository.TracoRepository;

@Service
public class CodelistService {

    @Autowired
    private TracoRepository tracoRepository;

    @Autowired
    private ManualRepository manualRepository;



    public void importCodelist(MultipartFile arquivo,Integer codManual) throws IOException{
        manualRepository.findById(codManual).ifPresent((manual) -> {
            
            try {
                @Cleanup FileInputStream codelistFile = (FileInputStream) arquivo.getInputStream();
                @Cleanup Workbook workbook = new XSSFWorkbook(codelistFile);
                Sheet sheet = workbook.getSheetAt(0);
                List<Row> linhas = IteratorUtils.toList(sheet.iterator());
                List<Traco> tracos = new ArrayList<Traco>();
                //List<CodelistLinha> codelistLinhas = new ArrayList<CodelistLinha>();
                List<Secao> secoes = new ArrayList<Secao>();
                String[] ultimoNumSecao = {""};
                String[] ultimoNumSubSecao = {""};

                linhas.forEach(linha -> {
                    List<Cell> celulas = IteratorUtils.toList(linha.cellIterator());        
                    if(linhas.indexOf(linha)!=0){
                        //pega todos os tracos
                        if(linhas.indexOf(linha)==1){
                            celulas.forEach(celula -> {
                                Integer index = celulas.indexOf(celula);
                                if(index>=6){
                                    List<String> tracoCelula = Arrays.asList(celula.toString().split(" - "));                                    
                                    Traco traco = Traco.builder()
                                    .traco(Integer.valueOf(tracoCelula.get(0)))
                                    .nome(tracoCelula.get(1))
                                    .build();
                                    tracos.add(tracoRepository.save(traco));
                                }
                            });      
                        } else {   
                            //Começo seção
                            Secao secao = new Secao();
                            secao.setNumSecao(celulas.get(0).toString());
                            //Fim seção

                            //Começo sub-seção
                            SubSecao subSecao = new SubSecao();
                            subSecao.setNumSubSecao(celulas.get(1).toString());
                            //Fim sub-seção

                            //Começo bloco
                            Bloco bloco;

                            //Começo traços do bloco
                            String tracoCelula = celulas.get(5).toString();
                            final List<Traco> tracosBloco = new ArrayList<Traco>();
                            if(tracoCelula.equals("ALL")){
                                tracosBloco.addAll(tracos);
                            } else {
                                //traços presentes naquele bloco no arquivo do codelist
                                List<Integer> tracosInteger = new ArrayList<Integer>();

                                Arrays.asList(tracoCelula.split(",")).forEach(traco -> {
                                    Double valorDouble = Math.abs(Double.valueOf(traco));
                                    Integer valor = valorDouble.intValue();
                                    tracosInteger.add(valor);
                                });

                                tracos.forEach(tr -> {
                                    Integer traco = tr.getTraco();
                                    if(tracosInteger.contains(traco)){
                                        tracosBloco.add(tr);
                                    }
                                });
                            }
                            //Fim traços do bloco

                            bloco = Bloco.builder()
                                        .numBloco(celulas.get(2).toString())
                                        .nomeBloco(celulas.get(3).toString())
                                        .codBlocoCodelist(celulas.get(4).toString())
                                        .tracos(tracosBloco)
                                        .build();
                            //Fim bloco

                            //Começo Adiciona o bloco na seção
                            secao.getBlocos().add(bloco);
                            //Fim Adiciona o bloco na seção
                            if(secoes.size()==0){ 
                                ultimoNumSecao[0] = secao.getNumSecao();
                                if(!subSecao.getNumSubSecao().equals("")){
                                    ultimoNumSubSecao[0] = subSecao.getNumSubSecao();
                                    subSecao.getBlocos().add(bloco);
                                    secao.getSubSecoes().add(subSecao);
                                    secoes.add(secao);
                                } else {
                                    secoes.add(secao);
                                }
                            } else {
                                if(!secao.getNumSecao().equals(ultimoNumSecao[0])){
                                    ultimoNumSecao[0] = secao.getNumSecao();
                                    ultimoNumSubSecao[0] = "";
                                    if(!subSecao.getNumSubSecao().equals("")){
                                        ultimoNumSubSecao[0] = subSecao.getNumSubSecao();
                                        subSecao.getBlocos().add(bloco);
                                        secao.getSubSecoes().add(subSecao);
                                        secoes.add(secao);
                                    } else {
                                        secoes.add(secao);
                                    }
                                } else {
                                    Integer ultimoIndexSecao = secoes.size()-1;
                                    Integer ultimoIndexSubSecao = secoes.get(ultimoIndexSecao).getSubSecoes().size()-1;
                                    if(!subSecao.getNumSubSecao().equals("")){



                                        if(!subSecao.getNumSubSecao().equals(ultimoNumSubSecao[0])){
                                            ultimoNumSubSecao[0] = subSecao.getNumSubSecao();

                                            subSecao.getBlocos().add(bloco);
                                            secoes.get(ultimoIndexSecao).getSubSecoes().add(subSecao);
                                        } else {
                                            secoes.get(ultimoIndexSecao).getSubSecoes().get(ultimoIndexSubSecao).getBlocos().add(bloco);
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
                manualRepository.save(manual);

            } catch (IOException e) {
                
            }
             
            
        });

    }
}
