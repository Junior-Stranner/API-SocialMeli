package br.com.socialmedia.socialmedia.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Socialmeli")
                        .version("v1")
                        .description("API REST SocialMeli - buyers seguem sellers, posts e promoções.")
                        .termsOfService("https://socialmedia.com.br/socialmeli")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0"))
                        .contact(new Contact()
                                .name("Junior Stranner")
                                .email("Junior@socialmeli.com")));
    }
}