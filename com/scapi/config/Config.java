package com.scapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class Config {
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("product-api")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.scapi"))
                //.paths(PathSelectors.regex("/employee/.*"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("Product API")
                .description("Spring rest API reference")
                .licenseUrl("meena.velumayil@outlook.com")
                .version("1.0")
                .build();
    }


}
