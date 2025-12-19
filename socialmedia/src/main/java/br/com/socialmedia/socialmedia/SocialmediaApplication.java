package br.com.socialmedia.socialmedia;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Rede Social Meli",
                description = "socimeli APi Rest documentatios",
                version = "v1",
                contact = @Contact(
                        name = "Heinz Stranner Junior",
                        email = "tutor@eazybytes.com",
                        url = "https://www.eazybytes.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://junior-stranner.netlify.app/"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description =  "socimeli Api Rest documentatios",
                url = "https://www.eazybytes.com/swagger-ui.html"
        )
)
public class SocialmediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialmediaApplication.class, args);
    }

}


