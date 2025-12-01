package com.example.demo.repository;

import com.example.demo.model.StudentCurso;
import com.example.demo.model.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.example.demo.model.Student;
import com.example.demo.model.Curso;

@Repository
public interface StudentCursoRepository extends JpaRepository<StudentCurso, Long> {
    List<StudentCurso> findByStudentId(Long studentId);

    List<StudentCurso> findByCursoId(Long cursoId);

    List<StudentCurso> findByEstado(String estado);

    List<StudentCurso> findByStudent(Student student);

    List<StudentCurso> findByCurso(Curso curso);

    @Query("SELECT sc FROM StudentCurso sc JOIN FETCH sc.student s JOIN FETCH sc.curso c LEFT JOIN FETCH c.profesor p WHERE sc.estado = 'ACTIVO'")
    List<StudentCurso> findAllWithDetails();

    boolean existsByStudentIdAndEstado(Long studentId, EstadoAsignacion estado);

    boolean existsByCursoIdAndEstado(Long cursoId, EstadoAsignacion estado);

    long countByEstado(EstadoAsignacion estado);
}