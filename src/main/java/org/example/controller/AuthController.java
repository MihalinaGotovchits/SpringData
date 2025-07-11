package org.example.controller;

import org.example.dto.ReqRes;
import org.example.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    public ReqRes signIn(@RequestBody ReqRes signInRequest) {
        return authService.signIn(signInRequest);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public ReqRes signUp(@RequestBody ReqRes signUpRequest) {
        return authService.signUp(signUpRequest);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public ReqRes refreshToken(@RequestBody ReqRes refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }
}