package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.GitHubOAuth2User;
import org.example.model.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal GitHubOAuth2User user, Model model) {
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "index";
    }

    @GetMapping("/user")
    public String userProfile(@AuthenticationPrincipal GitHubOAuth2User user, Model model) {
        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);
        model.addAttribute("name", user.getName());
        model.addAttribute("login", user.getLogin());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("avatarUrl", user.getAvatarUrl());

        return "user";
    }

    @GetMapping("/admin")
    public String adminPanel(@AuthenticationPrincipal GitHubOAuth2User user, Model model) {
        if (user == null) {
            return "redirect:/";
        }

        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Access denied");
        }

        model.addAttribute("user", user);
        return "admin";
    }

    @GetMapping("/error")
    public String error(@RequestParam(required = false) String error, Model model) {
        if ("access_denied".equals(error)) {
            model.addAttribute("error", "Access denied");
        } else if ("oauth_error".equals(error)) {
            model.addAttribute("error", "Authentication error");
        }
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException() {
        return "redirect:/error?access_denied";
    }
}