package com.example.tripto.controller;

import com.example.tripto.model.Trip;
import com.example.tripto.model.Destination;
import com.example.tripto.repository.DestinationRepository;
import com.example.tripto.repository.TripRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TripController {

    private final TripRepository tripRepo;
    private final DestinationRepository destRepo;

    public TripController(TripRepository tripRepo, DestinationRepository destRepo) {
        this.tripRepo = tripRepo;
        this.destRepo = destRepo;
    }


    @GetMapping("/trips")
    public String showTrips(Model model, HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {
            return "redirect:/login";
        }

        Trip blank = new Trip();
        blank.setDestination(new Destination());

        model.addAttribute("trip", blank);
        model.addAttribute("trips", tripRepo.findByUserId(currentUserId));
        model.addAttribute("destinations", destRepo.findAll());
        model.addAttribute("userId", currentUserId);

        return "trips";
    }


    @PostMapping("/trips")
    public String createTrip(@ModelAttribute Trip trip, HttpSession session, Model model) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        if (trip.getStartDate() != null &&
                trip.getEndDate() != null &&
                trip.getStartDate().isAfter(trip.getEndDate())) {

            model.addAttribute("error", "End date must be on or after start date");
            model.addAttribute("trips", tripRepo.findByUserId(currentUserId));
            model.addAttribute("destinations", destRepo.findAll());
            model.addAttribute("userId", currentUserId);
            model.addAttribute("trip", trip);

            return "trips";

        }

        if (trip.getDestination() != null && trip.getDestination().getId() != null) {

            destRepo.findById(trip.getDestination().getId())
                    .ifPresent(trip::setDestination);

        } else {

            trip.setDestination(null);

        }

        trip.setUserId(currentUserId);

        tripRepo.save(trip);

        return "redirect:/matches/" + currentUserId;
    }

    @PostMapping("/trips/delete")
    public String deleteTrip(@RequestParam Long id, HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        Trip trip = tripRepo.findById(id).orElse(null);
        if (trip == null) {

            return "redirect:/trips/";

        }

        if (!currentUserId.equals(trip.getUserId())) {

            return "redirect:/trips/";

        }

        tripRepo.deleteById(id);
        return "redirect:/trips";
    }

    @PostMapping("/trips/end")
    public String endTrip(@RequestParam Long id, HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {
            return "redirect:/login";
        }

        Trip trip = tripRepo.findById(id).orElse(null);
        if (trip == null) {
            return "redirect:/trips";
        }

        if (!currentUserId.equals(trip.getUserId())) {}

        trip.setCompleted(true);
        tripRepo.save(trip);

        return "redirect:/trips";

    }

}
