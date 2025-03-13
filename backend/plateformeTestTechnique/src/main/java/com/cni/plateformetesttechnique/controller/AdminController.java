package com.cni.plateformetesttechnique.controller;

import com.cni.plateformetesttechnique.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
public class AdminController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/inactive-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getInactiveUsers() {
        List<User> inactiveUsers = userRepository.findByActiveFalse();
        return ResponseEntity.ok(inactiveUsers);
    }

    @PostMapping("/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateUser(@RequestParam(name = "email") String email) {
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

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found"));
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
    }

    private void sendConfirmationEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Votre compte a été activé");
        message.setText("Bonjour " + user.getUsername() + ",\n\n" +
                "Votre compte a été activé avec succès ! Vous pouvez maintenant vous connecter.\n\n" +
                "Cordialement,\nL'équipe.");

        try {
            mailSender.send(message);
            System.out.println("✅ Email d'activation envoyé à " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email d'activation : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @GetMapping("/active-users")
    public ResponseEntity<List<User>> getActiveUsers() {
        List<User> activeUsers = userRepository.findByActiveTrue();
        return ResponseEntity.ok(activeUsers);
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateUser(@RequestParam(name = "email") String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found with email: " + email));
        }

        User user = optionalUser.get();

        if (!user.getActive()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is already inactive!"));
        }

        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User deactivated successfully!"));
    }

    
    
    
    
}
