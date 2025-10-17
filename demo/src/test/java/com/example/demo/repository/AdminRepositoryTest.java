package com.example.demo.repository;

import com.example.demo.model.Admin;
import com.example.demo.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    void shouldSaveAndFindAdmin() {
        // Given
        Admin admin = new Admin();
        admin.setNombre("Admin Test");
        admin.setCodigo("ADM001");
        admin.setEmail("admin@test.com");
        admin.setPassword("password123");

        // When
        Admin savedAdmin = adminRepository.save(admin);

        // Then
        assertThat(savedAdmin.getId()).isNotNull();
        assertThat(savedAdmin.getNombre()).isEqualTo("Admin Test");
        assertThat(savedAdmin.getCodigo()).isEqualTo("ADM001");
        assertThat(savedAdmin.getEmail()).isEqualTo("admin@test.com");
    }

    @Test
    void shouldFindAdminByEmail() {
        // Given
        Admin admin = new Admin();
        admin.setNombre("Admin Test");
        admin.setCodigo("ADM001");
        admin.setEmail("admin@test.com");
        admin.setPassword("password123");
        adminRepository.save(admin);

        // When
        Optional<Admin> foundAdmin = adminRepository.findByEmail("admin@test.com");

        // Then
        assertThat(foundAdmin).isPresent();
        assertThat(foundAdmin.get().getEmail()).isEqualTo("admin@test.com");
    }

    @Test
    void shouldCheckExistsByCodigo() {
        // Given
        Admin admin = new Admin();
        admin.setNombre("Admin Test");
        admin.setCodigo("ADM001");
        admin.setEmail("admin@test.com");
        admin.setPassword("password123");
        adminRepository.save(admin);

        // When & Then
        assertThat(adminRepository.existsByCodigo("ADM001")).isTrue();
        assertThat(adminRepository.existsByCodigo("ADM002")).isFalse();
    }

    @Test
    void shouldCheckExistsByEmail() {
        // Given
        Admin admin = new Admin();
        admin.setNombre("Admin Test");
        admin.setCodigo("ADM001");
        admin.setEmail("admin@test.com");
        admin.setPassword("password123");
        adminRepository.save(admin);

        // When & Then
        assertThat(adminRepository.existsByEmail("admin@test.com")).isTrue();
        assertThat(adminRepository.existsByEmail("other@test.com")).isFalse();
    }

    @Test
    void shouldFindAllAdmins() {
        // Given
        Admin admin1 = new Admin();
        admin1.setNombre("Admin 1");
        admin1.setCodigo("ADM001");
        admin1.setEmail("admin1@test.com");
        admin1.setPassword("password123");

        Admin admin2 = new Admin();
        admin2.setNombre("Admin 2");
        admin2.setCodigo("ADM002");
        admin2.setEmail("admin2@test.com");
        admin2.setPassword("password123");

        adminRepository.save(admin1);
        adminRepository.save(admin2);

        // When
        var admins = adminRepository.findAll();

        // Then
        assertThat(admins).hasSizeGreaterThanOrEqualTo(2);
    }
}