package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.demo.model.Admin;
import com.example.demo.service.AdminService;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdmin(AdminService adminService) {
        return args -> {
            String adminEmail = "admin@example.com";
            if (adminService.findByEmail(adminEmail).isEmpty()) {
                Admin a = new Admin();
                a.setNombre("Admin");
                a.setCodigo("A0001");
                a.setEmail(adminEmail);
                a.setPassword("admin123"); // AdminService.create codifica la contraseña
                adminService.create(a);
                System.out.println("Admin creado: " + adminEmail);
            }
        };
    }
}
