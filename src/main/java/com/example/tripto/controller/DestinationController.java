package com.example.tripto.controller;

import com.example.tripto.model.Destination;
import com.example.tripto.repository.DestinationRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DestinationController {

    private final DestinationRepository destinationRepo;

    public DestinationController(DestinationRepository destinationRepo) {
        this.destinationRepo = destinationRepo;
    }

    @GetMapping("/destinations")
    public String showDestinations(Model model) {
        model.addAttribute("destinations", destinationRepo.findAll());
        model.addAttribute("destination", new Destination());
        return "destinations";
    }

    @PostMapping("/destinations")
    public String createDestinations(@Valid Destination destination, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("destinations", destinationRepo.findAll());
            return "destinations";
        }

        destinationRepo.save(destination);
        return "redirect:/destinations";

    }
}