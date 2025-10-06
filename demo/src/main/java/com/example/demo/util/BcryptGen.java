package com.example.demo.util;

public class BcryptGen {
    public static void main(String[] args) {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder e =
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        // reemplaza "tuPassword" por la contraseña que quieras hashear
        System.out.println(e.encode("admin123"));
    }
}
