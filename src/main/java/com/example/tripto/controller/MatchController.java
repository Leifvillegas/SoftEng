package com.example.tripto.controller;

import com.example.tripto.model.User;
import com.example.tripto.repository.UserRepository;
import com.example.tripto.service.SimpleMatchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.MatchResult;

@Controller
public class MatchController {

    private final UserRepository userRepo;
    private final SimpleMatchService matchService;

    public MatchController(UserRepository userRepo, SimpleMatchService matchService) {
        this.userRepo = userRepo;
        this.matchService = matchService;
    }

    @GetMapping("/matches/{userId}")
    public String showMatches(@PathVariable Long userId, Model model) {

        User current = userRepo.findById(userId).orElse(null);
        if(current == null) {
            return "redirect:/";
        }

        List<User> allUsers = userRepo.findAll();
        List<MatchResult> results = new ArrayList<>();

        for (User other : allUsers) {
            if(!other.getId().equals(current.getId())) {
                int score = matchService.compatibilityPercent(current, other);
                results.add(new MatchResult(other, score));
            }
        }

        results.sort(Comparator.comparingInt(MatchResult::getScorePercent).reversed());

        model.addAttribute("currentUser", current);
        model.addAttribute("matches", results);
        return "matches";

    }

    public class MatchResult {

        private final User user;
        private final int scorePercent;

        public MatchResult(User user, int scorePercent) {
            this.user = user;
            this.scorePercent = scorePercent;

        }

        public User getUser() {
            return user;
        }

        public int  getScorePercent() {
            return scorePercent;
        }

    }

}
