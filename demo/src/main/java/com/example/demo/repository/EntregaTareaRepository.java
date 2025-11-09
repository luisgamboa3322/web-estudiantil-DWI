package com.example.demo.repository;

import com.example.demo.model.EntregaTarea;
import com.example.demo.model.Tarea;
import com.example.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntregaTareaRepository extends JpaRepository<EntregaTarea, Long> {
    List<EntregaTarea> findByTarea(Tarea tarea);
    List<EntregaTarea> findByTareaId(Long tareaId);
    List<EntregaTarea> findByStudent(Student student);
    List<EntregaTarea> findByStudentId(Long studentId);
    Optional<EntregaTarea> findByTareaAndStudent(Tarea tarea, Student student);
    Optional<EntregaTarea> findByTareaIdAndStudentId(Long tareaId, Long studentId);
    boolean existsByTareaAndStudent(Tarea tarea, Student student);
}