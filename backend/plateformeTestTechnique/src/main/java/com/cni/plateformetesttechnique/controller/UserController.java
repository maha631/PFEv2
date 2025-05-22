package com.cni.plateformetesttechnique.controller;

import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cni.plateformetesttechnique.model.User;
import com.cni.plateformetesttechnique.repository.UserRepository;
import com.cni.plateformetesttechnique.security.jwt.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

   
    @GetMapping("/users/photo")
    @PreAuthorize("hasRole('ADMIN')or hasRole('ROLE_DEVELOPPEUR') or hasRole('ROLE_CHEF')")
    public ResponseEntity<byte[]> getUserPhoto() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            byte[] imageBytes = user.getImage();

            if (imageBytes != null && imageBytes.length > 0) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG); // ou IMAGE_PNG si nécessaire
                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    @GetMapping("/users/info")
    @PreAuthorize("hasRole('ADMIN')or hasRole('ROLE_DEVELOPPEUR') or hasRole('ROLE_CHEF')")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);

        return userOptional.map(ResponseEntity::ok)
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')or hasRole('ROLE_DEVELOPPEUR') or hasRole('ROLE_CHEF')")
    public ResponseEntity<?> getUserById(@PathVariable(name="id") Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }
    }

}
