package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentPath")
    public String populateCurrentPath(HttpServletRequest request) {
        if (request == null) return "";
        String uri = request.getRequestURI();
        return uri != null ? uri : "";
    }
}
