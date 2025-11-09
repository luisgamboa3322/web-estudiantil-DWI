package com.example.demo.service;

import com.example.demo.model.Tarea;
import com.example.demo.model.Semana;
import com.example.demo.model.Professor;
import com.example.demo.repository.TareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;

    public List<Tarea> findAll() {
        return tareaRepository.findAll();
    }

    public Optional<Tarea> findById(Long id) {
        return tareaRepository.findById(id);
    }

    public List<Tarea> findBySemana(Semana semana) {
        return tareaRepository.findBySemanaOrderByFechaCreacion(semana);
    }

    public List<Tarea> findBySemanaId(Long semanaId) {
        return tareaRepository.findBySemanaIdOrderByFechaCreacion(semanaId);
    }

    public List<Tarea> findByProfesor(Professor profesor) {
        return tareaRepository.findByProfesor(profesor);
    }

    public List<Tarea> findTareasVencidas() {
        return tareaRepository.findByFechaLimiteBefore(LocalDateTime.now());
    }

    public List<Tarea> findTareasPendientes() {
        return tareaRepository.findByFechaLimiteAfter(LocalDateTime.now());
    }

    public Tarea save(Tarea tarea) {
        return tareaRepository.save(tarea);
    }

    public Tarea createTarea(String titulo, String descripcion, LocalDateTime fechaLimite, int puntosMaximos, Semana semana, Professor profesor) {
        Tarea tarea = new Tarea(titulo, descripcion, fechaLimite, puntosMaximos, semana, profesor);
        return save(tarea);
    }

    public Tarea update(Long id, Tarea tareaDetails) {
        Tarea tarea = findById(id).orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
        tarea.setTitulo(tareaDetails.getTitulo());
        tarea.setDescripcion(tareaDetails.getDescripcion());
        tarea.setFechaLimite(tareaDetails.getFechaLimite());
        tarea.setPuntosMaximos(tareaDetails.getPuntosMaximos());
        return save(tarea);
    }

    public void delete(Long id) {
        if (!tareaRepository.existsById(id)) {
            throw new IllegalArgumentException("Tarea no encontrada");
        }
        tareaRepository.deleteById(id);
    }
}