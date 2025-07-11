package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPT = 5;
    public static final long LOCK_TIME_DURATION = 60 * 60 * 1000; // 1 час

    @Autowired
    private UserRepository userRepository;

    public void loginFailed(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.isAccountNonLocked()) {
            if (user.getFailedAttempt() < MAX_ATTEMPT - 1) {
                user.setFailedAttempt(user.getFailedAttempt() + 1);
                userRepository.save(user);
            } else {
                user.setIsAccountNonLocked(false);
                user.setLockTime(new Date());
                userRepository.save(user);
            }
        }
    }

    public void loginSucceeded(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!user.isAccountNonLocked()) {
            if (user.getLockTime() != null &&
                    user.getLockTime().getTime() + LOCK_TIME_DURATION < System.currentTimeMillis()) {
                user.setIsAccountNonLocked(true);
                user.setLockTime(null);
                user.setFailedAttempt(0);
                userRepository.save(user);
            }
        } else {
            user.setFailedAttempt(0);
            userRepository.save(user);
        }
    }

    public boolean isBlocked(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!user.isAccountNonLocked()) {
            if (user.getLockTime() != null &&
                    user.getLockTime().getTime() + LOCK_TIME_DURATION > System.currentTimeMillis()) {
                return true;
            } else {
                user.setIsAccountNonLocked(true);
                user.setLockTime(null);
                user.setFailedAttempt(0);
                userRepository.save(user);
                return false;
            }
        }
        return false;
    }
}