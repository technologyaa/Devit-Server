package com.example.websocketchat.global.config;

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
                        .description("""
                                Devit 실시간 협업 플랫폼 API 문서입니다.
                                
                                ## 주요 기능
                                - 회원가입 및 로그인 (JWT 기반 인증)
                                - OAuth2 소셜 로그인 (Google)
                                - 프로젝트 관리 (CRUD)
                                - 사용자 정보 조회
                                
                                ## 인증 방법
                                - JWT 토큰을 사용하여 인증이 필요한 API에 접근할 수 있습니다.
                                - 로그인 후 받은 JWT 토큰을 Authorization 헤더에 Bearer 토큰으로 추가하세요.
                                - 예: `Authorization: Bearer {your-jwt-token}`
                                """)
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

