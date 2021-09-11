package com.slim.manual.rest.controller;

import com.slim.manual.domain.model.Usuario;
import com.slim.manual.rest.dto.UsuarioDTO;
import com.slim.manual.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/usuarios")
@Api("API de usuários")
public class UsuarioController {

    @Autowired
	private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioDTO> createUser(@RequestBody Usuario usuario ) throws Exception {
        return ResponseEntity
                .ok()
                .body(usuarioService.create(usuario));
    }
}
