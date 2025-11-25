package technologyaa.Devit.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import technologyaa.Devit.domain.auth.oauth.handler.OAuth2FailureHandler;
import technologyaa.Devit.domain.auth.oauth.handler.OAuth2SuccessHandler;
import technologyaa.Devit.domain.auth.oauth.service.CustomOAuth2UserService;
import technologyaa.Devit.global.jwt.JwtAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired(required = false)
    private CustomOAuth2UserService customOAuth2UserService;
    
    @Autowired(required = false)
    private OAuth2SuccessHandler oAuth2SuccessHandler;
    
    @Autowired(required = false)
    private OAuth2FailureHandler oAuth2FailureHandler;
    
    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/login", "/", "/static/**", "/swagger-ui/**", "/v3/api-docs/**", "/login.html", "/signup.html", "/chat.html", "/project.html", "/index.html", "/auth/**", "/*.html", "/ws/**").permitAll()
                                .requestMatchers("/oauth2/**").permitAll() // OAuth 로그인 허용
                                .requestMatchers("/api/users/me").permitAll() // 로그인 상태 확인용
                                .requestMatchers("/projects/**").permitAll() // 프로젝트 API 접근 허용
                                .requestMatchers("/api/profile/**").authenticated()
                        //.anyRequest().permitAll() // 임시로 모든 요청 허용 (테스트용)
                );
        
        // OAuth2 클라이언트 ID가 설정되어 있을 때만 OAuth2 로그인 활성화
        if (googleClientId != null && !googleClientId.isEmpty() && 
            customOAuth2UserService != null && 
            oAuth2SuccessHandler != null && 
            oAuth2FailureHandler != null) {
            http.oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(customOAuth2UserService)
                    )
                    .successHandler(oAuth2SuccessHandler)
                    .failureHandler(oAuth2FailureHandler)
            );
        }
        
        http
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

