package com.example.tripto.controller;

import com.example.tripto.model.Rating;
import com.example.tripto.model.User;
import com.example.tripto.repository.RatingRepository;
import com.example.tripto.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class ProfileController {

    private final UserRepository userRepo;
    private final RatingRepository ratingRepo;

    public ProfileController(UserRepository userRepo, RatingRepository ratingRepo) {

        this.userRepo = userRepo;
        this.ratingRepo = ratingRepo;

    }

    @GetMapping("/profile/{id}")
    public String viewProfile(@PathVariable Long id, Model model, HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        User user = userRepo.findById(id).orElse(null);
        if (user == null) {

            return "redirect:/register";

        }

        List<Rating> receivedRatings = ratingRepo.findByRatedUserId(id);

        double averageScore = 0.0;
        if (!receivedRatings.isEmpty()) {

            int total = 0;
            for (Rating r : receivedRatings) {

                total += r.getScore();

            }

            averageScore = (double) total / receivedRatings.size();

        }

        double successRate = 0.0;
        if (!receivedRatings.isEmpty()) {

            int successCount = 0;
            for (Rating r : receivedRatings) {

                if (r.isSuccessful()) {

                    successCount = successCount + 1;

                }

            }

            successRate = ((double) successCount / receivedRatings.size());

        }

        model.addAttribute("user", user);
        model.addAttribute("receivedRatings", receivedRatings);
        model.addAttribute("averageScore", averageScore);
        model.addAttribute("successRate", successRate);
        model.addAttribute("currentUserId", currentUserId);

        return "profile";

    }

    @GetMapping("/profile/{id}/edit")
    public String editProfile(@PathVariable Long id, Model model, HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        if (!currentUserId.equals(id)) {

            return "redirect:/profile/" + currentUserId;

        }

        User user = userRepo.findById(id).orElse(null);
        if (user == null) {

            return "redirect:/register";

        }

        model.addAttribute("user", user);
        return "profile_edit";

    }

    @PostMapping("/profile/{id}/edit")
    public String updateProfile(@PathVariable Long id, @ModelAttribute User updatedData, HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        if (!currentUserId.equals(id)) {

            return "redirect:/profile/" + currentUserId;

        }

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
    public String uploadPhoto(@PathVariable Long id, @RequestParam("photo") MultipartFile file, HttpSession session) throws IOException {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        if (!currentUserId.equals(id)) {

            return "redirect:/profile/" + currentUserId;

        }

        User user = userRepo.findById(id).orElse(null);
        if (user == null) {

            return "redirect:/register";

        }

        if (!file.isEmpty()) {

            String rootPath = System.getProperty("user.dir");
            File uploadDir = new File(rootPath, "uploads");

            if (!uploadDir.exists()) {

                uploadDir.mkdirs();

            }

            String fileName = "user_" + id + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir, fileName);

            file.transferTo(dest);

            user.setProfilePicture(fileName);
            userRepo.save(user);

        }

        return "redirect:/profile/" + id;

    }

}