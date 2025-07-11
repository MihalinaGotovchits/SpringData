package org.example.service;

import org.example.dto.ReqRes;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private LoginAttemptService loginAttemptService;

    public ReqRes signUp(ReqRes registrationRequest) {
        ReqRes res = new ReqRes();
        try {
            User user = new User();
            user.setUsername(registrationRequest.getName());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setRole(Role.valueOf(registrationRequest.getRole()));
            User userResult = userRepository.save(user);
            if (userResult != null && userResult.getId() > 0) {
                res.setUser(userResult);
                res.setMessage("Пользователь сохранен успешно");
                res.setStatusCode(200);
            }
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setError(e.getMessage());
        }
        return res;
    }

    public ReqRes signIn(ReqRes signInRequest) {
        ReqRes response = new ReqRes();

        if (loginAttemptService.isBlocked(signInRequest.getName())) {
            throw new RuntimeException("Акаунт заблокировн");
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getName(), signInRequest.getPassword()));
            var user = userRepository.findByUsername(signInRequest.getName()).orElseThrow();
            System.out.println("Пользователь: " + user);
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");
            response.setMessage("Вход успешно выполнен");
            loginAttemptService.loginSucceeded(user.getUsername());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
            loginAttemptService.loginFailed(signInRequest.getName());
        }
        return response;
    }

    public ReqRes refreshToken(ReqRes refreshTokenRequest) {
        ReqRes response = new ReqRes();
        try {
            String username = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            User user = userRepository.findByUsername(username).orElseThrow();

            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), user)) {
                var jwt = jwtUtils.generateToken(user);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Токен успешно обновлен");
            } else {
                response.setStatusCode(500);
                response.setMessage("Неверный токен обновления");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }
}
