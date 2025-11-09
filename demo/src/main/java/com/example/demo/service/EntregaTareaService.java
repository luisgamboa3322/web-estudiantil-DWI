package com.example.demo.service;

import com.example.demo.model.EntregaTarea;
import com.example.demo.model.Tarea;
import com.example.demo.model.Student;
import com.example.demo.repository.EntregaTareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntregaTareaService {

    @Autowired
    private EntregaTareaRepository entregaTareaRepository;

    public List<EntregaTarea> findAll() {
        return entregaTareaRepository.findAll();
    }

    public Optional<EntregaTarea> findById(Long id) {
        return entregaTareaRepository.findById(id);
    }

    public List<EntregaTarea> findByTarea(Tarea tarea) {
        return entregaTareaRepository.findByTarea(tarea);
    }

    public List<EntregaTarea> findByTareaId(Long tareaId) {
        return entregaTareaRepository.findByTareaId(tareaId);
    }

    public List<EntregaTarea> findByStudent(Student student) {
        return entregaTareaRepository.findByStudent(student);
    }

    public List<EntregaTarea> findByStudentId(Long studentId) {
        return entregaTareaRepository.findByStudentId(studentId);
    }

    public Optional<EntregaTarea> findByTareaAndStudent(Tarea tarea, Student student) {
        return entregaTareaRepository.findByTareaAndStudent(tarea, student);
    }

    public Optional<EntregaTarea> findByTareaIdAndStudentId(Long tareaId, Long studentId) {
        return entregaTareaRepository.findByTareaIdAndStudentId(tareaId, studentId);
    }

    public boolean existsByTareaAndStudent(Tarea tarea, Student student) {
        return entregaTareaRepository.existsByTareaAndStudent(tarea, student);
    }

    public EntregaTarea save(EntregaTarea entregaTarea) {
        return entregaTareaRepository.save(entregaTarea);
    }

    public EntregaTarea entregarTarea(String contenido, Tarea tarea, Student student) {
        if (existsByTareaAndStudent(tarea, student)) {
            throw new IllegalArgumentException("El estudiante ya ha entregado esta tarea");
        }
        EntregaTarea entrega = new EntregaTarea(contenido, tarea, student);
        return save(entrega);
    }

    public EntregaTarea calificarEntrega(Long entregaId, int calificacion) {
        EntregaTarea entrega = findById(entregaId).orElseThrow(() -> new IllegalArgumentException("Entrega no encontrada"));
        if (calificacion < 0 || calificacion > entrega.getTarea().getPuntosMaximos()) {
            throw new IllegalArgumentException("La calificación debe estar entre 0 y " + entrega.getTarea().getPuntosMaximos());
        }
        entrega.setCalificacion(calificacion);
        return save(entrega);
    }

    public void delete(Long id) {
        if (!entregaTareaRepository.existsById(id)) {
            throw new IllegalArgumentException("Entrega no encontrada");
        }
        entregaTareaRepository.deleteById(id);
    }
}