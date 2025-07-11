package org.example.controller;

import org.example.exception.OAuth2AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OAuth2AuthenticationException.class)
    public String handleOAuth2AuthenticationException() {
        return "redirect:/error?oauth_error";
    }
}