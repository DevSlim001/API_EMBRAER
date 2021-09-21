package com.slim.manual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.slim.manual.domain.model.Usuario;
import com.slim.manual.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class ManualApplication extends SpringBootServletInitializer implements CommandLineRunner {
    

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder){
        return springApplicationBuilder.sources(ManualApplication.class);
    }
    public static void main(String[] args) {
		SpringApplication.run(ManualApplication.class, args);
	}

    @Override
	public void run(String... args) throws Exception {
        //a senha é admin
        Usuario usuario =  Usuario
            .builder()
            .nome("Admin")
            .email("admin@email.com")
            .senha("$2a$12$PiXpqF0LLgwv6ZRrj1gtYegpx2EKZHUz31TrtYVeq2JZy15x46uCK")
            .role("ADMIN")
            .build();
        usuarioRepository.save(usuario);

    }
}
