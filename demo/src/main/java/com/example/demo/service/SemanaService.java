package com.example.demo.service;

import com.example.demo.model.Semana;
import com.example.demo.model.Curso;
import com.example.demo.repository.SemanaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SemanaService {

    @Autowired
    private SemanaRepository semanaRepository;

    public List<Semana> findAll() {
        return semanaRepository.findAll();
    }

    public Optional<Semana> findById(Long id) {
        return semanaRepository.findById(id);
    }

    public List<Semana> findByCurso(Curso curso) {
        return semanaRepository.findByCursoOrderByNumeroSemana(curso);
    }

    public List<Semana> findByCursoId(Long cursoId) {
        return semanaRepository.findByCursoIdOrderByNumeroSemana(cursoId);
    }

    public Semana save(Semana semana) {
        return semanaRepository.save(semana);
    }

    public Semana createSemana(int numeroSemana, String titulo, String descripcion, Curso curso) {
        if (semanaRepository.existsByCursoAndNumeroSemana(curso, numeroSemana)) {
            throw new IllegalArgumentException("Ya existe una semana con el número " + numeroSemana + " para este curso");
        }
        Semana semana = new Semana(numeroSemana, titulo, descripcion, curso);
        return save(semana);
    }

    public Semana update(Long id, Semana semanaDetails) {
        Semana semana = findById(id).orElseThrow(() -> new IllegalArgumentException("Semana no encontrada"));
        semana.setNumeroSemana(semanaDetails.getNumeroSemana());
        semana.setTitulo(semanaDetails.getTitulo());
        semana.setDescripcion(semanaDetails.getDescripcion());
        return save(semana);
    }

    public void delete(Long id) {
        if (!semanaRepository.existsById(id)) {
            throw new IllegalArgumentException("Semana no encontrada");
        }
        semanaRepository.deleteById(id);
    }
}