package com.slim.manual.rest.controller;


import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import com.slim.manual.domain.model.Manual;
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
    @PostMapping("/codelist/{codManual}")
    @ApiOperation(value = "Faz upload de um codelist através de um arquivo excel.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
        @ApiResponse(code = 200,message = "Codelist importado com sucesso."),
        @ApiResponse(code = 400,message = "Erro ao importar codelist."),
        @ApiResponse(code = 404,message = "Manual não encontrado.")
    })
    public void uploadCodeList(@RequestParam MultipartFile arquivo, @PathVariable Integer codManual) throws IOException{
        manualService.importCodelist(arquivo,codManual);
        
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
}
