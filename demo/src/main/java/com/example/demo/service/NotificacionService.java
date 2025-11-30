package com.example.demo.service;

import com.example.demo.model.Notificacion;
import com.example.demo.model.Student;
import com.example.demo.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    public List<Notificacion> getNotificacionesPorEstudiante(Student student) {
        return notificacionRepository.findByStudentOrderByFechaCreacionDesc(student);
    }

    public List<Notificacion> getNotificacionesNoLeidas(Student student) {
        return notificacionRepository.findByStudentAndLeidaFalseOrderByFechaCreacionDesc(student);
    }

    public long contarNoLeidas(Student student) {
        return notificacionRepository.countByStudentAndLeidaFalse(student);
    }

    @Transactional
    public Notificacion crearNotificacion(String titulo, String mensaje, Student student, String tipo, String link) {
        Notificacion notificacion = new Notificacion(titulo, mensaje, student, tipo);
        notificacion.setLink(link);
        return notificacionRepository.save(notificacion);
    }

    @Transactional
    public void marcarComoLeida(Long id) {
        notificacionRepository.findById(id).ifPresent(n -> {
            n.setLeida(true);
            notificacionRepository.save(n);
        });
    }

    @Transactional
    public void marcarTodasComoLeidas(Student student) {
        List<Notificacion> noLeidas = notificacionRepository
                .findByStudentAndLeidaFalseOrderByFechaCreacionDesc(student);
        for (Notificacion n : noLeidas) {
            n.setLeida(true);
        }
        notificacionRepository.saveAll(noLeidas);
    }
}
