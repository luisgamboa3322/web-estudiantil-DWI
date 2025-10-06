package com.example.demo.service;

import com.example.demo.model.Student;
import com.example.demo.model.Professor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentService studentService;
    private final ProfessorService professorService;

    public CustomUserDetailsService(StudentService studentService, ProfessorService professorService) {
        this.studentService = studentService;
        this.professorService = professorService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // intenta Student
        Optional<Student> sOpt = studentService.findByEmail(username);
        if (sOpt.isPresent()) {
            Student s = sOpt.get();
            return User.withUsername(s.getEmail())
                       .password(s.getPassword())
                       .roles("STUDENT")
                       .build();
        }

        // intenta Professor
        Optional<Professor> pOpt = professorService.findByEmail(username);
        if (pOpt.isPresent()) {
            Professor p = pOpt.get();
            return User.withUsername(p.getEmail())
                       .password(p.getPassword())
                       .roles("TEACHER") // o "PROFESSOR" según convención
                       .build();
        }

        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}