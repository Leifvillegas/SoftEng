package com.example.tripto.controller;

import com.example.tripto.model.User;
import com.example.tripto.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {
    private final RegistrationService registrationService;

    public AuthApiController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User body) {
        try {
            Long id = registrationService.register(body);
            return ResponseEntity.status(201).body(new ApiResponse(id, "Registered"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiError("REGISTER_FAILED", ex.getMessage()));
        }
    }

    record ApiResponse(Long userId, String message) {}
    record ApiError(String code, String message) {}
}
