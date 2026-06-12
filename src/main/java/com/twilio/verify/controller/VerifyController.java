package com.twilio.verify.controller;

import com.twilio.verify.model.User;
import com.twilio.verify.repository.UserRepository;
import com.twilio.verify.service.VerifyService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class VerifyController {

    private final UserRepository userRepository;
    private final VerifyService verifyService;

    public VerifyController(UserRepository userRepository, VerifyService verifyService) {
        this.userRepository = userRepository;
        this.verifyService = verifyService;
    }

    @GetMapping("/verify")
    public String showVerifyPage(Authentication auth, Model model) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();

        if (user.isVerified()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", user);
        return "verify";
    }

    @PostMapping("/verify")
    public String verifyCode(@RequestParam String code,
                           Authentication auth,
                           Model model) {

        User user = userRepository.findByUsername(auth.getName()).orElseThrow();

        boolean verified = verifyService.checkVerificationCode(user.getPhoneNumber(), code);

        if (verified) {
            user.setVerified(true);
            userRepository.save(user);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid verification code");
            model.addAttribute("user", user);
            return "verify";
        }
    }

    @PostMapping("/api/verify/resend")
    @ResponseBody
    public Map<String, Object> resendCode(@RequestParam String channel, Authentication auth) {
        Map<String, Object> response = new HashMap<>();

        User user = userRepository.findByUsername(auth.getName()).orElseThrow();

        boolean sent = verifyService.sendVerificationCode(user.getPhoneNumber(), channel);

        response.put("success", sent);
        if (sent) {
            response.put("message", "New code sent to your phone number");
        } else {
            response.put("message", "Failed to send code");
        }

        return response;
    }
}
