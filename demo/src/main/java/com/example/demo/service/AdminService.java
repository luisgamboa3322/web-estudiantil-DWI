package com.example.demo.service;

import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository repo;
    private final PasswordEncoder encoder;

    public AdminService(AdminRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public Admin create(Admin a) {
        if (repo.existsByCodigo(a.getCodigo())) {
            throw new IllegalStateException("Código ya existe");
        }
        if (repo.existsByEmail(a.getEmail())) {
            throw new IllegalStateException("Email ya existe");
        }
        // codifica la contraseña antes de guardar
        a.setPassword(encoder.encode(a.getPassword()));
        return repo.save(a);
    }

    public Optional<Admin> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public List<Admin> findAll() {
        return repo.findAll();
    }

    public Optional<Admin> findById(Long id) {
        return repo.findById(id);
    }

    public Admin update(Long id, Admin changes) {
        Admin existing = repo.findById(id).orElseThrow(() -> new IllegalStateException("Administrador no encontrado"));
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
        if (!repo.existsById(id)) throw new IllegalStateException("Administrador no encontrado");
        repo.deleteById(id);
    }
}