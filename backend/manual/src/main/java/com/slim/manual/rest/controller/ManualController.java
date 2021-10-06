package com.slim.manual.rest.controller;


import java.io.IOException;



import javax.validation.Valid;

import com.slim.manual.service.CodelistService;

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
	private CodelistService codelistService;

    /**
    * Endpoint para criar um usuário (apenas administradores podem acessar)
    * @param usuario
    * @return
     * @throws IOException
    * @throws Exception
    */
    @PostMapping("/codelist/import")
    @ApiOperation(value = "Faz upload de um codelist.")
    @ResponseStatus(HttpStatus.OK)
    public void uploadCodeList(@RequestParam MultipartFile arquivo) throws IOException{
        codelistService.importCodelist(arquivo,1);
        
    }
}
