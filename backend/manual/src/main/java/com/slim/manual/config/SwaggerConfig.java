package com.slim.manual.config;

import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

//CONFIGURAR NO SPRING SECURITY CONFIG
@Configuration
public class SwaggerConfig {
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.slim.rest.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("Api Gerador Manual")
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
}
