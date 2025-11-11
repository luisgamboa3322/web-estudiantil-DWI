package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.example.demo.dto.LoginRequest;
import com.example.demo.service.CustomUserDetailsService;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth API funcionando correctamente");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            // Validar credenciales
            var user = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            
            if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                // Crear sesión
                session.setAttribute("user", user);
                session.setAttribute("email", user.getUsername());
                
                // Obtener TODOS los roles y permisos
                var authorities = user.getAuthorities();
                var userRole = authorities.stream().findFirst().orElse(null);
                var role = userRole != null ? userRole.getAuthority() : "UNKNOWN";
                
                // Separar roles de permisos
                var roles = authorities.stream()
                    .filter(auth -> auth.getAuthority().startsWith("ROLE_"))
                    .map(auth -> auth.getAuthority())
                    .collect(java.util.stream.Collectors.toList());
                
                var permissions = authorities.stream()
                    .filter(auth -> !auth.getAuthority().startsWith("ROLE_"))
                    .map(auth -> auth.getAuthority())
                    .collect(java.util.stream.Collectors.toList());
                
                // Crear respuesta con información completa
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login exitoso");
                response.put("email", user.getUsername());
                response.put("role", role);
                response.put("roles", roles);
                response.put("permissions", permissions);
                response.put("authorities", authorities);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Credenciales inválidas"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Error en la autenticación: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        try {
            session.invalidate();
            return ResponseEntity.ok(Map.of("success", true, "message", "Logout exitoso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Error en logout: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        try {
            var email = (String) session.getAttribute("email");
            var user = (org.springframework.security.core.userdetails.User) session.getAttribute("user");
            
            if (email != null && user != null) {
                // Obtener TODOS los roles y permisos
                var authorities = user.getAuthorities();
                var userRole = authorities.stream().findFirst().orElse(null);
                var role = userRole != null ? userRole.getAuthority() : "UNKNOWN";
                
                // Separar roles de permisos
                var roles = authorities.stream()
                    .filter(auth -> auth.getAuthority().startsWith("ROLE_"))
                    .map(auth -> auth.getAuthority())
                    .collect(java.util.stream.Collectors.toList());
                
                var permissions = authorities.stream()
                    .filter(auth -> !auth.getAuthority().startsWith("ROLE_"))
                    .map(auth -> auth.getAuthority())
                    .collect(java.util.stream.Collectors.toList());
                
                // Crear respuesta con información completa
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("email", email);
                response.put("role", role);
                response.put("roles", roles);
                response.put("permissions", permissions);
                response.put("authorities", authorities);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "No autenticado"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Error obteniendo usuario: " + e.getMessage()));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkSession(HttpSession session) {
        var email = (String) session.getAttribute("email");
        if (email != null) {
            return ResponseEntity.ok(Map.of("authenticated", true, "email", email));
        } else {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }
    }
}