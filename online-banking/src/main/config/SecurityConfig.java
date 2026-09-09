package com.bank.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        return http

                .csrf(csrf -> csrf.disable())

                .cors(cors -> {
                })

                .authorizeHttpRequests(auth -> auth

                		// =====================
                		// PUBLIC URLS
                		// =====================
                		.requestMatchers(
                		        "/",
                		        "/login.html",
                		        "/register.html",
                		        "/forgot.html",
                		        "/main_page.html",
                		        "/index.html",
                		        "/admin_dashboard.html",

                		        "/css/**",
                		        "/js/**",
                		        "/images/**",

                		        "/favicon.ico",
                		        "/error",

                		        "/auth/**",
                		        "/otp/**",
                		        "/location/**",
                		        "/test"
                		)
                		.permitAll()

                		// =====================
                		// ADMIN ONLY API
                		// =====================
                		.requestMatchers("/admin/**")
                		.hasRole("ADMIN")

                		// =====================
                		// USER + ADMIN API
                		// =====================
                		.requestMatchers(
                		        "/users/**",
                		        "/users/account-summary/**",
                		        "/users/change-login-password",
                		        "/dashboard/**",
                		        "/account/**",
                		        "/accounts/**",
                		        "/check-auth",
                		        "/insurances/**",
                		        "/service-requests/**",
                		        "/card-requests/**",
                		        "/statement/**",
                		        "/transactions/**"
                		)
                		.hasAnyRole("USER", "ADMIN")

                		// =====================
                		// ANY OTHER REQUEST
                		// =====================
                		.anyRequest()
                		.authenticated())

                .sessionManagement(session ->

                session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exception ->

                exception

                        // 401 Unauthorized
                        .authenticationEntryPoint(
                                (request, response, ex) -> {

                                    System.out.println(
                                            "UNAUTHORIZED: "
                                                    + request.getRequestURI());

                                    if (request.getRequestURI()
                                            .endsWith(".html")) {

                                        response.sendRedirect(
                                                "/login.html");

                                    } else {

                                        response.sendError(
                                                HttpServletResponse.SC_UNAUTHORIZED,
                                                "Unauthorized");
                                    }
                                })

                        // 403 Forbidden
                        .accessDeniedHandler(
                                (request, response, ex) -> {

                                    System.out.println(
                                            "ACCESS DENIED: "
                                                    + request.getRequestURI());

                                    response.sendError(
                                            HttpServletResponse.SC_FORBIDDEN,
                                            "Access Denied");
                                }))

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}