package com.slim.manual.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

import com.slim.manual.domain.model.Usuario;


@Service
public class JwtService {


    @Value("${security.jwt.chave-assinatura}")
    private String chaveAssinatura;

    /**
     * Gera um token JWT através das informações de um usuário
     * @param usuario
     * @return token
     */
    public String gerarToken(Usuario usuario,Long expiracao){
        LocalDateTime dataHoraExpiracao = LocalDateTime.now().plusMinutes(expiracao);
        Instant instant = dataHoraExpiracao.atZone(ZoneId.systemDefault()).toInstant();
        Date data = Date.from(instant);
        
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("codUsuario",usuario.getCodUsuario());
        claims.put("nome",usuario.getNome());
        claims.put("role",usuario.getRole());
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(usuario.getEmail())
                .setExpiration(data)
                .signWith(SignatureAlgorithm.HS512, chaveAssinatura)
                .compact();
    }

    /**
     * Pega as informações do usuário através de um token JWT
     * @param token
     * @return claims
     * @throws ExpiredJwtException
     */
    private Claims obterClaims(String token) throws ExpiredJwtException{
            return Jwts
                    .parserBuilder()
                    .setSigningKey(chaveAssinatura)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    }

    /**
     * Verifica se um token JWT ainda é válido no horário atual
     * @param token
     * @return boolean
     */
    public boolean validarToken(String token){
        try {
            Claims claims = obterClaims(token);
            Date data = claims.getExpiration();
            LocalDateTime dataExp = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            return !LocalDateTime.now().isAfter(dataExp);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retorna o código do usuário referente ao token jwt
     * @param token
     * @return codUsuario
     */
    public String obterEmailUsuario(String token){
        return (String) obterClaims(token).getSubject();
    }

    public Integer obterCodUsuario(String token){
        return Integer.valueOf(obterClaims(token).get("codUsuario").toString());
    }

    public String obterRoleUsuario(String token){
        return (String) obterClaims(token).get("role").toString();
    }

    public String obterNomeUsuario(String token){
        return (String) obterClaims(token).get("nome").toString();
    }

}
