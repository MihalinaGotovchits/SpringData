package org.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AdminUserController {

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String userAccess() {
        return "User Content";
    }

    @GetMapping("/moderator")
    @PreAuthorize("hasAuthority('ROLE_MODERATOR')")
    public String moderatorAccess() {
        return "Moderator Board";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public String adminAccess() {
        return "Admin Board";
    }
}