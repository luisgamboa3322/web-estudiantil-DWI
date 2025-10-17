package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/select-dashboard")
    public String selectDashboard(Authentication authentication, Model model) {
        String email = authentication != null ? authentication.getName() : null;
        model.addAttribute("userEmail", email);

        // Determinar qué dashboards están disponibles
        boolean hasAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_ADMIN_DASHBOARD"));
        boolean hasTeacher = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_TEACHER_DASHBOARD"));
        boolean hasStudent = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));

        model.addAttribute("hasAdmin", hasAdmin);
        model.addAttribute("hasTeacher", hasTeacher);
        model.addAttribute("hasStudent", hasStudent);

        return "select-dashboard";
    }
}