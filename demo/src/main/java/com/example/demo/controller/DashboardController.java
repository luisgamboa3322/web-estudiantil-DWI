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

        // Mostrar TODAS las opciones del dashboard para todos los usuarios
        // El control de acceso se maneja en cada dashboard individualmente
        model.addAttribute("hasAdmin", true);   // Siempre mostrar opción admin
        model.addAttribute("hasTeacher", true); // Siempre mostrar opción docente
        model.addAttribute("hasStudent", true); // Siempre mostrar opción estudiante

        return "select-dashboard";
    }

    @GetMapping("/error/acceso-denegado")
    public String accesoDenegado(Model model, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        model.addAttribute("userEmail", email);
        return "error/acceso-denegado";
    }

    @GetMapping("/redirect/admin")
    public String redirectToAdmin(Authentication authentication) {
        boolean hasAdminPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_ADMIN_DASHBOARD"));

        if (hasAdminPermission) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/error/acceso-denegado";
        }
    }

    @GetMapping("/redirect/profesor")
    public String redirectToProfesor(Authentication authentication) {
        boolean hasTeacherPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_TEACHER_DASHBOARD"));

        if (hasTeacherPermission) {
            return "redirect:/profesor/dashboard";
        } else {
            return "redirect:/error/acceso-denegado";
        }
    }

    @GetMapping("/redirect/student")
    public String redirectToStudent(Authentication authentication) {
        boolean hasStudentPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));

        if (hasStudentPermission) {
            return "redirect:/student/dashboard";
        } else {
            return "redirect:/error/acceso-denegado";
        }
    }
}