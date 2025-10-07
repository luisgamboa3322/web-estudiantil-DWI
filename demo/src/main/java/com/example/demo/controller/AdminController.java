package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.Admin;
import com.example.demo.service.AdminService;

import com.example.demo.model.Student;
import com.example.demo.service.StudentService;

import com.example.demo.model.Professor;
import com.example.demo.service.ProfessorService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final StudentService studentService;
    private final ProfessorService professorService;

    public AdminController(AdminService adminService, StudentService studentService, ProfessorService professorService) {
        this.adminService = adminService;
        this.studentService = studentService;
        this.professorService = professorService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Student> students = studentService.findAll();
        model.addAttribute("students", students);
        // añadir lista de profesores para Thymeleaf
        model.addAttribute("professors", professorService.findAll());
        // añadir lista de administradores
        model.addAttribute("admins", adminService.findAll());
        return "administrador/dashboard";
    }

    // Nuevo endpoint para crear estudiante (JSON)
    @PostMapping("/students")
    @ResponseBody
    public Student createStudent(@RequestBody Student student) {
        return studentService.save(student);
    }

    // Nuevo endpoint para crear profesor (JSON)
    @PostMapping("/profesores")
    @ResponseBody
    public Professor createProfessor(@RequestBody Professor professor) {
        return professorService.create(professor);
    }

    // Nuevo endpoint para crear administrador (JSON)
    @PostMapping("/admins")
    @ResponseBody
    public Admin createAdmin(@RequestBody Admin admin) {
        return adminService.create(admin);
    }
}