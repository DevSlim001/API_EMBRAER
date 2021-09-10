package com.slim.manual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.slim.manual.domain.model.Usuario;
import com.slim.manual.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class ManualApplication implements CommandLineRunner {
    
    @Autowired
    private UsuarioService usuarioService;

	public static void main(String[] args) {
		SpringApplication.run(ManualApplication.class, args);
	}

    @Override
	public void run(String... args) throws Exception {
        Usuario usuario =  Usuario
        .builder()
        .nome("Nicholas")
        .email("nicholas@email.com")
        .senha("admin")
        .role("ADMIN")
        .build();
        usuarioService.create(usuario);

    }
}
