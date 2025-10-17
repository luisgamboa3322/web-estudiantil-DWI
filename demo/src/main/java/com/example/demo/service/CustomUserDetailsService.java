package com.example.demo.service;

import com.example.demo.model.Admin;
import com.example.demo.model.Student;
import com.example.demo.model.Professor;
import com.example.demo.model.Role;
import com.example.demo.model.Permission;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        Set<String> authorities = new HashSet<>();

        // intenta Admin
        Optional<Admin> aOpt = adminService.findByEmail(username);
        if (aOpt.isPresent()) {
            Admin a = aOpt.get();
            if (a.getRoles() != null) {
                for (Role role : a.getRoles()) {
                    authorities.add("ROLE_" + role.getName().toUpperCase());
                    if (role.getPermissions() != null) {
                        for (Permission perm : role.getPermissions()) {
                            authorities.add(perm.getName());
                        }
                    }
                }
            }
            return User.withUsername(a.getEmail())
                         .password(a.getPassword())
                         .authorities(authorities.toArray(new String[0]))
                         .build();
        }

        // intenta Student
        Optional<Student> sOpt = studentService.findByEmail(username);
        if (sOpt.isPresent()) {
            Student s = sOpt.get();
            if (s.getRoles() != null) {
                for (Role role : s.getRoles()) {
                    authorities.add("ROLE_" + role.getName().toUpperCase());
                    if (role.getPermissions() != null) {
                        for (Permission perm : role.getPermissions()) {
                            authorities.add(perm.getName());
                        }
                    }
                }
            }
            return User.withUsername(s.getEmail())
                         .password(s.getPassword())
                         .authorities(authorities.toArray(new String[0]))
                         .build();
        }

        // intenta Professor
        Optional<Professor> pOpt = professorService.findByEmail(username);
        if (pOpt.isPresent()) {
            Professor p = pOpt.get();
            if (p.getRoles() != null) {
                for (Role role : p.getRoles()) {
                    authorities.add("ROLE_" + role.getName().toUpperCase());
                    if (role.getPermissions() != null) {
                        for (Permission perm : role.getPermissions()) {
                            authorities.add(perm.getName());
                        }
                    }
                }
            }
            return User.withUsername(p.getEmail())
                         .password(p.getPassword())
                         .authorities(authorities.toArray(new String[0]))
                         .build();
        }

        // Si es admin y no se encontró como profesor, intentar como admin
        if (username.equals("admin@example.com")) {
            Optional<Admin> adminOpt = adminService.findByEmail(username);
            if (adminOpt.isPresent()) {
                Admin a = adminOpt.get();
                if (a.getRoles() != null) {
                    for (Role role : a.getRoles()) {
                        authorities.add("ROLE_" + role.getName().toUpperCase());
                        if (role.getPermissions() != null) {
                            for (Permission perm : role.getPermissions()) {
                                authorities.add(perm.getName());
                            }
                        }
                    }
                }
                return User.withUsername(a.getEmail())
                             .password(a.getPassword())
                             .authorities(authorities.toArray(new String[0]))
                             .build();
            }
        }

        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}