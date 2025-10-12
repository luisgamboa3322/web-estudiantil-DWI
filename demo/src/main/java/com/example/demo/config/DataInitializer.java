package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.demo.model.Admin;
import com.example.demo.model.Student;
import com.example.demo.model.Professor;
import com.example.demo.model.Curso;
import com.example.demo.model.EstadoCurso;
import com.example.demo.service.AdminService;
import com.example.demo.service.StudentService;
import com.example.demo.service.ProfessorService;
import com.example.demo.service.StudentCursoService;
import com.example.demo.repository.CursoRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(AdminService adminService, StudentService studentService,
                                     ProfessorService professorService, CursoRepository cursoRepository,
                                     StudentCursoService studentCursoService) {
        return args -> {
            // Crear admin
            String adminEmail = "admin@example.com";
            if (adminService.findByEmail(adminEmail).isEmpty()) {
                Admin a = new Admin();
                a.setNombre("Admin");
                a.setCodigo("A0001");
                a.setEmail(adminEmail);
                a.setPassword("admin123");
                adminService.create(a);
                System.out.println("Admin creado: " + adminEmail);
            }

            // Crear profesor
            String profEmail = "prof@example.com";
            if (professorService.findByEmail(profEmail).isEmpty()) {
                Professor p = new Professor();
                p.setNombre("Juan Pérez");
                p.setCodigo("P0001");
                p.setEmail(profEmail);
                p.setEspecialidad("Matemáticas");
                p.setPassword("prof123");
                professorService.create(p);
                System.out.println("Profesor creado: " + profEmail);
            }

            // Crear estudiante (si no existe por código)
            String studentCodigo = "u001";
            Student student = null;
            if (studentService.findAll().stream().noneMatch(s -> s.getCodigo().equals(studentCodigo))) {
                Student s = new Student();
                s.setNombre("Luis Francisco");
                s.setCodigo(studentCodigo);
                s.setEmail("student@example.com");
                s.setPassword("student123");
                student = studentService.save(s);
                System.out.println("Estudiante creado: " + studentCodigo);
            } else {
                student = studentService.findAll().stream()
                    .filter(s -> s.getCodigo().equals(studentCodigo))
                    .findFirst().orElse(null);
            }

            // Asignar curso al estudiante si no tiene asignaciones
            if (student != null && studentCursoService.findByStudentId(student.getId()).isEmpty()) {
                var cursos = cursoRepository.findAll();
                if (!cursos.isEmpty()) {
                    var cursoConProfesor = cursos.stream()
                        .filter(c -> c.getProfesor() != null)
                        .findFirst();
                    if (cursoConProfesor.isPresent()) {
                        studentCursoService.assignCursoToStudent(student.getId(), cursoConProfesor.get().getId());
                        System.out.println("Curso asignado al estudiante: " + cursoConProfesor.get().getNombre());
                    }
                }
            }

            // Crear curso
            if (cursoRepository.findByCodigo("MAT101").isEmpty()) {
                Curso c = new Curso();
                c.setNombre("Matemáticas Básicas");
                c.setCodigo("MAT101");
                c.setDescripcion("Curso de matemáticas básicas");
                c.setEstado(EstadoCurso.ACTIVO);
                // Asignar profesor
                professorService.findByEmail(profEmail).ifPresent(c::setProfesor);
                cursoRepository.save(c);
                System.out.println("Curso creado: MAT101");
            }
        };
    }
}
