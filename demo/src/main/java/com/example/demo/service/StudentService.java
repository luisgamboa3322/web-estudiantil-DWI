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
}