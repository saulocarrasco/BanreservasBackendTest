package com.banreservas.customer.application;

import com.banreservas.customer.domain.exception.InvalidCredentialsException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;

@ApplicationScoped
public class AuthService {

    @Inject
    @ConfigProperty(name = "app.auth.username")
    String configuredUsername;

    @Inject
    @ConfigProperty(name = "app.auth.password")
    String configuredPassword;

    public String generateToken(String username, String password) {
        boolean usernameOk = MessageDigest.isEqual(
            configuredUsername.getBytes(StandardCharsets.UTF_8),
            username.getBytes(StandardCharsets.UTF_8));
        boolean passwordOk = MessageDigest.isEqual(
            configuredPassword.getBytes(StandardCharsets.UTF_8),
            password.getBytes(StandardCharsets.UTF_8));
        if (!usernameOk || !passwordOk) {
            throw new InvalidCredentialsException();
        }
        return Jwt.issuer("https://banreservas.com")
            .subject(username)
            .groups("user")
            .expiresIn(Duration.ofHours(1))
            .sign();
    }
}
