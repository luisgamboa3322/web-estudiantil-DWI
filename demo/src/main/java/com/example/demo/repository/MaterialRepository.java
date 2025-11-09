package com.example.demo.repository;

import com.example.demo.model.Material;
import com.example.demo.model.Semana;
import com.example.demo.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findBySemanaOrderByFechaCreacion(Semana semana);
    List<Material> findBySemanaIdOrderByFechaCreacion(Long semanaId);
    List<Material> findByProfesor(Professor profesor);
}