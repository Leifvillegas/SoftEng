package com.example.tripto.controller;

import com.example.tripto.model.User;
import com.example.tripto.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PersonalityController {

    private final UserRepository userRepo;

    public PersonalityController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/personality_test")
    public String showQuiz(@RequestParam Long userId, Model model) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/register";
        }

        model.addAttribute("userId", userId);
        model.addAttribute("userName", user.getName());

        return "personality_test";

    }

    @PostMapping("/personality_test")
    public String submitQuiz(
            @RequestParam Long userId,
            @RequestParam int q1,
            @RequestParam int q2,
            @RequestParam int q3,
            @RequestParam int q4,
            @RequestParam int q5,
            @RequestParam int q6,
            @RequestParam int q7,
            @RequestParam int q8,
            Model model
    ) {

        User user = userRepo.findById(userId).orElse(null);
        if(user == null) {
            return "redirect:/register";
        }

        int socialScore = q1 + q3 + q5;
        String socialPart = (socialScore >= 9) ? "Social" : "Quiet";

        int plannerScore = q2 + q8;
        String planPart = (plannerScore >= 6) ? "Planner" : "Explorer";

        String personalityLabel = socialPart + " " + planPart;

        user.setPersonalityType(personalityLabel);
        userRepo.save(user);

        model.addAttribute("name", user.getName());
        model.addAttribute("personality", personalityLabel);

        return "personality_test";

    }

}
