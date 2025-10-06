package com.example.demo.service;

import com.example.demo.model.Professor;
import com.example.demo.repository.ProfessorRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {

    private final ProfessorRepository repository;
    private final PasswordEncoder encoder;


    public ProfessorService(ProfessorRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public List<Professor> getAll() {
        return repository.findAll();
    }

    public Optional<Professor> getById(Long id) {
        return repository.findById(id);
    }

    public Professor create(Professor p) {
        if (repository.existsByCodigo(p.getCodigo())) {
            throw new IllegalStateException("Código ya existe");
        }
        if (repository.existsByEmail(p.getEmail())) {
            throw new IllegalStateException("Email ya existe");
        }
        // codifica la contraseña antes de guardar
        p.setPassword(encoder.encode(p.getPassword()));
        return repository.save(p);
    }

    public Optional<Professor> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Professor update(Long id, Professor changes) {
        Professor existing = repository.findById(id).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));
        if (changes.getNombre() != null) existing.setNombre(changes.getNombre());
        if (changes.getCodigo() != null && !changes.getCodigo().equals(existing.getCodigo())) {
            if (repository.existsByCodigo(changes.getCodigo())) throw new IllegalStateException("Código ya existe");
            existing.setCodigo(changes.getCodigo());
        }
        if (changes.getEmail() != null && !changes.getEmail().equals(existing.getEmail())) {
            if (repository.existsByEmail(changes.getEmail())) throw new IllegalStateException("Email ya existe");
            existing.setEmail(changes.getEmail());
        }
        if (changes.getPassword() != null) {
            // codificar contraseña si se actualiza
            existing.setPassword(encoder.encode(changes.getPassword()));
        }
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new IllegalStateException("Profesor no encontrado");
        repository.deleteById(id);
    }

     public List<Professor> findAll() {
        return repository.findAll();
    }
}
