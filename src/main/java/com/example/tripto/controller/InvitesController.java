package com.example.tripto.controller;

import com.example.tripto.model.TripParticipant;
import com.example.tripto.repository.TripParticipantRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class InvitesController {

    private final TripParticipantRepository participantRepo;

    public InvitesController(TripParticipantRepository participantRepo) {

        this.participantRepo = participantRepo;

    }

    @GetMapping("/invites")
    public String showInvites(HttpSession session, Model model) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {
            return "redirect:/login";
        }

        List<TripParticipant> invites = participantRepo.findByUserIdAndAcceptedFalse(currentUserId);

        model.addAttribute("invites", invites);

        return "invites";
    }

    @PostMapping("/invites/{id}/accept")
    public String acceptInvite(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        TripParticipant tp = participantRepo.findById(id).orElse(null);
        if (tp == null || !tp.getUser().getId().equals(currentUserId)) {

            return "redirect:/invites";

        }

        tp.setAccepted(true);
        participantRepo.save(tp);

        redirect.addFlashAttribute("message", "Invite accepted");
        return "redirect:/trips";

    }

    @PostMapping("/invites/{id}/decline")
    public String declineInvite(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {

        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {

            return "redirect:/login";

        }

        TripParticipant tp = participantRepo.findById(id).orElse(null);
        if (tp == null || !tp.getUser().getId().equals(currentUserId)) {

            return "redirect:/invites";

        }

        participantRepo.delete(tp);

        redirect.addFlashAttribute("message", "Invite declined");
        return "redirect:/invites";

    }

}
