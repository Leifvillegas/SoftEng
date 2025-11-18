package com.example.tripto.controller;

import com.example.tripto.model.User;
import com.example.tripto.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Controller
public class ProfileController {

    private final UserRepository userRepo;

    public ProfileController(UserRepository userRepo) {

        this.userRepo = userRepo;

    }

    @GetMapping("/profile/{userId}")
    public String viewProfile(@PathVariable Long userId, Model model) {

        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {

            return "redirect:/register";

        }

        model.addAttribute("user", user);
        model.addAttribute("currentUserId", id);

        return "profile";

    }

    @GetMapping("/profile/{id}/edit")
    public String updateProfile(@PathVariable Long id, @ModelAttribute User formUser, @RequestParam(name = "photoFile", required = false) MultipartFile photoFile) {

        User user = userRepo.findById(id).orElse(null);
        if (user == null) {

            return "redirect:/register";

        }

        user.setName(formUser.getName());
        user.setEmail(formUser.getEmail());
        user.setInterests(formUser.getInterests());
        user.setLanguages(formUser.getLanguages());
        user.setTravelStyle(formUser.getTravelStyle());

        if (photoFile != null && !photoFile.isEmpty()) {

            try {

                Path uploadDir = Paths.get("uploads");
                if (!Files.exists(uploadDir)) {

                    Files.createDirectories(uploadDir);

                }

                String originalName = photoFile.getOriginalFilename();
                if (originalName == null) {

                    originalName = "photo";

                }

                String fileName = "user-" + id + "-" + originalName,replaceAll("\\s+", "_");


            }

        }

    }

}
