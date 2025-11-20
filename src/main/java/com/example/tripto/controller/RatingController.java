package com.example.tripto.controller;

import com.example.tripto.model.Rating;
import com.example.tripto.model.Trip;
import com.example.tripto.model.User;
import com.example.tripto.repository.RatingRepository;
import com.example.tripto.repository.TripRepository;
import com.example.tripto.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RatingController {

    private final RatingRepository ratingRepo;
    private final UserRepository userRepo;
    private final TripRepository tripRepo;

    public RatingController(RatingRepository ratingRepo, UserRepository userRepo, TripRepository tripRepo) {

        this.ratingRepo = ratingRepo;
        this.userRepo = userRepo;
        this.tripRepo = tripRepo;

    }

    @GetMapping("/trips/{tripId}/rate") public String showRatingForm(@PathVariable Long tripId, @RequestParam("userId") Long userId, Model model) {

        Trip trip = tripRepo.findById(tripId).orElse(null);
        if (trip == null) {

            return "redirect:/trips?userId=" + userId;

        }

        User rater = userRepo.findById(userId).orElse(null);
        if (rater == null) {

            return "redirect:/register";

        }

        Rating rating = new Rating();

        model.addAttribute("trip", trip);
        model.addAttribute("rater", rater);
        model.addAttribute("rating", rating);
        model.addAttribute("userId", userId);

        return "rating_form";
    }

    @PostMapping("/trips/{tripId}/rate")
    public String submitRating(@PathVariable Long tripId, @RequestParam("userId") Long userId, @RequestParam("ratedUserId") Long ratedUserId, @ModelAttribute Rating rating,
                               RedirectAttributes redirectAttributes) {

        Trip trip = tripRepo.findById(tripId).orElse(null);
        if (trip == null) {

            redirectAttributes.addFlashAttribute("error", "Trip not found.");
            return "redirect:/trips?userId=" + userId;

        }

        User rater = userRepo.findById(userId).orElse(null);
        if (rater == null) {

            return "redirect:/register";

        }

        User ratedUser = userRepo.findById(ratedUserId).orElse(null);
        if (ratedUser == null) {

            redirectAttributes.addFlashAttribute("error", "User to rate not found.");
            return "redirect:/trips?userId=" + userId;

        }

        if (userId.equals(ratedUserId)) {

            redirectAttributes.addFlashAttribute("error", "You cannot rate yourself.");
            return "redirect:/profile/" + userId;

        }

        rating.setTrip(trip);
        rating.setRater(rater);
        rating.setRatedUser(ratedUser);

        if (rating.getScore() < 1) {

            rating.setScore(1);

        }

        if (rating.getScore() > 5) {

            rating.setScore(5);

        }

        ratingRepo.save(rating);

        redirectAttributes.addFlashAttribute("message", "Rating submitted!");

        return "redirect:/profile/" + ratedUserId;
    }
}
