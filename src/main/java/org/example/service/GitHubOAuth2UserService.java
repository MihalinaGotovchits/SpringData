package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.OAuth2AuthenticationException;
import org.example.model.GitHubOAuth2User;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GitHubOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(GitHubOAuth2UserService.class);

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            Map<String, Object> attributes = oAuth2User.getAttributes();
            String githubId = attributes.get("id").toString();
            String login = attributes.get("login").toString();
            String name = attributes.containsKey("name") ? attributes.get("name").toString() : login;
            String avatarUrl = attributes.get("avatar_url").toString();
            String email = fetchUserEmail(userRequest.getAccessToken().getTokenValue());

            if (email == null) {
                throw new OAuth2AuthenticationException("Email is required");
            }

            User user = userRepository.findByGithubId(githubId)
                    .map(existingUser -> updateExistingUser(existingUser, login, name, avatarUrl, email))
                    .orElseGet(() -> createNewUser(githubId, login, name, email, avatarUrl));

            logger.info("User {} (role: {}) authenticated via GitHub", email, user.getRole());
            return new GitHubOAuth2User(user, attributes);
        } catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage());
            throw new OAuth2AuthenticationException("Authentication failed");
        }
    }

    private User updateExistingUser(User user, String login, String name, String avatarUrl, String email) {
        user.setLogin(login);
        user.setName(name);
        user.setAvatarUrl(avatarUrl);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private User createNewUser(String githubId, String login, String name, String email, String avatarUrl) {
        User newUser = new User(
                githubId,
                login,
                name,
                email,
                avatarUrl,
                determineUserRole(login, email)
        );
        return userRepository.save(newUser);
    }

    private String fetchUserEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        return response.getBody().stream()
                .filter(email -> (Boolean) email.get("primary"))
                .findFirst()
                .map(email -> (String) email.get("email"))
                .orElseThrow(() -> new OAuth2AuthenticationException("Email not found"));
    }

    private Role determineUserRole(String login, String email) {
        if ("admin-user".equals(login) || "admin@example.com".equals(email)) {
            return Role.ADMIN;
        }
        return Role.USER;
    }
}