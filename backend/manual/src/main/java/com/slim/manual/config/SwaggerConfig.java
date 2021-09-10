package com.slim.manual.config;

import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//CONFIGURAR NO SPRING SECURITY CONFIG
@Configuration
@EnableSwagger2
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
                .description("API para o Projeto Integrador do 4º Semestre de ADS 2021")
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
