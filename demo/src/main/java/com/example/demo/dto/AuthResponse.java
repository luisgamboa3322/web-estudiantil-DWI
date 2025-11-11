package com.example.demo.dto;

import java.util.Set;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private String nombre;
    private Set<String> roles;
    private Set<String> permissions;
    private String userType; // "ADMIN", "TEACHER", "STUDENT"
    private boolean multiRole; // Para usuarios con múltiples roles

    public AuthResponse() {}

    public AuthResponse(String email, String nombre, Set<String> roles, 
                       Set<String> permissions, String userType) {
        this.email = email;
        this.nombre = nombre;
        this.roles = roles;
        this.permissions = permissions;
        this.userType = userType;
        this.multiRole = roles != null && roles.size() > 1;
    }

    // Getters y setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isMultiRole() {
        return multiRole;
    }

    public void setMultiRole(boolean multiRole) {
        this.multiRole = multiRole;
    }
}