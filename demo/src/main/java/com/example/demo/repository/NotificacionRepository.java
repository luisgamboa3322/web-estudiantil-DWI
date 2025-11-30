package com.example.demo.repository;

import com.example.demo.model.Notificacion;
import com.example.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByStudentOrderByFechaCreacionDesc(Student student);

    List<Notificacion> findByStudentAndLeidaFalseOrderByFechaCreacionDesc(Student student);

    long countByStudentAndLeidaFalse(Student student);
}
