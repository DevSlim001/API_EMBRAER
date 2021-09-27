package com.slim.manual.service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.github.fge.jsonpatch.JsonPatchException;
import com.slim.manual.domain.model.Usuario;
import com.slim.manual.domain.repository.UsuarioRepository;
import com.slim.manual.exception.CredencialException;
import com.slim.manual.exception.UsuarioNotFoundException;
import com.slim.manual.exception.ValidateTokenException;
import com.slim.manual.exception.UsuarioExistenteException;
import com.slim.manual.rest.dto.CredenciaisDTO;
import com.slim.manual.rest.dto.SenhaDTO;
import com.slim.manual.rest.dto.TokenDTO;
import com.slim.manual.rest.dto.UsuarioDTO;
import com.slim.manual.security.jwt.JwtService;
import com.slim.manual.utils.GeradorSenha;
import com.slim.manual.utils.SenderMailService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService{
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    SenderMailService senderMailService;

    
    private final PasswordEncoder passwordEncoder ;
    
    /**
     * Cria um usuário
     * @param Usuario
     * @return UsuarioDTO
     */
    @Transactional
    public UsuarioDTO create(UsuarioDTO user){
        boolean isPresent = usuarioRepository.findByEmail(user.getEmail()).isPresent();
        if(isPresent){
            throw new UsuarioExistenteException("O email fornecido já está cadastrado.");
        }
        GeradorSenha geradorSenha = new GeradorSenha();
        String senha = geradorSenha.gerarSenha();
        String senhaCripto = passwordEncoder.encode(senha);
        Usuario usuario = user.toEntityInsert();
        usuario.setSenha(senhaCripto);
        senderMailService.enviar(usuario.getEmail(),"Conta criada","Sua conta foi criada.\nSua senha é: "+senha);
        return usuarioRepository
                .save(usuario)
                .toUserDTO();
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = usuarioRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado nos registros."));
        String[] roles = usuario.getRole().equals("ADMIN") ? 
                        new String[] {"ADMIN","USER"} : 
                        new String[] {"USER"} ;
        return User
                .builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(roles)
                .build();
    }

    public TokenDTO auth(CredenciaisDTO credenciais){

        UserDetails user = loadUserByUsername(credenciais.getEmail());
        boolean senhasIguais = passwordEncoder.matches(credenciais.getSenha(), user.getPassword());
        if(senhasIguais){
            Usuario usuario = usuarioRepository
                                .findByEmail(credenciais.getEmail())
                                .get();
            String token;
            if(credenciais.isManterConectado()){
                token = jwtService.gerarToken(usuario,Long.valueOf("5256000")); // 5256000 minutos = 10 anos
            }
            else{
                token = jwtService.gerarToken(usuario,Long.valueOf("120")); // 120 minutos = 2 horas
            }
            return TokenDTO
                        .builder()
                        .token(token)
                        .build();
        }
        throw new CredencialException("Senha inválida.");
    }

    public void verifyToken(TokenDTO token){
        boolean isValid = jwtService.validarToken(token.getToken());
        if(!isValid){
            throw new ValidateTokenException("Token inválido.");
        }
    }

    public void updateUsuario(Integer codUsuario, JsonPatch patch) throws JsonProcessingException, JsonPatchException{
            Usuario usuario = usuarioRepository
                .findById(codUsuario)
                .orElseThrow(()-> new UsuarioNotFoundException("Usuário não encontrado."));
            Usuario usuarioAtualizado = applyPatchToUsuario(patch,usuario);
            usuarioRepository.save(usuarioAtualizado);
    }

    public void updateSenhaUsuario(SenhaDTO senha, HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        String token = authorization.split(" ")[1];
        Integer codUsuario = jwtService.obterCodUsuario(token);
        Usuario usuario = usuarioRepository
            .findById(codUsuario)
            .orElseThrow(()-> new UsuarioNotFoundException("Usuário não encontrado."));
            updateSenha(usuario, senha.getSenha());
    }

    private void updateSenha(Usuario usuario, String senha){
            String senhaCripto = passwordEncoder.encode(senha);
            usuario.setSenha(senhaCripto);
            usuarioRepository.save(usuario);
    }

    public void esqueciSenha(String email){
        Usuario usuario = usuarioRepository
            .findByEmail(email)
            .orElseThrow(()-> new UsuarioNotFoundException("Usuário não encontrado."));
        
        GeradorSenha geradorSenha = new GeradorSenha();
        String senha = geradorSenha.gerarSenha();
        updateSenha(usuario, senha);
        senderMailService.enviar(email,"Recuperação de senha","Sua nova senha é: "+senha);

    }

    public void deleteUsuario(Integer codUsuario){
        usuarioRepository
            .findById(codUsuario)
            .map((usuario)->{
                usuarioRepository.delete(usuario);
                return usuario;
            })
            .orElseThrow(()-> new UsuarioNotFoundException("Usuário não encontrado"));

    }

    private Usuario applyPatchToUsuario(JsonPatch patch, Usuario usuario) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(usuario, JsonNode.class));
        return objectMapper.treeToValue(patched, Usuario.class);
    }
}
