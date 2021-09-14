package com.slim.manual.rest.controller;

import javax.transaction.Transactional;
import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.slim.manual.exception.CredencialException;
import com.slim.manual.rest.dto.CredenciaisDTO;
import com.slim.manual.rest.dto.SenhaDTO;
import com.slim.manual.rest.dto.TokenDTO;
import com.slim.manual.rest.dto.UsuarioDTO;
import com.slim.manual.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/usuarios")
@Api(tags = "Usuários")
public class UsuarioController {

    @Autowired
	private UsuarioService usuarioService;

    /**
     * Endpoint para criar um usuário (apenas administradores podem acessar)
     * @param usuario
     * @return
     * @throws Exception
     */
    @PostMapping
    @Transactional
    @ApiOperation(value = "Cria um usuário.")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO createUser(@RequestBody @Valid UsuarioDTO usuario ) throws CredencialException {
        try {
            return usuarioService.create(usuario);
        } catch (CredencialException e) {
            throw new CredencialException(e.getMessage());
        }
    }

    /**
     * Endpoint para realizar a autenticação de um usuário
     * @param credenciais
     * @return jwt token
     */
    @PostMapping("/auth")
    @ApiOperation(value = "Faz a autenticação de um usuário.")
    @ResponseStatus(HttpStatus.OK)
    public TokenDTO auth(@RequestBody CredenciaisDTO credenciais){
        try {
            return usuarioService.auth(credenciais);
        } catch (CredencialException e) {
            throw new CredencialException(e.getMessage());
        }
    }
    
    /**
     * Endpoint para atualizar parcialmente um usuário.
     * [
     *      {"op":"add","path":"/favorites/0","value":"Bread"}, adiciona um elemento no inicio de um array
     *      {"op":"remove","path":"/communicationPreferences"}, remove valor do campo communicationPreferences
     *      {"op":"replace","path":"/telephone","value":"001-555-5678"}, atualiza o valor do campo telephone
     *      {"op":"test","path":"/telephone","value":"001-555-5678"} verifica se o valor é igual
     * ] EXEMPLO objeto requisição
     * ATENÇÃO: quando o campo a ser atualizado for senha, 
     * tem que criptografar a senha no frontend antes de atualizar no backend
     * @param codUsuario
     * @param patch
     * @return usuarioDTO
     * @throws JsonProcessingException
     * @throws JsonPatchException
     */
    @PatchMapping(path="{codUsuario}",consumes = "application/json-patch+json")
    @ApiOperation(value = "Faz a atualização parcial de um usuário.")
    @ResponseStatus(HttpStatus.OK)
    public UsuarioDTO updateUsuario(@PathVariable Integer codUsuario , @RequestBody JsonPatch patch) throws JsonProcessingException, JsonPatchException{
        return usuarioService.updateUsuario(codUsuario, patch);
    }

    /**
     * Atualiza a senha do usuário pelo id
     * @param codUsuario
     * @param senha
     */
    @PatchMapping("/senha/{codUsuario}")
    @ApiOperation(value = "Faz a atualização da senha de um usuário.")
    @ResponseStatus(HttpStatus.OK)
    public void updateSenhaUsuario(@PathVariable Integer codUsuario , @RequestBody SenhaDTO senha) {
        usuarioService.updateSenhaUsuario(codUsuario, senha);
    }

    /**
     * Atualiza a senha do usuário pelo email
     * @param email
     * @param senha
     */
    @PatchMapping("/senha/{email}/update")
    @ApiOperation(value = "Faz a atualização da senha de um usuário.")
    @ResponseStatus(HttpStatus.OK)
    public void esqueciSenha(@PathVariable String email) {
        usuarioService.esqueciSenha(email);
    }

    /**
     * Deleta um usuário
     * @param codUsuario
     */
    @DeleteMapping("{codUsuario}")
    @ApiOperation(value = "Deleta um usuário.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUsuario(@PathVariable Integer codUsuario){
        usuarioService.deleteUsuario(codUsuario);
    }
}
