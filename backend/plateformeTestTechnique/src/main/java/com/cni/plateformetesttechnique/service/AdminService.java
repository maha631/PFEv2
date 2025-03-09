package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.model.User;
import com.cni.plateformetesttechnique.repository.UserRepository;
import com.cni.plateformetesttechnique.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public ResponseEntity<?> activateUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found with email: " + email));
        }

        User user = optionalUser.get();
        if (user.getActive()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is already active!"));
        }

        user.setActive(true);
        userRepository.save(user);
        sendConfirmationEmail(user);

        return ResponseEntity.ok(new MessageResponse("User activated successfully!"));
    }

    private void sendConfirmationEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Votre compte a été activé");
        message.setText("Votre compte a été activé avec succès !");
        mailSender.send(message);
    }
}
