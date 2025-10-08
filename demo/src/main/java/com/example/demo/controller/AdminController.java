package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    // Endpoints para obtener un usuario específico
    @GetMapping("/students/{id}")
    @ResponseBody
    public Student getStudent(@PathVariable Long id) {
        return studentService.findById(id).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));
    }

    @GetMapping("/profesores/{id}")
    @ResponseBody
    public Professor getProfessor(@PathVariable Long id) {
        return professorService.getById(id).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));
    }

    @GetMapping("/admins/{id}")
    @ResponseBody
    public Admin getAdmin(@PathVariable Long id) {
        return adminService.findById(id).orElseThrow(() -> new IllegalStateException("Administrador no encontrado"));
    }

    // Endpoints para administradores
    @PutMapping("/admins/{id}")
    @ResponseBody
    public Admin updateAdmin(@PathVariable Long id, @RequestBody Admin admin) {
        return adminService.update(id, admin);
    }

    @DeleteMapping("/admins/{id}")
    @ResponseBody
    public String deleteAdmin(@PathVariable Long id) {
        adminService.delete(id);
        return "Administrador eliminado";
    }

    // Endpoints para estudiantes
    @PutMapping("/students/{id}")
    @ResponseBody
    public Student updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.update(id, student);
    }

    @DeleteMapping("/students/{id}")
    @ResponseBody
    public String deleteStudent(@PathVariable Long id) {
        studentService.delete(id);
        return "Estudiante eliminado";
    }

    // Endpoints para profesores
    @PutMapping("/profesores/{id}")
    @ResponseBody
    public Professor updateProfessor(@PathVariable Long id, @RequestBody Professor professor) {
        return professorService.update(id, professor);
    }

    @DeleteMapping("/profesores/{id}")
    @ResponseBody
    public String deleteProfessor(@PathVariable Long id) {
        professorService.delete(id);
        return "Profesor eliminado";
    }
}