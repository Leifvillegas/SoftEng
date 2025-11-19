package com.example.tripto.controller;

import com.example.tripto.model.User;
import com.example.tripto.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class ProfileController {

    private final UserRepository userRepo;

    public ProfileController(UserRepository userRepo) {

        this.userRepo = userRepo;

    }

    @GetMapping("/profile/{id}")
    public String viewProfile(@PathVariable Long id, Model model) {

        User user = userRepo.findById(id).orElse(null);
        if (user == null) {

            return "redirect:/register";

        }

        model.addAttribute("user", user);
        return "profile";

    }

    @GetMapping("/profile/{id}/edit")
    public String editProfile(@PathVariable Long id, Model model) {

        User user = userRepo.findById(id).orElse(null);
        if (user == null) {

            return "redirect:/register";

        }

        model.addAttribute("user", user);
        return "profile_edit";

    }

    @PostMapping("/profile/{id}/edit")
    public String updateProfile(@PathVariable Long id, @ModelAttribute User updatedData) {

        User user = userRepo.findById(id).orElse(null);
        if (user == null) {

            return "redirect:/register";

        }

        user.setName(updatedData.getName());
        user.setBio(updatedData.getBio());
        user.setInterests(updatedData.getInterests());
        user.setLanguages(updatedData.getLanguages());
        user.setTravelStyle(updatedData.getTravelStyle());

        userRepo.save(user);

        return "redirect:/profile/" + id;

    }

    @PostMapping("/profile/{id}/upload")
    public String uploadPhoto(@PathVariable Long id, @RequestParam("photo") MultipartFile file) throws IOException {

        User user = userRepo.findById(id).orElse(null);
        if (user == null) {

            return "redirect:/register";

        }

        if (!file.isEmpty()) {

            String uploadDir = "uploads/";
            String fileName =  "user_" + id + "_" + file.getOriginalFilename();

            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);

            user.setProfilePicture(fileName);
            userRepo.save(user);

        }

        return  "redirect:/profile/" + id;

    }



}