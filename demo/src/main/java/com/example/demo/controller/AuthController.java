package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.example.demo.dto.LoginRequest;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.util.JwtUtil;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth API funcionando correctamente");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Validar credenciales
            var user = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            
            if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                
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

                // Crear estructura de autoridades para el JWT
                Map<String, Object> authData = new HashMap<>();
                authData.put("role", role);
                authData.put("roles", roles);
                authData.put("permissions", permissions);
                authData.put("authorities", authorities);

                // Generar JWT token
                String token = jwtUtil.generateToken(user.getUsername(), user.getUsername(), authData);
                
                // Crear respuesta con información completa + JWT
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login exitoso");
                response.put("email", user.getUsername());
                response.put("token", token); // ✅ JWT TOKEN PARA ANGULAR
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
    public ResponseEntity<?> logout() {
        try {
            // Con JWT, el logout es manejado del lado del cliente eliminando el token
            return ResponseEntity.ok(Map.of("success", true, "message", "Logout exitoso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Error en logout: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Token no proporcionado"));
            }

            String token = authHeader.substring(7); // Remover "Bearer "
            
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Token inválido"));
            }

            // Obtener datos del token JWT
            String email = jwtUtil.getEmailFromToken(token);
            Map<String, Object> authData = jwtUtil.getAuthoritiesFromToken(token);

            // Crear respuesta con información del token
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("email", email);
            response.put("role", authData.get("role"));
            response.put("roles", authData.get("roles"));
            response.put("permissions", authData.get("permissions"));
            response.put("authorities", authData.get("authorities"));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Error obteniendo usuario: " + e.getMessage()));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);
                return ResponseEntity.ok(Map.of("authenticated", true, "email", email));
            }
        }
        return ResponseEntity.ok(Map.of("authenticated", false));
    }
}