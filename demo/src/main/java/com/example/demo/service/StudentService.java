package com.example.demo.service;

import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository repo;
    private final PasswordEncoder encoder;

    public StudentService(StudentRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public Student save(Student s) {
        // codifica la contraseña antes de guardar
        s.setPassword(encoder.encode(s.getPassword()));
        return repo.save(s);
    }

    public Optional<Student> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public List<Student> findAll() {
        return repo.findAll();
    }

    public Optional<Student> findById(Long id) {
        return repo.findById(id);
    }

    public Student update(Long id, Student changes) {
        Student existing = repo.findById(id).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));
        if (changes.getNombre() != null) existing.setNombre(changes.getNombre());
        if (changes.getCodigo() != null && !changes.getCodigo().equals(existing.getCodigo())) {
            if (repo.existsByCodigo(changes.getCodigo())) throw new IllegalStateException("Código ya existe");
            existing.setCodigo(changes.getCodigo());
        }
        if (changes.getEmail() != null && !changes.getEmail().equals(existing.getEmail())) {
            if (repo.existsByEmail(changes.getEmail())) throw new IllegalStateException("Email ya existe");
            existing.setEmail(changes.getEmail());
        }
        if (changes.getPassword() != null) {
            existing.setPassword(encoder.encode(changes.getPassword()));
        }
        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalStateException("Estudiante no encontrado");
        repo.deleteById(id);
    }
}