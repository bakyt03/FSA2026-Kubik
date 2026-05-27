package sk.posam.fsa.statstracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    private final RestSecurityExceptionHandler restSecurityExceptionHandler;

    SecurityConfiguration(RestSecurityExceptionHandler restSecurityExceptionHandler) {
        this.restSecurityExceptionHandler = restSecurityExceptionHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        // Player linking – ADMIN only
                        .requestMatchers(HttpMethod.PATCH, "/players/*/link-user").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/players/*/link-user").hasRole("ADMIN")
                        // User management requires ADMIN role
                        .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        // Personalized endpoint requires authentication
                        .requestMatchers(HttpMethod.GET, "/players/me").authenticated()
                        // Public read-only access
                        .requestMatchers(HttpMethod.GET, "/**").permitAll()
                        // Team suggestions is open to all
                        .requestMatchers(HttpMethod.POST, "/team-suggestions").permitAll()
                        // All write operations require authentication
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restSecurityExceptionHandler)
                        .accessDeniedHandler(restSecurityExceptionHandler))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            jwt.decoder(jwtDecoder);
                            jwt.jwtAuthenticationConverter(JwtConverter::new);
                        }))
                .build();
    }
}
