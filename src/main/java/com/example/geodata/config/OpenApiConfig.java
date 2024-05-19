package com.example.geodata.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "GeoData API",
                description = "convenient search for info about geo objects",
                version = "1.0",
                contact = @Contact(
                        name = "Victor",
                        email = "viktor706481342@gmail.com"
                )
        )
)

public class OpenApiConfig {
}
