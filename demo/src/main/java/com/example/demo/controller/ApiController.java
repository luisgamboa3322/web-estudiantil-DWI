package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AdminService;
import com.example.demo.service.StudentService;
import com.example.demo.service.ProfessorService;
import com.example.demo.service.StudentCursoService;
import com.example.demo.repository.CursoRepository;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private StudentCursoService studentCursoService;

    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("API está funcionando correctamente");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("students", studentService.findAll());
        data.put("professors", professorService.findAll());
        data.put("admins", adminService.findAll());
        data.put("cursos", cursoRepository.findAll());
        data.put("asignaciones", studentCursoService.findAllWithDetails());
        
        // Estadísticas generales
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalStudents", (long) studentService.findAll().size());
        stats.put("totalProfessors", (long) professorService.findAll().size());
        stats.put("totalAdmins", (long) adminService.findAll().size());
        stats.put("totalCursos", (long) cursoRepository.findAll().size());
        stats.put("totalAsignaciones", (long) studentCursoService.findAll().size());
        
        data.put("stats", stats);
        
        return ResponseEntity.ok(data);
    }

    @GetMapping("/admin/dashboard")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        return getDashboardData();
    }
}