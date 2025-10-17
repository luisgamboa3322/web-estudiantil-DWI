package com.example.demo.integration;

import com.example.demo.model.*;
import com.example.demo.service.AdminService;
import com.example.demo.service.ProfessorService;
import com.example.demo.service.StudentCursoService;
import com.example.demo.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CrudOperationsIntegrationTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private StudentCursoService studentCursoService;

    @Autowired
    private com.example.demo.repository.CursoRepository cursoRepository;

    @Test
    void shouldPerformCompleteCrudOperationsWorkflow() {
        // === CREATE Operations ===

        // Create Admin
        Admin admin = new Admin();
        admin.setNombre("Admin Integration");
        admin.setCodigo("ADMINT001");
        admin.setEmail("admin.integration@test.com");
        admin.setPassword("password123");
        Admin createdAdmin = adminService.create(admin);
        assertThat(createdAdmin.getId()).isNotNull();

        // Create Professor
        Professor professor = new Professor();
        professor.setNombre("Professor Integration");
        professor.setCodigo("PROFINT001");
        professor.setEmail("professor.integration@test.com");
        professor.setEspecialidad("Matemáticas Avanzadas");
        professor.setPassword("password123");
        Professor createdProfessor = professorService.create(professor);
        assertThat(createdProfessor.getId()).isNotNull();

        // Create Student
        Student student = new Student();
        student.setNombre("Student Integration");
        student.setCodigo("STUINT001");
        student.setEmail("student.integration@test.com");
        student.setPassword("password123");
        Student createdStudent = studentService.save(student);
        assertThat(createdStudent.getId()).isNotNull();

        // Create Curso
        Curso curso = new Curso();
        curso.setNombre("Matemáticas Integrales");
        curso.setCodigo("MATINT101");
        curso.setDescripcion("Curso completo de integración matemática");
        curso.setEstado(EstadoCurso.ACTIVO);
        curso.setProfesor(createdProfessor);

        // Save curso through repository (since no service for curso yet)
        curso = cursoRepository.save(curso);
        assertThat(curso.getId()).isNotNull();

        // === READ Operations ===

        // Read Admin
        List<Admin> admins = adminService.findAll();
        assertThat(admins).isNotEmpty();
        assertThat(admins.stream().anyMatch(a -> a.getCodigo().equals("ADMINT001"))).isTrue();

        // Read Professor
        List<Professor> professors = professorService.findAll();
        assertThat(professors).isNotEmpty();
        assertThat(professors.stream().anyMatch(p -> p.getCodigo().equals("PROFINT001"))).isTrue();

        // Read Student
        List<Student> students = studentService.findAll();
        assertThat(students).isNotEmpty();
        assertThat(students.stream().anyMatch(s -> s.getCodigo().equals("STUINT001"))).isTrue();

        // === UPDATE Operations ===

        // Update Admin
        Admin adminChanges = new Admin();
        adminChanges.setNombre("Admin Integration Updated");
        Admin updatedAdmin = adminService.update(createdAdmin.getId(), adminChanges);
        assertThat(updatedAdmin.getNombre()).isEqualTo("Admin Integration Updated");

        // Update Professor
        Professor professorChanges = new Professor();
        professorChanges.setNombre("Professor Integration Updated");
        professorChanges.setEspecialidad("Matemáticas Aplicadas");
        Professor updatedProfessor = professorService.update(createdProfessor.getId(), professorChanges);
        assertThat(updatedProfessor.getNombre()).isEqualTo("Professor Integration Updated");
        assertThat(updatedProfessor.getEspecialidad()).isEqualTo("Matemáticas Aplicadas");

        // Update Student
        Student studentChanges = new Student();
        studentChanges.setNombre("Student Integration Updated");
        Student updatedStudent = studentService.update(createdStudent.getId(), studentChanges);
        assertThat(updatedStudent.getNombre()).isEqualTo("Student Integration Updated");

        // === RELATIONSHIP Operations ===

        // Assign Student to Curso
        StudentCurso assignment = studentCursoService.assignCursoToStudent(
            createdStudent.getId(), curso.getId());
        assertThat(assignment.getId()).isNotNull();
        assertThat(assignment.getEstado()).isEqualTo(EstadoAsignacion.ACTIVO);

        // Verify assignment
        List<StudentCurso> studentAssignments = studentCursoService.findByStudentId(createdStudent.getId());
        assertThat(studentAssignments).hasSize(1);
        assertThat(studentAssignments.get(0).getCurso().getId()).isEqualTo(curso.getId());

        // === DELETE Operations ===

        // Unassign student from curso (soft delete)
        studentCursoService.unassignCursoFromStudent(assignment.getId());
        StudentCurso unassigned = studentCursoService.findById(assignment.getId()).orElseThrow();
        assertThat(unassigned.getEstado()).isEqualTo(EstadoAsignacion.INACTIVO);

        // Delete Student
        studentService.delete(createdStudent.getId());
        assertThat(studentService.findById(createdStudent.getId())).isEmpty();

        // Delete Professor
        professorService.delete(createdProfessor.getId());
        assertThat(professorService.getById(createdProfessor.getId())).isEmpty();

        // Delete Admin
        adminService.delete(createdAdmin.getId());
        assertThat(adminService.findById(createdAdmin.getId())).isEmpty();

        // Delete Curso
        cursoRepository.delete(curso);
        assertThat(cursoRepository.findById(curso.getId())).isEmpty();
    }

    @Test
    void shouldHandleMultipleStudentsAndCoursesScenario() {
        // Create Professor
        Professor professor = new Professor();
        professor.setNombre("Professor Multiple");
        professor.setCodigo("PROFMULT001");
        professor.setEmail("professor.multiple@test.com");
        professor.setEspecialidad("Ciencias");
        professor.setPassword("password123");
        Professor createdProfessor = professorService.create(professor);

        // Create multiple courses
        Curso curso1 = new Curso();
        curso1.setNombre("Matemáticas I");
        curso1.setCodigo("MAT001");
        curso1.setDescripcion("Matemáticas básicas");
        curso1.setEstado(EstadoCurso.ACTIVO);
        curso1.setProfesor(createdProfessor);
        curso1 = cursoRepository.save(curso1);

        Curso curso2 = new Curso();
        curso2.setNombre("Física I");
        curso2.setCodigo("FIS001");
        curso2.setDescripcion("Física básica");
        curso2.setEstado(EstadoCurso.ACTIVO);
        curso2.setProfesor(createdProfessor);
        curso2 = cursoRepository.save(curso2);

        // Create multiple students
        Student student1 = new Student();
        student1.setNombre("Student One");
        student1.setCodigo("STUONE001");
        student1.setEmail("student.one@test.com");
        student1.setPassword("password123");
        Student createdStudent1 = studentService.save(student1);

        Student student2 = new Student();
        student2.setNombre("Student Two");
        student2.setCodigo("STUTWO001");
        student2.setEmail("student.two@test.com");
        student2.setPassword("password123");
        Student createdStudent2 = studentService.save(student2);

        // Assign students to courses
        StudentCurso assignment1 = studentCursoService.assignCursoToStudent(createdStudent1.getId(), curso1.getId());
        StudentCurso assignment2 = studentCursoService.assignCursoToStudent(createdStudent1.getId(), curso2.getId());
        StudentCurso assignment3 = studentCursoService.assignCursoToStudent(createdStudent2.getId(), curso1.getId());

        // Verify assignments
        List<StudentCurso> student1Assignments = studentCursoService.findByStudentId(createdStudent1.getId());
        assertThat(student1Assignments).hasSize(2);

        List<StudentCurso> student2Assignments = studentCursoService.findByStudentId(createdStudent2.getId());
        assertThat(student2Assignments).hasSize(1);

        // Verify course enrollments
        assertThat(studentCursoService.hasActiveAsignacionesByCursoId(curso1.getId())).isTrue();
        assertThat(studentCursoService.hasActiveAsignacionesByCursoId(curso2.getId())).isTrue();

        // Get all assignments with details
        List<StudentCurso> allWithDetails = studentCursoService.findAllWithDetails();
        assertThat(allWithDetails).hasSizeGreaterThanOrEqualTo(3);

        // Verify that details are loaded
        StudentCurso detailAssignment = allWithDetails.stream()
            .filter(a -> a.getId().equals(assignment1.getId()))
            .findFirst().orElseThrow();
        assertThat(detailAssignment.getStudent()).isNotNull();
        assertThat(detailAssignment.getCurso()).isNotNull();
        assertThat(detailAssignment.getCurso().getProfesor()).isNotNull();
    }
}