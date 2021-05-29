package com.avakio.mailer.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@OpenAPIDefinition(info =
    @Info(title = "Mailer API", version = "1.0", description = "Documentation Mailer API v1.0")
)
@SecurityScheme(
        name = "controllerBasicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class SwaggerConfiguration {

    public SwaggerConfiguration() {
        if (log.isDebugEnabled()) log.debug("Component Initialized.");
    }
}
