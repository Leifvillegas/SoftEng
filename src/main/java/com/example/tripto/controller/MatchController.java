package com.example.tripto.controller;

import com.example.tripto.model.User;
import com.example.tripto.repository.UserRepository;
import com.example.tripto.service.SimpleMatchService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class MatchController {

    private final UserRepository userRepo;
    private final SimpleMatchService matchService;

    public MatchController(UserRepository userRepo, SimpleMatchService matchService) {

        this.userRepo = userRepo;
        this.matchService = matchService;

    }

    @GetMapping("/matches/{userId}")
    public String showMatches(@PathVariable Long userId, Model model, HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }


        if (!currentUserId.equals(userId)) {

            return "redirect:/matches/" + currentUserId;

        }

        User current = userRepo.findById(userId).orElse(null);
        if (current == null) {

            return "redirect:/register";

        }

        List<User> allUsers = userRepo.findAll();
        List<MatchRow> allResults = new ArrayList<>();

        for (User other : allUsers) {

            if (!other.getId().equals(current.getId())) {

                int score = matchService.compatibilityPercent(current, other);
                allResults.add(new MatchRow(other, score));

            }
        }

        allResults.sort(Comparator.comparingInt(MatchRow::score).reversed());

        String tier = current.getTier();
        boolean isPremium = tier != null && tier.equalsIgnoreCase("PREMIUM");

        List<MatchRow> visible;

        if (isPremium) {

            visible = allResults;

        } else {

            visible = (allResults.size() > 3) ? new ArrayList<>(allResults.subList(0, 3)) : allResults;

        }

        model.addAttribute("currentUser", current);
        model.addAttribute("matches", visible);
        model.addAttribute("isPremium", isPremium);

        return "matches";
    }

    public record MatchRow(User user, int score) {



    }

}