package com.example.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.service.StudentService;

@Configuration
public class SecurityConfig {

    // { changed code: inyectar StudentService para pasar al successHandler }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, StudentService studentService) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login", "/", "/css/**", "/js/**", "/images/**", "/webjars/**",
                    "/static/**", "/favicon.ico", "/error").permitAll()
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                // ajustar parámetros para que coincidan con tu formulario
                .usernameParameter("email")
                .passwordParameter("password")
                // { changed code: pasar studentService correctamente en lugar de null }
                .successHandler(authenticationSuccessHandler(studentService))
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(StudentService studentService) {
        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                String username = null;
                if (authentication != null) {
                    username = authentication.getName(); // email
                }
                if (username == null || username.isEmpty()) {
                    username = request.getParameter("email");
                }

                String target = "/";

                if (username != null && !username.isEmpty()) {
                    // Determinar el rol basado en las autoridades del usuario autenticado
                    if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                        target = "/admin/dashboard";
                    } else if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_TEACHER"))) {
                        target = "/profesor/dashboard";
                    } else if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_STUDENT"))) {
                        target = "/student/dashboard";
                    } else {
                        target = "/student/dashboard"; // fallback
                    }
                }

                getRedirectStrategy().sendRedirect(request, response, target);
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService uds) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(uds);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
}
