package com.pnc.project.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pnc.project.dto.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AuthRole {

    private final JwtFilter jwtFilter;

    public AuthRole(@Lazy JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    private String errorResponse(HttpStatus status, String message) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(Response.build(status.value(), message, null));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http

                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; script-src 'self';"))
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true))
                        .xssProtection(HeadersConfigurer.XXssConfig::disable)
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny))

                // Configuración general
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Filtro JWT
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/save").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html") .permitAll()
                        
                        .requestMatchers(HttpMethod.GET, "/api/actividades/**").hasAnyRole("ENCARGADO", "INSTRUCTOR_NORMAL", "INSTRUCTOR_REMUNERADO")
                        .requestMatchers("/api/actividades/**").hasRole("ENCARGADO") // POST, PUT, DELETE solo para ENCARGADO
                        .requestMatchers(HttpMethod.GET, "/api/materias").hasAnyRole("ENCARGADO", "INSTRUCTOR_NORMAL", "INSTRUCTOR_REMUNERADO")
                        .requestMatchers("/api/materias/**").hasRole("ENCARGADO") // POST, PUT, DELETE solo para ENCARGADO
                        .requestMatchers("/api/usuarios/**").hasRole("ENCARGADO")
                        .requestMatchers("/api/roles/**").hasRole("ENCARGADO")
                        .requestMatchers("/api/manage/horas/usuario/fecha").hasRole("ENCARGADO")

                        // Horas: MANAGER tiene acceso total, INSTRUCTOR solo algunos métodos
                        .requestMatchers(HttpMethod.GET, "/api/horas/**")
                        .hasAnyRole("ENCARGADO", "INSTRUCTOR_NORMAL", "INSTRUCTOR_REMUNERADO")
                        .requestMatchers("/api/horas/**").hasRole("ENCARGADO")

                        .anyRequest().authenticated())
                // Manejo de excepciones
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler((req, res, ex) -> {
                            res.setStatus(HttpStatus.FORBIDDEN.value());
                            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            res.getWriter().write(errorResponse(HttpStatus.FORBIDDEN,
                                    "Acceso denegado: No tienes los permisos necesarios"));
                        })
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setStatus(HttpStatus.UNAUTHORIZED.value());
                            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            res.getWriter().write(errorResponse(HttpStatus.UNAUTHORIZED,
                                    "Autenticación requerida: Por favor inicie sesión"));
                        }))
                .build();
    }
}