package com.example.variant4.service;

import com.example.variant4.config.JwtProperties;
import com.example.variant4.model.AuthRequest;
import com.example.variant4.model.AuthResponse;
import com.example.variant4.model.RefreshTokenRequest;
import com.example.variant4.model.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserService userService;
    JwtService jwtService;
    AuthenticationManager authenticationManager;
    TokenService tokenService;
    JwtProperties properties;
    EmailService emailService;
    PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userService.loadUserByUsername(request.getEmail());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        tokenService.saveRefreshToken(refreshToken, user.getUsername());

        emailService.send(
                user.getUsername(),
                "Login Successful",
                "You have successfully logged in. If this wasn't you, please secure your account."
        );

        return new AuthResponse(accessToken, refreshToken, properties.getAccessExpiration() / 1000);
    }

    public AuthResponse register(AuthRequest request) {

        if(userService.existByEmail(request.getEmail()))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        }

        var user = userService.save(
                User.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .build()
        );


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );


        String refreshToken = jwtService.generateRefreshToken(user);
        String accessToken = jwtService.generateAccessToken(user);

        tokenService.saveRefreshToken(refreshToken, user.getUsername());

        emailService.send(
                user.getUsername(),
                "Login Successful",
                "You have successfully logged in. If this wasn't you, please secure your account."
        );

        return new AuthResponse(accessToken, refreshToken, properties.getAccessExpiration() / 1000);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        var token = request.getRefreshToken();
        if (!tokenService.isRefreshTokenValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }


        String email = tokenService.getEmailFromRefreshToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email not found"));

        var user = userService.loadUserByUsername(email);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        tokenService.deleteRefreshToken(token);
        tokenService.saveRefreshToken(newRefreshToken, email);

        emailService.send(
                email,
                "Token Refreshed",
                "Your authentication token was refreshed. If this wasn't you, please review your account activity."
        );

        return new AuthResponse(newAccessToken, newRefreshToken, properties.getAccessExpiration() / 1000);
    }
}

