package com.example.tripto.service;

import com.example.tripto.model.User;
import com.example.tripto.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final UserRepository users;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public RegistrationService(UserRepository users) { this.users = users; }

    public Long register(User body) {
        if (body.getEmail() == null || body.getEmail().isBlank())
            throw new IllegalArgumentException("Email is required");
        if (body.getPasswordHash() == null || body.getPasswordHash().length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters");
        if (users.existsByEmail(body.getEmail()))
            throw new IllegalArgumentException("Email already registered");

        String raw = body.getPasswordHash();
        body.setPasswordHash(encoder.encode(raw));

        if (body.getPremium() == null) body.setPremium(false);

        return users.save(body).getId();
    }
}
