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
}