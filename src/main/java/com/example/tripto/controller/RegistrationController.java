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

        if(user.getEmail() != null && userRepo.existsByEmail(user.getEmail())) {
            model.addAttribute("user", user);
            model.addAttribute("emailError", "Email already exists!");
            return "register";
        }

        if (userRepo.existsByEmail(user.getEmail().trim())) {
            model.addAttribute("user", user);
            model.addAttribute("emailError", "Email is already registered");
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

        User saved = userRepo.save(user);

        return "redirect:/personality_test?userId=" + saved.getId();

    }

}
