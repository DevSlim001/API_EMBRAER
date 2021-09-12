package com.slim.manual.rest.controller;

import javax.transaction.Transactional;

import com.slim.manual.exception.SenhaInvalidaException;
import com.slim.manual.rest.dto.CredenciaisDTO;
import com.slim.manual.rest.dto.TokenDTO;
import com.slim.manual.rest.dto.UsuarioDTO;
import com.slim.manual.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/usuarios")
@Api(tags = "Usuários")
public class UsuarioController {

    @Autowired
	private UsuarioService usuarioService;

    @PostMapping
    @Transactional
    @ApiOperation(value = "Cria um usuário.")
    public ResponseEntity<UsuarioDTO> createUser(@RequestBody UsuarioDTO usuario ) throws Exception {
        return ResponseEntity
                .ok()
                .body(usuarioService.create(usuario));
    }

    @PostMapping("/auth")
    @ApiOperation(value = "Faz a autenticação de um usuário.")
    public TokenDTO auth(@RequestBody CredenciaisDTO credenciais){
        TokenDTO token;
        try {
            token = usuarioService.auth(credenciais);
        } catch (UsernameNotFoundException | SenhaInvalidaException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());
        }
        return token;
    }
}
