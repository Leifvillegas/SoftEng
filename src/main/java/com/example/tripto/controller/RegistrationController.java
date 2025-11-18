package com.example.tripto.controller;

import com.example.tripto.model.User;
import com.example.tripto.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final UserRepository userRepo;

    public RegistrationController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/register")
    public String showForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, Model model) {

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            model.addAttribute("user", user);
            model.addAttribute("nameError", "Name is required");
            return "register";
        }

        if(user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            model.addAttribute("user", user);
            model.addAttribute("emailError", "Email is required");
            return "register";
        }

        String emailTrimmed = user.getEmail().trim();

        if (userRepo.existsByEmail(emailTrimmed)) {
            model.addAttribute("user", user);
            model.addAttribute("emailError", "Email already exists");
            return "register";
        }

        String raw = user.getPasswordHash();
        if(raw == null || raw.isEmpty()) {
            model.addAttribute("user", user);
            model.addAttribute("passwordError", "Password is required!");
            return "register";
        }

        String hashed = BCrypt.hashpw(raw, BCrypt.gensalt(10));
        user.setPasswordHash(hashed);

        if (user.getBio() != null) {
            user.setBio(user.getBio().trim());
        }

        if (user.getInterests() != null) {
            user.setInterests(user.getInterests().trim());
        }

        if (user.getLanguages() != null) {
            user.setLanguages(user.getLanguages().trim());
        }

        if (user.getTravelStyle() != null) {
            user.setTravelStyle(user.getTravelStyle().trim());
        }

        if (user.getTier() == null || user.getTier().isBlank()) {
            user.setTier("FREE");
        }

        user.setEmail(emailTrimmed);

        User saved = userRepo.save(user);

        return "redirect:/personality_test?userId=" + saved.getId();

    }

}
