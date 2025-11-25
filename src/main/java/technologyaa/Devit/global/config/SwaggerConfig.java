package technologyaa.Devit.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
        );

        return new OpenAPI()
                .info(new Info()
                        .title("Devit API 문서")
                        .description("Devit 실시간 협업 플랫폼 API 문서입니다.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Devit Team")
                                .email("devit@example.com")
                        )
                )
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}

