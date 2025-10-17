package com.example.demo.service;

import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldCreateAdminSuccessfully() {
        // Given
        Admin admin = new Admin();
        admin.setNombre("Admin Test");
        admin.setCodigo("ADM001");
        admin.setEmail("admin@test.com");
        admin.setPassword("password123");

        // When
        Admin createdAdmin = adminService.create(admin);

        // Then
        assertThat(createdAdmin.getId()).isNotNull();
        assertThat(createdAdmin.getNombre()).isEqualTo("Admin Test");
        assertThat(createdAdmin.getCodigo()).isEqualTo("ADM001");
        assertThat(createdAdmin.getEmail()).isEqualTo("admin@test.com");
        assertThat(passwordEncoder.matches("password123", createdAdmin.getPassword())).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenCreatingAdminWithExistingCodigo() {
        // Given
        Admin admin1 = new Admin();
        admin1.setNombre("Admin 1");
        admin1.setCodigo("ADM001");
        admin1.setEmail("admin1@test.com");
        admin1.setPassword("password123");
        adminService.create(admin1);

        Admin admin2 = new Admin();
        admin2.setNombre("Admin 2");
        admin2.setCodigo("ADM001"); // Same codigo
        admin2.setEmail("admin2@test.com");
        admin2.setPassword("password123");

        // When & Then
        assertThatThrownBy(() -> adminService.create(admin2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Código ya existe");
    }

    @Test
    void shouldThrowExceptionWhenCreatingAdminWithExistingEmail() {
        // Given
        Admin admin1 = new Admin();
        admin1.setNombre("Admin 1");
        admin1.setCodigo("ADM001");
        admin1.setEmail("admin@test.com");
        admin1.setPassword("password123");
        adminService.create(admin1);

        Admin admin2 = new Admin();
        admin2.setNombre("Admin 2");
        admin2.setCodigo("ADM002");
        admin2.setEmail("admin@test.com"); // Same email
        admin2.setPassword("password123");

        // When & Then
        assertThatThrownBy(() -> adminService.create(admin2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Email ya existe");
    }

    @Test
    void shouldFindAdminByEmail() {
        // Given
        Admin admin = new Admin();
        admin.setNombre("Admin Test");
        admin.setCodigo("ADM001");
        admin.setEmail("admin@test.com");
        admin.setPassword("password123");
        adminService.create(admin);

        // When
        Optional<Admin> foundAdmin = adminService.findByEmail("admin@test.com");

        // Then
        assertThat(foundAdmin).isPresent();
        assertThat(foundAdmin.get().getEmail()).isEqualTo("admin@test.com");
    }

    @Test
    void shouldFindAllAdmins() {
        // Given
        Admin admin1 = new Admin();
        admin1.setNombre("Admin 1");
        admin1.setCodigo("ADM001");
        admin1.setEmail("admin1@test.com");
        admin1.setPassword("password123");
        adminService.create(admin1);

        Admin admin2 = new Admin();
        admin2.setNombre("Admin 2");
        admin2.setCodigo("ADM002");
        admin2.setEmail("admin2@test.com");
        admin2.setPassword("password123");
        adminService.create(admin2);

        // When
        List<Admin> admins = adminService.findAll();

        // Then
        assertThat(admins).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldUpdateAdminSuccessfully() {
        // Given
        Admin admin = new Admin();
        admin.setNombre("Admin Original");
        admin.setCodigo("ADM001");
        admin.setEmail("admin@test.com");
        admin.setPassword("password123");
        Admin createdAdmin = adminService.create(admin);

        Admin changes = new Admin();
        changes.setNombre("Admin Updated");
        changes.setEmail("admin.updated@test.com");

        // When
        Admin updatedAdmin = adminService.update(createdAdmin.getId(), changes);

        // Then
        assertThat(updatedAdmin.getNombre()).isEqualTo("Admin Updated");
        assertThat(updatedAdmin.getEmail()).isEqualTo("admin.updated@test.com");
        assertThat(updatedAdmin.getCodigo()).isEqualTo("ADM001"); // Unchanged
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToExistingEmail() {
        // Given
        Admin admin1 = new Admin();
        admin1.setNombre("Admin 1");
        admin1.setCodigo("ADM001");
        admin1.setEmail("admin1@test.com");
        admin1.setPassword("password123");
        Admin createdAdmin1 = adminService.create(admin1);

        Admin admin2 = new Admin();
        admin2.setNombre("Admin 2");
        admin2.setCodigo("ADM002");
        admin2.setEmail("admin2@test.com");
        admin2.setPassword("password123");
        Admin createdAdmin2 = adminService.create(admin2);

        Admin changes = new Admin();
        changes.setEmail("admin2@test.com"); // Existing email

        // When & Then
        assertThatThrownBy(() -> adminService.update(createdAdmin1.getId(), changes))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Email ya existe");
    }

    @Test
    void shouldDeleteAdminSuccessfully() {
        // Given
        Admin admin = new Admin();
        admin.setNombre("Admin Test");
        admin.setCodigo("ADM001");
        admin.setEmail("admin@test.com");
        admin.setPassword("password123");
        Admin createdAdmin = adminService.create(admin);

        // When
        adminService.delete(createdAdmin.getId());

        // Then
        assertThat(adminService.findById(createdAdmin.getId())).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentAdmin() {
        // When & Then
        assertThatThrownBy(() -> adminService.delete(999L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Administrador no encontrado");
    }
}