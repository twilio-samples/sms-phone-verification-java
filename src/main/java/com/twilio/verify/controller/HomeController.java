package com.twilio.verify.controller;

import com.twilio.verify.model.User;
import com.twilio.verify.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();

        if (!user.isVerified()) {
            return "redirect:/verify";
        }

        model.addAttribute("user", user);
        return "dashboard";
    }
}
