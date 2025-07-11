package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.model.GitHubOAuth2User;
import org.example.model.Role;
import org.example.service.GitHubOAuth2UserService;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final GitHubOAuth2UserService gitHubOAuth2UserService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private static final String GITHUB_CLIENT_ID = "Ov23liuqerxCioIi74DY";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions
                                .deny()
                        )
                        .addHeaderWriter(new StaticHeadersWriter("X-Frame-Options", "SAMEORIGIN", "/h2-console/**"))
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error", "/webjars/**", "/css/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(gitHubOAuth2UserService)
                        )
                        .successHandler(authenticationSuccessHandler())
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(logoutHandler())
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            GitHubOAuth2User user = (GitHubOAuth2User) authentication.getPrincipal();

            if (user.getRole() == Role.ADMIN) {
                response.sendRedirect("/admin");
            } else {
                response.sendRedirect("/user");
            }

            LoggerFactory.getLogger(getClass())
                    .info("User {} logged in successfully with role {}", user.getEmail(), user.getRole());
        };
    }

    @Bean
    public LogoutHandler logoutHandler() {
        return (request, response, authentication) -> {
            if (authentication != null) {
                GitHubOAuth2User user = (GitHubOAuth2User) authentication.getPrincipal();

                LoggerFactory.getLogger(getClass())
                        .info("User {} logged out and token revoked", user.getEmail());
            }
        };
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/h2-console/**");
    }
}