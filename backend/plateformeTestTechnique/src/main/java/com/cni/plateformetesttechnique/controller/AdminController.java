//package com.cni.plateformetesttechnique.controller;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.cni.plateformetesttechnique.model.User;
//<<<<<<< Updated upstream
//import java.util.*;
//import com.cni.plateformetesttechnique.repository.UserRepository;
//import com.cni.plateformetesttechnique.response.MessageResponse;
//=======
//import com.cni.plateformetesttechnique.repository.UserRepository;
//import com.cni.plateformetesttechnique.response.MessageResponse;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//
//import java.util.List;
//import java.util.Optional;
//>>>>>>> Stashed changes
//
//@RestController
////@RequestMapping("/api/admin")
////public class AdminController {
////
////    @Autowired
////    AdminService adminService;
////
////    @PostMapping("/activate")
////    @PreAuthorize("hasRole('ADMIN')")
////    public ResponseEntity<?> activateUser(@RequestParam(name = "email") String email) {
////        return adminService.activateUser(email);
////    }
////}
//@RequestMapping("/api/admin")
//public class AdminController {
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @GetMapping("/inactive-users")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<User>> getInactiveUsers() {
//
//        List<User> inactiveUsers = userRepository.findByActiveFalse();
//        return ResponseEntity.ok(inactiveUsers);
//    }
//=======
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    AdminService adminService;
//    private JavaMailSender mailSender;
//
//    @GetMapping("/inactive-users")
////    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<User>> getInactiveUsers() {
//        List<User> inactiveUsers = userRepository.findByActiveFalse();
//        return ResponseEntity.ok(inactiveUsers);
//    }
//
//>>>>>>> Stashed changes
//
//    @PostMapping("/activate")
////    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> activateUser(@RequestParam(name = "email") String email) {
//        Optional<User> optionalUser = userRepository.findByEmail(email);
//
//        if (optionalUser.isEmpty()) {
//            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found with email: " + email));
//        }
//
//        User user = optionalUser.get();
//
//        if (user.getActive()) {
//            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is already active!"));
//        }
//
//        user.setActive(true);
//        userRepository.save(user);
//
//        sendConfirmationEmail(user);
//
//        return ResponseEntity.ok(new MessageResponse("User activated successfully!"));
//    }
//
//    @DeleteMapping("/delete/{id}")
//<<<<<<< Updated upstream
//    @PreAuthorize("hasRole('ADMIN')")
//=======
////    @PreAuthorize("hasRole('ADMIN')")
//>>>>>>> Stashed changes
//    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
//        Optional<User> optionalUser = userRepository.findById(id);
//        if (optionalUser.isEmpty()) {
//            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found"));
//        }
//        userRepository.deleteById(id);
//        return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
//    }
//
//    private void sendConfirmationEmail(User user) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(user.getEmail());
//        message.setSubject("Votre compte a été activé");
//        message.setText("Bonjour " + user.getUsername() + ",\n\n" +
//                "Votre compte a été activé avec succès ! Vous pouvez maintenant vous connecter.\n\n" +
//                "Cordialement,\nL'équipe.");
//
//        try {
//            mailSender.send(message);
//            System.out.println("✅ Email d'activation envoyé à " + user.getEmail());
//        } catch (Exception e) {
//            System.err.println("❌ Erreur lors de l'envoi de l'email d'activation : " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//<<<<<<< Updated upstream
//
//=======
//
//>>>>>>> Stashed changes
//    @GetMapping("/active-users")
//    public ResponseEntity<List<User>> getActiveUsers() {
//        List<User> activeUsers = userRepository.findByActiveTrue();
//        return ResponseEntity.ok(activeUsers);
//    }
//
//    @PostMapping("/deactivate")
//    public ResponseEntity<?> deactivateUser(@RequestParam(name = "email") String email) {
//        Optional<User> optionalUser = userRepository.findByEmail(email);
//
//        if (optionalUser.isEmpty()) {
//            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found with email: " + email));
//        }
//
//        User user = optionalUser.get();
//
//        if (!user.getActive()) {
//            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is already inactive!"));
//        }
//
//        user.setActive(false);
//        userRepository.save(user);
//
//        return ResponseEntity.ok(new MessageResponse("User deactivated successfully!"));
//    }
//
//<<<<<<< Updated upstream
//
//
//
//
//}
//=======
//
//
//
//
//}
//>>>>>>> Stashed changes
package com.cni.plateformetesttechnique.controller;


import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.cni.plateformetesttechnique.repository.*;
import com.cni.plateformetesttechnique.dto.UpdateAdminRequest;
import com.cni.plateformetesttechnique.model.Administrateur;
import com.cni.plateformetesttechnique.model.AdministrateurDTO;
import com.cni.plateformetesttechnique.model.ImageUtil;
import com.cni.plateformetesttechnique.model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import com.cni.plateformetesttechnique.repository.UserRepository;
import com.cni.plateformetesttechnique.response.MessageResponse;
import com.cni.plateformetesttechnique.security.services.UserDetailsImpl;

import java.nio.file.Path;



@RestController
@RequestMapping("/api/admin")
public class AdminController {


    @Autowired
    UserRepository userRepository;
    
    @Autowired
    
   private  AdministrateurRepository  administrateurRepository ;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

  /*  @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAdminWithImage(
            Authentication authentication,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("grade") String grade,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long id = userDetails.getId();

        Optional<Administrateur> optionalAdmin = administrateurRepository.findById(id);
        if (optionalAdmin.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Administrateur administrateur = optionalAdmin.get();
        administrateur.setUsername(username);
        administrateur.setEmail(email);

        // Ici, il faut encoder le mot de passe avant de le sauvegarder
        administrateur.setPassword(passwordEncoder.encode(password));

        administrateur.setGrade(grade);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                administrateur.setImage(imageFile.getBytes()); // Ici on met directement les bytes dans l'attribut
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur lors de la lecture de l'image : " + e.getMessage());
            }
        }

        administrateurRepository.save(administrateur);
        return ResponseEntity.ok(new MessageResponse("Administrateur mis à jour avec succès."));
    }
*/

  /*  @GetMapping("get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminById(@PathVariable(name="id") Long id) {
        Optional<Administrateur> optionalAdmin = administrateurRepository.findById(id);
        if (optionalAdmin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Administrateur non trouvé"));
        }
        return ResponseEntity.ok(optionalAdmin.get());
    }*/
    @GetMapping("get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminById(@PathVariable(name = "id") Long id) {
        Optional<Administrateur> optionalAdmin = administrateurRepository.findById(id);
        if (optionalAdmin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Administrateur non trouvé"));
        }

        Administrateur admin = optionalAdmin.get();
        AdministrateurDTO dto = new AdministrateurDTO();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setEmail(admin.getEmail());
        dto.setGrade(admin.getGrade());

        if (admin.getImage() != null) {
            String base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(admin.getImage());
            dto.setImage(base64Image);
        }

        return ResponseEntity.ok(dto);
    }

   /* @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAdminWithImage(
            Authentication authentication,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("grade") String grade,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long id = userDetails.getId();

        Optional<Administrateur> optionalAdmin = administrateurRepository.findById(id);
        if (optionalAdmin.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Administrateur administrateur = optionalAdmin.get();
        
        // Vérification de l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, administrateur.getPassword())) {
            return ResponseEntity.badRequest().body("L'ancien mot de passe est incorrect.");
        }

        // Si le nouveau mot de passe est fourni, on le met à jour après l'avoir encodé
        if (newPassword != null && !newPassword.isEmpty()) {
            administrateur.setPassword(passwordEncoder.encode(newPassword));  // Encodage du nouveau mot de passe
        }

        administrateur.setUsername(username);
        administrateur.setEmail(email);
        administrateur.setGrade(grade);

        // Traitement de la photo (si elle est fournie)
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                administrateur.setImage(imageFile.getBytes()); // Ici on met directement les bytes dans l'attribut
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur lors de la lecture de l'image : " + e.getMessage());
            }
        }

        administrateurRepository.save(administrateur);
        return ResponseEntity.ok(new MessageResponse("Administrateur mis à jour avec succès."));
    }
*/

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAdminWithImage(
            @PathVariable(name = "id") Long id,
            @ModelAttribute UpdateAdminRequest request,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long authenticatedId = userDetails.getId();

        if (!authenticatedId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous ne pouvez pas modifier un autre administrateur.");
        }

        Optional<Administrateur> optionalAdmin = administrateurRepository.findById(id);
        if (optionalAdmin.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Administrateur administrateur = optionalAdmin.get();

        // Sécuriser l'ancien mot de passe seulement si on veut modifier le mot de passe
        if (request.getOldPassword() != null && !request.getOldPassword().isBlank()) {
            if (!passwordEncoder.matches(request.getOldPassword(), administrateur.getPassword())) {
                return ResponseEntity.badRequest().body("L'ancien mot de passe est incorrect.");
            }
            if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
                administrateur.setPassword(passwordEncoder.encode(request.getNewPassword()));
            }
        }

        // Mise à jour du username
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            administrateur.setUsername(request.getUsername());
        }
        // Sinon NE PAS toucher au username !

        // Mise à jour de l'email
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            administrateur.setEmail(request.getEmail());
        }
        // Sinon NE PAS toucher au email !

        // Mise à jour du grade
        if (request.getGrade() != null && !request.getGrade().isBlank()) {
            administrateur.setGrade(request.getGrade());
        }

        // Mise à jour de l'image
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                administrateur.setImage(request.getImage().getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur lors de la lecture de l'image : " + e.getMessage());
            }
        }

        administrateurRepository.save(administrateur);

        return ResponseEntity.ok(new MessageResponse("Administrateur mis à jour avec succès."));
    }

    @PutMapping("/update-password/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAdminPassword(
            Authentication authentication,
            @PathVariable(name = "id") Long id,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword
    ) {
        // Récupération des informations de l'utilisateur authentifié
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long authenticatedUserId = userDetails.getId();

        // Vérification si l'utilisateur est celui qui est censé être mis à jour
        if (!authenticatedUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous ne pouvez pas modifier le mot de passe d'un autre administrateur.");
        }

        Optional<Administrateur> optionalAdmin = administrateurRepository.findById(id);
        if (optionalAdmin.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        

        Administrateur administrateur = optionalAdmin.get();
        
        // Vérification de l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, administrateur.getPassword())) {
            return ResponseEntity.badRequest().body("L'ancien mot de passe est incorrect.");
        }

        // Mise à jour du mot de passe avec un nouveau mot de passe encodé
        if (newPassword != null && !newPassword.isEmpty()) {
            administrateur.setPassword(passwordEncoder.encode(newPassword));  // Encodage du nouveau mot de passe
        }

        administrateurRepository.save(administrateur);
        return ResponseEntity.ok(new MessageResponse("Mot de passe mis à jour avec succès."));
    }
    
    @PutMapping("/update-photo/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAdminPhoto(
            @PathVariable(name = "id") Long id,
            @RequestParam("image") MultipartFile image,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long authenticatedId = userDetails.getId();

        if (!authenticatedId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous ne pouvez pas modifier un autre administrateur.");
        }

        Optional<Administrateur> optionalAdmin = administrateurRepository.findById(id);
        if (optionalAdmin.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Administrateur administrateur = optionalAdmin.get();

        if (image != null && !image.isEmpty()) {
            try {
                administrateur.setImage(image.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur lors de la lecture de l'image : " + e.getMessage());
            }
        }

        administrateurRepository.save(administrateur);

        return ResponseEntity.ok(new MessageResponse("Photo de profil mise à jour avec succès."));
    }


}