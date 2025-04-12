package com.cni.plateformetesttechnique.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cni.plateformetesttechnique.dto.PasswordResetDto;
import com.cni.plateformetesttechnique.dto.PasswordResetRequest;
import com.cni.plateformetesttechnique.model.PasswordResetToken;
import com.cni.plateformetesttechnique.model.User;
import com.cni.plateformetesttechnique.repository.PasswordResetTokenRepository;
import com.cni.plateformetesttechnique.service.EmailService;
import com.cni.plateformetesttechnique.service.UserService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Cache<String, Boolean> resendCache = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetRequest request) {
        try {
            User user = userService.findUserByEmail(request.getEmail());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Vous n'avez pas de compte avec cet email"));
            }

            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            emailService.sendPasswordResetEmail(user.getEmail(), token);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Email envoyé (vérifiez votre boîte mail)");
            response.put("debug_token", token); // Pour les tests seulement
            response.put("reset_link", "http://localhost:4200/reset-password?token=" + token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", "Erreur lors du traitement"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        try {
            PasswordResetToken token = passwordResetTokenRepository.findByToken(passwordResetDto.getToken());
            if (token == null) {
                return ResponseEntity.badRequest().body("Invalid password reset token");
            }

            if (token.isExpired()) {
                passwordResetTokenRepository.delete(token);
                return ResponseEntity.badRequest().body("Password reset token has expired");
            }

            if (!passwordResetDto.getNewPassword().equals(passwordResetDto.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("New password and confirmation password do not match");
            }

            if (!isPasswordValid(passwordResetDto.getNewPassword())) {
                return ResponseEntity.badRequest().body("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter and one number");
            }

            User user = token.getUser();
            if (passwordEncoder.matches(passwordResetDto.getNewPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body("New password must be different from current password");
            }

            userService.changeUserPassword(user, passwordResetDto.getNewPassword());
            passwordResetTokenRepository.delete(token);

            return ResponseEntity.ok("Password has been reset successfully");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while resetting your password");
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null
                && password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*\\d.*");
    }

    @PostMapping("/resend-email")
    public ResponseEntity<?> resendEmail(@RequestBody PasswordResetRequest request) {
        try {
            String email = request.getEmail().toLowerCase().trim();

            if (resendCache.getIfPresent(email) != null) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(Collections.singletonMap("error", "Veuillez patienter 2 minutes avant de renvoyer"));
            }

            User user = userService.findUserByEmail(email);
            if (user == null) {
                return ResponseEntity.ok()
                        .body(Collections.singletonMap("message", "Si cet email existe, un lien a été envoyé"));
            }

            PasswordResetToken existingToken = passwordResetTokenRepository.findByUser(user);
            if (existingToken != null) {
                passwordResetTokenRepository.delete(existingToken);
            }

            String newToken = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, newToken);
            emailService.sendPasswordResetEmail(user.getEmail(), newToken);

            resendCache.put(email, true);

            return ResponseEntity.ok()
                    .body(Collections.singletonMap("message", "Email de réinitialisation renvoyé"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", "Erreur lors du renvoi de l'email"));
        }
    }
}
