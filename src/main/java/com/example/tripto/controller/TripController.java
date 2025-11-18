package com.example.tripto.controller;

import com.example.tripto.model.Trip;
import com.example.tripto.model.Destination;
import com.example.tripto.repository.DestinationRepository;
import com.example.tripto.repository.TripRepository;
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
    public String showTrips(
            @RequestParam(name = "userId", required = false) Long userId,
            Model model
    ) {
        Trip blank = new Trip();
        blank.setDestination(new Destination());

        model.addAttribute("trip", blank);
        model.addAttribute("trips", tripRepo.findAll());
        model.addAttribute("destinations", destRepo.findAll());
        model.addAttribute("userId", userId); // keep it in the model

        return "trips";
    }


    @PostMapping("/trips")
    public String createTrip(
            @ModelAttribute Trip trip,
            @RequestParam(name = "userId", required = false) Long userId,
            Model model
    ) {

        if (trip.getStartDate() != null &&
                trip.getEndDate() != null &&
                trip.getStartDate().isAfter(trip.getEndDate())) {

            model.addAttribute("error", "End date must be on or after start date");
            model.addAttribute("trips", tripRepo.findAll());
            model.addAttribute("destinations", destRepo.findAll());
            model.addAttribute("userId", userId);
            model.addAttribute("trip", trip);

            return "trips";
        }

        if (trip.getDestination() != null && trip.getDestination().getId() != null) {
            destRepo.findById(trip.getDestination().getId())
                    .ifPresent(trip::setDestination);
        } else {
            trip.setDestination(null);
        }

        tripRepo.save(trip);

        if (userId != null) {
            return "redirect:/matches/" + userId;
        }

        return "redirect:/trips";
    }

    @PostMapping("/trips/delete")
    public String deleteTrip(
            @RequestParam Long id,
            @RequestParam(name = "userId", required = false) Long userId
    ) {
        tripRepo.deleteById(id);

        if (userId != null) {
            return "redirect:/trips?userId=" + userId;
        }

        return "redirect:/trips";
    }
}
