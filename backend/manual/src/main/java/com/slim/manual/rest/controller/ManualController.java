package com.slim.manual.rest.controller;


import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import com.slim.manual.domain.model.Codelist;
import com.slim.manual.domain.model.CodelistLinha;
import com.slim.manual.domain.model.Manual;
import com.slim.manual.domain.model.Revisao;
import com.slim.manual.domain.model.Secao;
import com.slim.manual.domain.model.Traco;
import com.slim.manual.rest.dto.ManualDTO;
import com.slim.manual.service.ManualService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin
@RequestMapping("/manual")
@Api(tags = "Manual")
public class ManualController {

    @Autowired
	private ManualService manualService;

    /**
     * Endpoint para fazer upload de um codelist através de um arquivo excel.
     * @param arquivo
     * @param codManual
     * @throws IOException
     */
    @PostMapping("/{codManual}/codelist")
    @ApiOperation(value = "Faz upload de um codelist através de um arquivo excel.")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
        @ApiResponse(code = 201,message = "Codelist importado com sucesso."),
        @ApiResponse(code = 400,message = "Erro ao importar codelist."),
        @ApiResponse(code = 404,message = "Manual não encontrado.")
    })
    public Manual uploadCodeList(@RequestParam MultipartFile arquivo, @PathVariable Integer codManual) throws IOException{
        return manualService.importCodelist(arquivo,codManual);
        // /(manual+pn)/Master/(numSecao)(vai precisar arrumar algum jeito de identificar pq eu nao salvo o nome da secao)/(numBloco+nomeBloco)/(nomeManual+pn)-(numSecao)-(numBloco)c(codBlocoCodelist)
        // /(manual+pn)/Master/(numSecao)/(numSubsecao)(msm esquema da secao)/(numBloco+nomeBloco)/(nomeManual+pn)-(numSecao)-(numBloco)c(codBlocoCodelist)

    }

    /**
     * Endpoint para criar um codelist. TESTAR
     * @param codelist
     * @param codManual
     */
    @PostMapping("/{codManual}/codelist/create")
    @ApiOperation(value = "Cria um codelist.")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
        @ApiResponse(code = 201,message = "Codelist criado com sucesso."),
        @ApiResponse(code = 400,message = "Erro ao criar codelist."),
        @ApiResponse(code = 404,message = "Manual não encontrado.")
    })
    public Manual createCodelist(@RequestBody Codelist codelist, @PathVariable Integer codManual){
        return manualService.createCodelist(codelist,codManual);   
    }

    /**
     * Retorna todos os manuais
     * @return List<ManualDTO>
     */
    @GetMapping
    @ApiOperation(value = "Retorna todos os manuais.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(code = 200,message = "Manuais encontrados.")
    public List<ManualDTO> getManuais() {
        return manualService.getManuais();
    }

    /**
     * Retorna todas as informações de um manual.
     * @param codManual
     * @return
     */
    @GetMapping("{codManual}")
    @ApiOperation(value = "Retorna todas as informações de um manual.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
        @ApiResponse(code = 200,message = "Manual encontrado com sucesso."),
        @ApiResponse(code = 404,message = "Manual não encontrado.")
    })
    public Manual getManualById(@PathVariable Integer codManual) {
        return manualService.getManualById(codManual);
    }

    @PostMapping
    @ApiOperation(value = "Cria um manual.")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
        @ApiResponse(code = 201,message = "Manual criado com sucesso."),
        @ApiResponse(code = 400,message = "Erro ao criar manual.")
    })
    public ManualDTO createManual(@RequestBody @Valid ManualDTO manual) {
        return manualService.createManual(manual);
    }

    @DeleteMapping("/{codManual}/bloco/{codBloco}")
    @ApiOperation(value = "Deleta um bloco.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
        @ApiResponse(code = 201,message = "Manual criado com sucesso."),
        @ApiResponse(code = 400,message = "Erro ao criar manual.")
    })
    public Manual deleteBloco(@PathVariable Integer codManual, @PathVariable Integer codBloco) {
        return manualService.deleteBloco(codManual,codBloco);
    }

    @PostMapping("/bloco/{codBloco}")
    @ApiOperation(value = "Faz upload de um arquivo para o bloco especificado.")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
        @ApiResponse(code = 201,message = "Arquivo importado com sucesso."),
        @ApiResponse(code = 400,message = "Erro ao importar Arquivo."),
        @ApiResponse(code = 404,message = "Manual não encontrado."),
        @ApiResponse(code = 404,message = "Bloco não encontrado.")
    })
    public void uploadArquivoBloco(@RequestParam MultipartFile arquivo, @PathVariable Integer codBloco) throws IOException{
        manualService.importArquivoBloco(arquivo,codBloco);
    }

    @GetMapping("/{codManual}/{numSecao}")
    @ApiOperation(value = "Faz upload de um codelist através de um arquivo excel.")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
        @ApiResponse(code = 201,message = "Codelist importado com sucesso."),
        @ApiResponse(code = 400,message = "Erro ao importar codelist."),
        @ApiResponse(code = 404,message = "Manual não encontrado.")
    })
    public Secao teste(@PathVariable Integer codManual, @PathVariable String numSecao){
        return manualService.testeSecao(numSecao, codManual);

    }

    @GetMapping("/{codManual}/revisoes")
    @ApiOperation(value = "Retorna todas as revisões cadastradas de um determinado manual.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
        @ApiResponse(code = 200,message = "Revisões encontradas com sucesso."),
        @ApiResponse(code = 404,message = "Manual não encontrado.")
    })
    public List<Revisao> getRevisoes(@PathVariable Integer codManual){
        return manualService.getRevisoesByCodManual(codManual);

    }

    @GetMapping("/{codManual}/tracos")
    @ApiOperation(value = "Retorna todos os tracos cadastrados de um determinado manual.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
        @ApiResponse(code = 200,message = "Traços encontrados com sucesso."),
        @ApiResponse(code = 404,message = "Manual não encontrado.")
    })
    public List<Traco> getTracos(@PathVariable Integer codManual){
        return manualService.getTracosByCodManual(codManual);

    }
    /* ------------------------------ROTAS DE TESTE----------------------------------- */
    @GetMapping("/listManuais")
    @ApiOperation(value = "Retorna todos os manuais no diretório.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
        @ApiResponse(code = 200,message = "Manuais encontrados com sucesso."),
        @ApiResponse(code = 404,message = "Manuais não encontrado.")
    })
    public String[] listManuais() throws IOException {
        return manualService.listDiretorioManuais();
    }

    @PostMapping("/listSecao")
    @ApiOperation(value = "Lista as seções do manual.")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
        @ApiResponse(code = 201,message = "Manual criado com sucesso."),
        @ApiResponse(code = 400,message = "Erro ao criar manual.")
    })
    public void createManualDiretorio(@RequestBody ManualDTO manual) throws IOException {
        manualService.createDiretorioManual(manual);
    }

    @GetMapping("/listSecoes/{manual}")
    @ApiOperation(value = "Retorna todas as seções de um manual.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
        @ApiResponse(code = 200,message = "Seções encontradas com sucesso."),
        @ApiResponse(code = 404,message = "Seções não encontradas.")
    })
    public String[] listSecoes(@PathVariable String manual) throws IOException {
        return manualService.listDiretorioSecao(manual);
    }
}
