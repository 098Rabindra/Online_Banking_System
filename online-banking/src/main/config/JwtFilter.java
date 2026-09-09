package com.bank.config;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bank.entity.User;
import com.bank.repository.UserRepository;
import com.bank.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(
            JwtUtil jwtUtil,
            UserRepository userRepository) {

        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {

        String path = request.getRequestURI();

        return path.startsWith("/auth/")
                || path.startsWith("/otp/")
                || path.startsWith("/location/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.equals("/")
                || path.equals("/login.html")
                || path.equals("/register.html")
                || path.equals("/forgot.html")
                || path.equals("/main_page.html")
                || path.equals("/favicon.ico")
                || path.equals("/error");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        System.out.println("\n========== JWT FILTER ==========");
        System.out.println("PATH   = " + request.getRequestURI());
        System.out.println("HEADER = " + header);

        if (header != null && header.startsWith("Bearer ")) {

            try {

                String token = header.substring(7);

                if (jwtUtil.validateToken(token)) {

                    String email = jwtUtil.extractUsername(token);

                    User user = userRepository
                            .findByEmail(email)
                            .orElse(null);

                    if (user != null) {

                        String role = user.getRole().name();

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        email,
                                        null,
                                        List.of(
                                                new SimpleGrantedAuthority(role)
                                        )
                                );

                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(auth);

                        System.out.println("AUTHENTICATED USER = " + email);
                        System.out.println("ROLE = " + role);
                    }
                }

            } catch (Exception e) {

                System.out.println("JWT ERROR:");
                e.printStackTrace();

                SecurityContextHolder.clearContext();
            }

        } else {

            System.out.println("NO AUTHORIZATION HEADER FOUND");
        }

        filterChain.doFilter(request, response);
    }
}