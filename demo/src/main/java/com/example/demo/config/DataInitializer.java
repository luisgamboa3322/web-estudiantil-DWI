package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.demo.model.Student;
import com.example.demo.service.StudentService;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdmin(StudentService studentService) {
        return args -> {
            String adminEmail = "admin@example.com";
            if (studentService.findByEmail(adminEmail).isEmpty()) {
                Student s = new Student();
                s.setNombre("Admin");
                s.setCodigo("A0001"); // inicia con 'A' para redirigir a admin
                s.setEmail(adminEmail);
                s.setPassword("admin123"); // StudentService.save codifica la contraseña
                studentService.save(s);
                System.out.println("Admin creado: " + adminEmail);
            }
        };
    }
}
