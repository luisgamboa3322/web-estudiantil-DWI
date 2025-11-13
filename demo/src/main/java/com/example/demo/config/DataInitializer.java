package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.example.demo.model.Admin;
import com.example.demo.model.Student;
import com.example.demo.model.Professor;
import com.example.demo.model.Curso;
import com.example.demo.model.EstadoCurso;
import com.example.demo.model.Role;
import com.example.demo.model.Permission;
import com.example.demo.service.AdminService;
import com.example.demo.service.StudentService;
import com.example.demo.service.ProfessorService;
import com.example.demo.service.StudentCursoService;
import com.example.demo.repository.CursoRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.PermissionRepository;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(new JavaTimeModule())
                .build();
    }

    @Bean
    public CommandLineRunner initData(AdminService adminService, StudentService studentService,
                                     ProfessorService professorService, CursoRepository cursoRepository,
                                     StudentCursoService studentCursoService, RoleRepository roleRepository,
                                     PermissionRepository permissionRepository) {
        return args -> {
            // Crear permisos
            Permission adminDash = permissionRepository.findByName("ACCESS_ADMIN_DASHBOARD")
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setName("ACCESS_ADMIN_DASHBOARD");
                    return permissionRepository.save(p);
                });

            Permission teacherDash = permissionRepository.findByName("ACCESS_TEACHER_DASHBOARD")
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setName("ACCESS_TEACHER_DASHBOARD");
                    return permissionRepository.save(p);
                });

            Permission studentDash = permissionRepository.findByName("ACCESS_STUDENT_DASHBOARD")
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setName("ACCESS_STUDENT_DASHBOARD");
                    return permissionRepository.save(p);
                });

            // Crear roles
            Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ADMIN");
                    Set<Permission> perms = new HashSet<>();
                    perms.add(adminDash);
                    perms.add(teacherDash);
                    perms.add(studentDash);
                    r.setPermissions(perms);
                    return roleRepository.save(r);
                });

            Role teacherRole = roleRepository.findByName("TEACHER")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("TEACHER");
                    Set<Permission> perms = new HashSet<>();
                    perms.add(teacherDash);
                    perms.add(studentDash);
                    r.setPermissions(perms);
                    return roleRepository.save(r);
                });

            Role studentRole = roleRepository.findByName("STUDENT")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("STUDENT");
                    Set<Permission> perms = new HashSet<>();
                    perms.add(studentDash);
                    r.setPermissions(perms);
                    return roleRepository.save(r);
                });
            // Crear admin
            String adminEmail = "admin@example.com";
            if (adminService.findByEmail(adminEmail).isEmpty()) {
                Admin a = new Admin();
                a.setNombre("Admin");
                a.setCodigo("A0001");
                a.setEmail(adminEmail);
                a.setPassword("admin123");
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                roles.add(teacherRole);
                roles.add(studentRole);
                a.setRoles(roles);
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
                Set<Role> roles = new HashSet<>();
                roles.add(teacherRole);
                p.setRoles(roles);
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
                Set<Role> roles = new HashSet<>();
                roles.add(studentRole);
                s.setRoles(roles);
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
                    // Asignar el curso "DESARROLLO WEB INTEGRADO" si existe, sino el primero disponible
                    var cursoPreferido = cursos.stream()
                        .filter(c -> c.getCodigo().equals("DWI-001"))
                        .findFirst();

                    if (cursoPreferido.isEmpty()) {
                        cursoPreferido = cursos.stream()
                            .filter(c -> c.getProfesor() != null)
                            .findFirst();
                    }

                    if (cursoPreferido.isPresent()) {
                        studentCursoService.assignCursoToStudent(student.getId(), cursoPreferido.get().getId());
                        System.out.println("Curso asignado al estudiante: " + cursoPreferido.get().getNombre());
                    }
                }
            }

            // Crear curso básico para pruebas
            if (cursoRepository.findByCodigo("DWI-001").isEmpty()) {
                Curso c = new Curso();
                c.setNombre("DESARROLLO WEB INTEGRADO");
                c.setCodigo("DWI-001");
                c.setDescripcion("Curso completo de desarrollo web con tecnologías modernas");
                c.setEstado(EstadoCurso.ACTIVO);
                // Asignar profesor
                professorService.findByEmail(profEmail).ifPresent(c::setProfesor);
                cursoRepository.save(c);
                System.out.println("Curso creado: DWI-001 - DESARROLLO WEB INTEGRADO");
            }
        };
    }

}
