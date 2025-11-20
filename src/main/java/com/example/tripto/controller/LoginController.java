package com.example.tripto.controller;

import com.example.tripto.model.User;
import com.example.tripto.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    private final UserRepository userRepo;

    public LoginController(UserRepository userRepo) {

        this.userRepo = userRepo;

    }

    @GetMapping("/login")
    public String loginForm() {

        return "login";

    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {

        Optional<User> opt =  userRepo.findByEmail(email);

        if (opt.isEmpty()) {

            model.addAttribute("error", "Invalid email or password");
            return "login";

        }

        User user = opt.get();

        if (!user.getPasswordHash().equals(password)) {

            model.addAttribute("error", "Invalid email or password");
            return "login";

        }

        session.setAttribute("currentUserId",  user.getId());

        return "redirect:/profile/" + user.getId();

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();
        return "redirect:/login";

    }

}
