package com.example.tripto.controller;

import com.example.tripto.model.Trip;
import com.example.tripto.model.Destination;
import com.example.tripto.model.TripParticipant;
import com.example.tripto.model.User;
import com.example.tripto.repository.DestinationRepository;
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
public class TripController {

    private final TripRepository tripRepo;
    private final DestinationRepository destRepo;
    private final TripParticipantRepository participantRepo;
    private final UserRepository userRepo;

    public TripController(TripRepository tripRepo, DestinationRepository destRepo, TripParticipantRepository participantRepo, UserRepository userRepo) {

        this.tripRepo = tripRepo;
        this.destRepo = destRepo;
        this.participantRepo = participantRepo;
        this.userRepo = userRepo;

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

        if (!currentUserId.equals(trip.getUserId())) {

            return "redirect:/trips";

        }

        trip.setCompleted(true);
        tripRepo.save(trip);

        List<TripParticipant> participants = participantRepo.findByTripId(id);

        TripParticipant firstAccepted = null;
        for (TripParticipant tp : participants) {

            if (tp.isAccepted()) {

                firstAccepted = tp;
                break;

            }

        }

        if  (firstAccepted != null) {

            Long buddyId = firstAccepted.getUser().getId();
            return "redirect:/trips/" + id + "/rate?buddyId=" + buddyId;

        }

        return "redirect:/trips";

    }

    @PostMapping("/trips/add-buddy")
    public String addBuddyFromMatches(@RequestParam Long buddyId, HttpSession session, RedirectAttributes redirect) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        List<Trip> myTrips = tripRepo.findByUserId(currentUserId);
        if (myTrips.isEmpty()) {

            redirect.addFlashAttribute("error", "Create a trip first before inviting a buddy");
            return "redirect:/trips";

        }

        Trip target = null;
        for (Trip t :  myTrips) {

            if (!t.isCompleted()) {

                target = t;
                break;

            }

        }

        if (target == null) {

            target = myTrips.get(myTrips.size() - 1);

        }

        User buddy = userRepo.findById(buddyId).orElse(null);
        if (buddy == null) {

            redirect.addFlashAttribute("error", "buddy not found");
            return "redirect:/trips";

        }

        TripParticipant tp = new TripParticipant();
        tp.setTrip(target);
        tp.setUser(buddy);
        tp.setAccepted(false);

        redirect.addFlashAttribute("message", "invite sent to your buddy");
        return "redirect:/trips/";

    }

}
