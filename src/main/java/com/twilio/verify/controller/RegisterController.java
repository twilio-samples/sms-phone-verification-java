package com.twilio.verify.controller;

import com.twilio.verify.model.User;
import com.twilio.verify.repository.UserRepository;
import com.twilio.verify.service.VerifyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
public class RegisterController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerifyService verifyService;
    private final SecurityContextRepository securityContextRepository;

    public RegisterController(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            VerifyService verifyService,
                            SecurityContextRepository securityContextRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verifyService = verifyService;
        this.securityContextRepository = securityContextRepository;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                              @RequestParam(defaultValue = "sms") String channel,
                              BindingResult result,
                              Model model,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        if (userRepository.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists");
        }

        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            result.rejectValue("phoneNumber", "error.user", "Phone number already registered");
        }

        if (result.hasErrors()) {
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        // Send verification code
        boolean sent = verifyService.sendVerificationCode(user.getPhoneNumber(), channel);

        if (!sent) {
            model.addAttribute("error", "Failed to send verification code");
            return "register";
        }

        // Auto-login the user
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return "redirect:/verify";
    }
}
