package com.example.tripto.controller;

import com.example.tripto.model.Rating;
import com.example.tripto.model.Trip;
import com.example.tripto.model.TripParticipant;
import com.example.tripto.model.User;
import com.example.tripto.repository.RatingRepository;
import com.example.tripto.repository.TripParticipantRepository;
import com.example.tripto.repository.TripRepository;
import com.example.tripto.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class RatingController {

    private final RatingRepository ratingRepo;
    private final UserRepository userRepo;
    private final TripRepository tripRepo;
    private final TripParticipantRepository tripParticipantRepo;

    public RatingController(RatingRepository ratingRepo, UserRepository userRepo, TripRepository tripRepo, TripParticipantRepository tripParticipantRepo) {

        this.ratingRepo = ratingRepo;
        this.userRepo = userRepo;
        this.tripRepo = tripRepo;
        this.tripParticipantRepo = tripParticipantRepo;
    }

    @GetMapping("/trips/{tripId}/rate")
    public String showRatingForm(@PathVariable Long tripId, @RequestParam("buddyId") Long buddyId, HttpSession session, Model model) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }


        Trip trip = tripRepo.findById(tripId).orElse(null);
        if (trip == null) {

            return "redirect:/trips";

        }

        User buddy = userRepo.findById(buddyId).orElse(null);
        if (buddy == null) {

            return "redirect:/register";

        }

        if (currentUserId.equals(buddyId)) {

            return "redirect:/profile/" + currentUserId;

        }

        Rating rating = new Rating();

        model.addAttribute("trip", trip);
        model.addAttribute("buddy", buddy);
        model.addAttribute("rating", rating);

        return "rating_form";
    }

    @PostMapping("/trips/{tripId}/rate")
    public String submitRating(@PathVariable Long tripId, @RequestParam("buddyId") Long buddyId, @ModelAttribute Rating rating, HttpSession session,
                               RedirectAttributes redirect) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        Trip trip = tripRepo.findById(tripId).orElse(null);
        if (trip == null) {

            redirect.addFlashAttribute("error", "Trip not found");
            return "redirect:/trips";

        }

        User rater = userRepo.findById(currentUserId).orElse(null);
        User ratedUser = userRepo.findById(buddyId).orElse(null);

        if (rater == null ||  ratedUser == null) {

            redirect.addFlashAttribute("error", "User not found");
            return "redirect:/trips";

        }

        if (currentUserId.equals(buddyId)) {

            redirect.addFlashAttribute("error", "You can't rate yourself");
            return "redirect:/profile/" + currentUserId;

        }

        if (rating.getScore() < 1) rating.setScore(1);
        if (rating.getScore() > 5) rating.setScore(5);

        rating.setTrip(trip);
        rating.setRater(rater);
        rating.setRatedUser(ratedUser);

        ratingRepo.save(rating);

        redirect.addFlashAttribute("message", "Rating submitted!");
        return "redirect:/profile/" + buddyId;

    }

    @GetMapping("/rate-buddy")
    public String rateBuddyFromProfile(@RequestParam("buddyId") Long buddyId, HttpSession session, RedirectAttributes redirect) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        if (currentUserId.equals(buddyId)) {

            redirect.addFlashAttribute("error", "You can't rate yourself");
            return "redirect:/profile/" + currentUserId;

        }

        User buddy = userRepo.findById(buddyId).orElse(null);
        if (buddy == null) {

            redirect.addFlashAttribute("error", "User not found");
            return "redirect:/profile/" + currentUserId;

        }

        List<TripParticipant> myParticipations = tripParticipantRepo.findByUserId(currentUserId);

        Trip sharedTrip = null;

        for (TripParticipant tp: myParticipations) {

            Trip t = tp.getTrip();

            if (t.getUserId() != null && t.getUserId().equals(buddyId)) {

                sharedTrip = t;
                break;

            }

            List<TripParticipant> others = tripParticipantRepo.findByTripId(t.getId());
            for (TripParticipant otherTp : others) {
                if (otherTp.getUser().getId().equals(buddyId)) {
                    sharedTrip = t;
                    break;
                }
            }

            if (sharedTrip != null) break;

        }

        if (sharedTrip == null) {
            redirect.addFlashAttribute("error", "You don't share a trip with this user yet.");
            return "redirect:/profile/" + buddyId;
        }

        return "redirect:/trips/" + sharedTrip.getId() + "/rate?buddyId=" + buddyId;

    }

}
