package com.example.demo.repository;

import com.example.demo.model.StudentCurso;
import com.example.demo.model.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentCursoRepository extends JpaRepository<StudentCurso, Long> {
    List<StudentCurso> findByStudentId(Long studentId);
    List<StudentCurso> findByCursoId(Long cursoId);
    List<StudentCurso> findByEstado(String estado);

    @Query("SELECT sc FROM StudentCurso sc JOIN FETCH sc.student s JOIN FETCH sc.curso c LEFT JOIN FETCH c.profesor p")
    List<StudentCurso> findAllWithDetails();

    boolean existsByStudentIdAndEstado(Long studentId, EstadoAsignacion estado);
    boolean existsByCursoIdAndEstado(Long cursoId, EstadoAsignacion estado);
}