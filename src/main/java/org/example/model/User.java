package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String githubId;
    private String login;
    private String name;
    private String email;
    private String avatarUrl;
    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String githubId, String login, String name, String email, String avatarUrl, Role role) {
        this.githubId = githubId;
        this.login = login;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.role = role;
    }

    public User(User user) {

    }
}