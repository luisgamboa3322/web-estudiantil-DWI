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
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {}) // Habilitar CORS configurado en WebConfig
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login", "/", "/css/**", "/js/**", "/images/**", "/webjars/**",
                    "/static/**", "/favicon.ico", "/error", "/select-dashboard", "/error/acceso-denegado",
                    "/redirect/admin", "/redirect/profesor", "/redirect/student"
                ).permitAll()
                .requestMatchers("/admin/api/**", "/profesor/api/**", "/student/api/**").permitAll() // APIs JWT primero
                .requestMatchers(
                    "/admin/students/**",
                    "/admin/profesores/**",
                    "/admin/admins/**",
                    "/admin/cursos/**",
                    "/admin/asignaciones/**"
                ).permitAll() // Protegidos manualmente con JWT en los controladores
                .requestMatchers("/profesor/cursos/**",
                                 "/profesor/semanas/**",
                                 "/profesor/tareas/**",
                                 "/profesor/materiales/**",
                                 "/profesor/entregas/**").permitAll()
                .requestMatchers("/student/cursos/**",
                                 "/student/semanas/**",
                                 "/student/tareas/**",
                                 "/student/materiales/**",
                                 "/student/profile").permitAll()
                .requestMatchers("/admin/**").hasAuthority("ACCESS_ADMIN_DASHBOARD")
                .requestMatchers("/profesor/**").hasAuthority("ACCESS_TEACHER_DASHBOARD")
                .requestMatchers("/student/**").hasAuthority("ACCESS_STUDENT_DASHBOARD")
                .requestMatchers("/api/auth/**", "/api/public/**").permitAll() // APIs públicas para login
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
                    // Verificar si el usuario tiene múltiples dashboards disponibles (ej. TEACHER)
                    boolean hasAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ACCESS_ADMIN_DASHBOARD"));
                    boolean hasTeacher = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ACCESS_TEACHER_DASHBOARD"));
                    boolean hasStudent = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));

                    if (hasAdmin && hasTeacher && hasStudent) {
                        // ADMIN: múltiples opciones, redirigir a selección
                        target = "/select-dashboard";
                    } else if (hasTeacher && hasStudent && !hasAdmin) {
                        // TEACHER: múltiples opciones (docente + estudiante), redirigir a selección
                        target = "/select-dashboard";
                    } else if (hasStudent && !hasTeacher && !hasAdmin) {
                        // STUDENT: solo estudiante, pero mostrar selector para consistencia
                        target = "/select-dashboard";
                    } else if (hasAdmin) {
                        target = "/admin/dashboard";
                    } else if (hasTeacher) {
                        target = "/profesor/dashboard";
                    } else if (hasStudent) {
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
