package com.example.demo.service;

import com.example.demo.model.Admin;
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

    private final AdminService adminService;
    private final StudentService studentService;
    private final ProfessorService professorService;

    public CustomUserDetailsService(AdminService adminService, StudentService studentService, ProfessorService professorService) {
        this.adminService = adminService;
        this.studentService = studentService;
        this.professorService = professorService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // intenta Admin
        Optional<Admin> aOpt = adminService.findByEmail(username);
        if (aOpt.isPresent()) {
            Admin a = aOpt.get();
            return User.withUsername(a.getEmail())
                        .password(a.getPassword())
                        .roles("ADMIN")
                        .build();
        }

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