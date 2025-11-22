/*package com.example.tripto.controller;

import com.example.tripto.model.Trip;
import com.example.tripto.model.TripInvitation;
import com.example.tripto.model.TripParticipant;
import com.example.tripto.model.User;
import com.example.tripto.repository.TripInvitationRepository;
import com.example.tripto.repository.TripParticipantRepository;
import com.example.tripto.repository.TripRepository;
import com.example.tripto.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TripInvitationController {

    private final TripInvitationRepository invitationRepo;
    private final TripRepository tripRepo;
    private final UserRepository userRepo;
    private final TripParticipantRepository tripParticipantRepo;

    public TripInvitationController(TripInvitationRepository invitationRepo, TripRepository tripRepo, UserRepository userRepo, TripParticipantRepository tripParticipantRepo) {

        this.invitationRepo = invitationRepo;
        this.tripRepo = tripRepo;
        this.userRepo = userRepo;
        this.tripParticipantRepo = tripParticipantRepo;

    }

    @PostMapping("/invites/send")
    public String sendInvite(@RequestParam Long buddyId, HttpSession session, RedirectAttributes redirect) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        List<Trip> myTrips = tripRepo.findByUserId(currentUserId);
        if (myTrips.isEmpty()) {

            redirect.addFlashAttribute("error", "Create a trip first before sending invites");
            return "redirect:/trips";

        }

        Trip target = null;
        for (Trip t : myTrips) {

            if (!t.isCompleted()) {

                target = t;

            }

        }

        if (target == null) {

            target = myTrips.get(myTrips.size() - 1);

        }

        User fromUser = userRepo.findById(currentUserId).orElse(null);
        User toUser = userRepo.findById(buddyId).orElse(null);

        if (fromUser == null || toUser == null) {

            redirect.addFlashAttribute("error", "User doesn't exist");
            return "redirect:/trips";

        }

        TripInvitation inv = TripInvitation.builder().trip(target).fromUser(fromUser).toUser(toUser).status(TripInvitation.Status.PENDING).build();

        invitationRepo.save(inv);
        redirect.addFlashAttribute("message", "Invite sent successfully");
        return "redirect:/matches/" + currentUserId;

    }

    @GetMapping("/invites")
    public String listinvites(HttpSession session, Model model) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        List<TripInvitation> pending = invitationRepo.findByToUserIdAndStatus(currentUserId, TripInvitation.Status.PENDING);

        model.addAttribute("invites", pending);
        return "invites";

    }

    @PostMapping("/invites/{id}/respond")
    public String respondInvite(@PathVariable Long id, @RequestParam String action, HttpSession session, RedirectAttributes redirect) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        TripInvitation inv = invitationRepo.findById(id).orElse(null);
        if (inv == null) {

            redirect.addFlashAttribute("error", "Invite could not be found");
            return "redirect:/invites";

        }

        if (!inv.getToUser().getId().equals(currentUserId)) {

            redirect.addFlashAttribute("error", "you cannot respond to this invite.");
            return "redirect:/invites";

        }

        if ("accept".equalsIgnoreCase(action)) {

            inv.setStatus(TripInvitation.Status.ACCEPTED);
            invitationRepo.save(inv);

            TripParticipant tp = new TripParticipant();
            tp.setTrip(inv.getTrip());
            tp.setUser(inv.getFromUser());
            tripParticipantRepo.save(tp);

            redirect.addFlashAttribute("message", "you joined the trip!");

        } else if ("decline".equalsIgnoreCase(action)) {

            inv.setStatus(TripInvitation.Status.DECLINED);
            invitationRepo.save(inv);
            redirect.addFlashAttribute("message", "you declined the trip!");

        }

        return "redirect:/invites";

    }

}*/
