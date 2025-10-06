package com.example.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

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
                .requestMatchers("/admin/students").permitAll()
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
                    // Buscar student por email y usar su 'codigo' para decidir la ruta
                    Optional<com.example.demo.model.Student> opt = studentService.findByEmail(username);
                    if (opt.isPresent() && opt.get().getCodigo() != null && !opt.get().getCodigo().isEmpty()) {
                        char first = Character.toUpperCase(opt.get().getCodigo().trim().charAt(0));
                        if (first == 'U') target = "/student/dashboard";
                        else if (first == 'C') target = "/teacher/dashboard";
                        else if (first == 'A') target = "/admin/dashboard";
                    } else {
                        // fallback: usar la inicial del email si no hay codigo
                        char first = Character.toUpperCase(username.charAt(0));
                        if (first == 'U') target = "/student/dashboard";
                        else if (first == 'C') target = "/teacher/dashboard";
                        else if (first == 'A') target = "/admin/dashboard";
                        else target = "/student/dashboard";
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