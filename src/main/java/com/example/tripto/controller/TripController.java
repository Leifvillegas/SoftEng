package com.example.tripto.controller;

import com.example.tripto.model.Trip;
import com.example.tripto.model.Destination;
import com.example.tripto.repository.DestinationRepository;
import com.example.tripto.repository.TripRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TripController {

    private final TripRepository tripRepo;
    private final DestinationRepository destRepo;

    public TripController(TripRepository tripRepo, DestinationRepository destRepo) {
        this.tripRepo = tripRepo;
        this.destRepo = destRepo;
    }

    @GetMapping("/trips")
    public String showTrips(Model model) {
        var blank = new Trip();
        blank.setDestination(new Destination());

        model.addAttribute("trips", tripRepo.findAll());
        model.addAttribute("trip", new Trip());
        model.addAttribute("destinations", destRepo.findAll());
        return "trips";
    }

    @PostMapping("/trips")
    public String createTrip(@Valid Trip trip, BindingResult result, Model model) {
        if(trip.getStartDate() != null && trip.getEndDate() != null && trip.getStartDate().isAfter(trip.getEndDate())) {
            result.rejectValue("endDate", "date.invalid", "End date must be on or after start date");
        }

        if(result.hasErrors()) {
            model.addAttribute("trips", tripRepo.findAll());
            model.addAttribute("destinations", destRepo.findAll());
            return "trips";
        }

        if(trip.getDestination() != null && trip.getDestination().getId() != null) {
            destRepo.findById(trip.getDestination().getId()).ifPresent(trip::setDestination);
        }

        tripRepo.save(trip);
        return "redirect:/trips";

    }

    @PostMapping("/trips/delete")
    public String deleteTrip(Long id) {
        tripRepo.deleteById(id);
        return "redirect:/trips";
    }

}
