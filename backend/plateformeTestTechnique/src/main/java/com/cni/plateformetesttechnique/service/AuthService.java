package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.model.*;
import com.cni.plateformetesttechnique.repository.RoleRepository;
import com.cni.plateformetesttechnique.repository.UserRepository;
import com.cni.plateformetesttechnique.dto.LoginRequest;
import com.cni.plateformetesttechnique.dto.SignupRequest;
import com.cni.plateformetesttechnique.response.JwtResponse;
import com.cni.plateformetesttechnique.response.MessageResponse;
import com.cni.plateformetesttechnique.security.jwt.JwtUtils;
import com.cni.plateformetesttechnique.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JavaMailSender mailSender;

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur introuvable.");
        }

        User user = userOptional.get();
        if (!user.getActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Votre compte n'est pas encore activé.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        Role roleDeveloppeur = roleRepository.findByName(ERole.ROLE_DEVELOPPEUR)
                .orElseThrow(() -> new RuntimeException("Error: Role DEVELOPPEUR not found."));

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role not found."));
            roles.add(userRole);
        } else {
            for (String role : strRoles) {
                switch (role.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                        roles.add(adminRole);
                        break;
                    case "developpeur":
                        roles.add(roleDeveloppeur);
                        break;
                    case "chefprojet":
                        Role chefProjetRole = roleRepository.findByName(ERole.ROLE_CHEF_PROJET)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                        roles.add(chefProjetRole);
                        break;
                    default:
                        return ResponseEntity.badRequest().body(new MessageResponse("Error: Role " + role + " not recognized."));
                }
            }
        }

        User user;
        if (strRoles.contains("developpeur")) {
            Developpeur developpeur = new Developpeur();
            developpeur.setUsername(signUpRequest.getUsername());
            developpeur.setEmail(signUpRequest.getEmail());
            developpeur.setPassword(encoder.encode(signUpRequest.getPassword()));
            developpeur.setSpecialite(signUpRequest.getSpecialite());
            developpeur.setScore(signUpRequest.getScore());
            developpeur.setExperience(signUpRequest.getExperience());
            developpeur.setTechnologies(signUpRequest.getTechnologies());
            developpeur.setRoles(roles);
            developpeur.setActive(false);
            user = developpeur;
            sendActivationEmail(developpeur);
        } else if (strRoles.contains("admin")) {
            Administrateur administrateur = new Administrateur();
            administrateur.setUsername(signUpRequest.getUsername());
            administrateur.setEmail(signUpRequest.getEmail());
            administrateur.setPassword(encoder.encode(signUpRequest.getPassword()));
            administrateur.setGrade(signUpRequest.getGrade());
            administrateur.setRoles(roles);
            administrateur.setActive(true);
            user = administrateur;
        } else {
            user = new User(signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    encoder.encode(signUpRequest.getPassword()));
            user.setRoles(roles);
            user.setActive(true);
        }

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully."));
    }

    private void sendActivationEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Activation de votre compte");
        message.setText("Bonjour " + user.getUsername() + ",\n\n" +
                "Merci de vous être inscrit. Un administrateur doit activer votre compte.\n\n" +
                "Cordialement,\nL'équipe.");

        mailSender.send(message);
    }
}
