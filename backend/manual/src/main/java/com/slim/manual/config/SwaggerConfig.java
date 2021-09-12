package com.slim.manual.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

//CONFIGURAR NO SPRING SECURITY CONFIG
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.slim.manual.rest.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("Api Gerador Manual")
                .version("1.0")
                .description("API criada para o projeto integrador do grupo Slim do 4º Semestre de ADS 2021 da FATEC São José dos Campos - Prof. Jessen Vidal")
                .contact(contact())
                .build();
    }

    private Contact contact(){
        return new Contact(
            "Slim",
            "https://github.com/DevSlim001/API_Slim_4Semestre"
            ,"timeslim123@gmail.com"
        );
    }

    public ApiKey apiKey(){
        return new ApiKey("JWT","Authorization","header");
    }

    private SecurityContext securityContext(){
        return SecurityContext
                    .builder()
                    .securityReferences(defaultAuth())
                    .build();
    }

    private List<SecurityReference> defaultAuth(){
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything"); 
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1]; 
        authorizationScopes[0] = authorizationScope; 
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes)); 
    }


}
