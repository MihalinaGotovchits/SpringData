package org.example.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class GitHubOAuth2User extends User implements OAuth2User {
    private Map<String, Object> attributes;

    public GitHubOAuth2User(User user, Map<String, Object> attributes) {
        super(user);
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + getRole().name()));
    }

    @Override
    public String getName() {
        return getLogin();
    }
}