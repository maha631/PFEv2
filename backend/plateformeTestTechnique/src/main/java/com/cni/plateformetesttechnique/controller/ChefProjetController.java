package com.cni.plateformetesttechnique.controller;



import com.cni.plateformetesttechnique.dto.ChefDTO;
import com.cni.plateformetesttechnique.dto.UpdateChefRequest;
import com.cni.plateformetesttechnique.model.ChefDeProjet;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.service.ChefDeProjetService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.cni.plateformetesttechnique.repository.ChefDeProjetRepository;
import com.cni.plateformetesttechnique.response.MessageResponse;
import com.cni.plateformetesttechnique.security.services.UserDetailsImpl;
import com.cni.plateformetesttechnique.service.ScoreService;

import java.io.IOException;


import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chefdeprojet")
@CrossOrigin(origins = "*")
public class ChefProjetController {

    @Autowired
    private ChefDeProjetService chefDeProjetService;
    @Autowired
    private ScoreService ScoreService ;
    @Autowired
    private ChefDeProjetRepository chefDeProjetRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;


   
    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ChefDeProjet ajouterChef(@RequestBody ChefDeProjet chef) {
        return chefDeProjetService.ajouterChefDeProjet(chef);
    }

   
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_CHEF')")
    public ChefDeProjet modifierChef(@PathVariable(name = "id") Long id, @RequestBody ChefDeProjet nouveauChef) {
        return chefDeProjetService.modifierChefDeProjet(id, nouveauChef);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void supprimerChef(@PathVariable(name = "id")  Long id) {
        chefDeProjetService.supprimerChefDeProjet(id);
    }

  
    @GetMapping("/{id}")
    public ChefDeProjet getChefById(@PathVariable(name = "id") Long id) {
        return chefDeProjetService.getChefDeProjetById(id);
    }

  
    @GetMapping
    public List<ChefDeProjet> getAllChefs() {
        return chefDeProjetService.getAllChefsDeProjet();
    }
    /*@PostMapping("/{chefId}/assign/{devId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ChefDeProjet assignerDeveloppeur(@PathVariable(name = "chefId") Long chefId, @PathVariable(name = "devId") Long devId) {
        return chefDeProjetService.assignerDeveloppeur(chefId, devId);
    }*/
    /*@PostMapping("/{chefId}/assign/{devId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> assignerDeveloppeur(
            @PathVariable("chefId") Long chefId,
            @PathVariable("devId") Long devId) {

        // Appel au service qui fait l'assignation + recalcul
        ChefDeProjet chef = chefDeProjetService.assignerDeveloppeur(chefId, devId);

        // Préparer la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("chef", chef);
        response.put("scoreRecalcule", chef.getScore());

        return ResponseEntity.ok(response);
    }*/
    
    @PostMapping("/{chefId}/assign/{devId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> assignerDeveloppeur(
            @PathVariable(name="chefId") Long chefId,
            @PathVariable(name="devId") Long devId) {

        // Appel au service qui fait l'assignation + recalcul
        ChefDeProjet chef = chefDeProjetService.assignerDeveloppeur(chefId, devId);
        double avgScore = ScoreService.updateChefScore(chefId);

        // Récupération des développeurs après assignation
        List<Developpeur> devs = Optional.ofNullable(chef.getDeveloppeurs())
                                         .orElse(Collections.emptyList());

        // Calcul du score moyen sécurisé
        avgScore = devs.stream()
            .map(Developpeur::getScore)
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);

        // Compter les développeurs par spécialité sécurisé
        Map<String, Long> specialties = devs.stream()
            .map(Developpeur::getSpecialite)
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(
                specialite -> specialite,
                Collectors.counting()
            ));

        // Préparer la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("chef", chef.getUsername()); // ou renvoyer un DTO si nécessaire
        response.put("scoreRecalcule", avgScore);
        response.put("developerCount", devs.size());
        response.put("specialties", specialties);

        return ResponseEntity.ok(response);
    }

    



    
    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> verifierUsername(@PathVariable String username) {
        boolean exists = chefDeProjetService.existeParUsername(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> verifierEmail(@PathVariable String email) {
        boolean exists = chefDeProjetService.existeParEmail(email);
        return ResponseEntity.ok(exists);
    }
    @GetMapping("/{id}/developpeurs")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<List<Developpeur>> getDeveloppeursParChef(@PathVariable(name = "id")  Long id) {
        List<Developpeur> developpeurs = chefDeProjetService.getDeveloppeursParChef(id);
        return ResponseEntity.ok(developpeurs);
    }
    
    @GetMapping("chef/get/{id}")
    @PreAuthorize("hasRole('ROLE_CHEF')")
    public ResponseEntity<?> getChefById2(@PathVariable(name = "id") Long id) {
        Optional<ChefDeProjet> optionalChef = chefDeProjetRepository.findById(id);
        if (optionalChef.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Chef de projet non trouvé"));
        }

        ChefDeProjet chef = optionalChef.get();
        ChefDTO dto = new ChefDTO();
        dto.setId(chef.getId());
        dto.setUsername(chef.getUsername());
        dto.setEmail(chef.getEmail());
        dto.setSpecialite(chef.getSpecialite());
        dto.setScore(chef.getScore());

        if (chef.getImage() != null) {
            String base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(chef.getImage());
            dto.setImage(base64Image);
        }

        return ResponseEntity.ok(dto);
    }

    
    

    @PutMapping("chef/update-password/{id}")
    @PreAuthorize("hasRole('ROLE_CHEF')")
    public ResponseEntity<?> updateChefPassword(
            Authentication authentication,
            @PathVariable(name = "id") Long id,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long authenticatedId = userDetails.getId();

        if (!authenticatedId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous ne pouvez pas modifier ce mot de passe.");
        }

        Optional<ChefDeProjet> optionalChef = chefDeProjetRepository.findById(id);
        if (optionalChef.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ChefDeProjet chef = optionalChef.get();

        if (!passwordEncoder.matches(oldPassword, chef.getPassword())) {
            return ResponseEntity.badRequest().body("Ancien mot de passe incorrect.");
        }

        chef.setPassword(passwordEncoder.encode(newPassword));
        chefDeProjetRepository.save(chef);

        return ResponseEntity.ok(new MessageResponse("Mot de passe mis à jour avec succès."));
    }
    
    @PutMapping("/chef/update/{id}")
    @PreAuthorize("hasRole('ROLE_CHEF')")
    public ResponseEntity<?> updateChefWithImage(
            @PathVariable(name = "id") Long id,
            @ModelAttribute UpdateChefRequest request,
            Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Vous ne pouvez pas modifier un autre chef.");
        }

        Optional<ChefDeProjet> optionalChef = chefDeProjetRepository.findById(id);
        if (optionalChef.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ChefDeProjet chef = optionalChef.get();

        if (request.getOldPassword() != null && !request.getOldPassword().isBlank()) {
            if (!passwordEncoder.matches(request.getOldPassword(), chef.getPassword())) {
                return ResponseEntity.badRequest().body("Ancien mot de passe incorrect.");
            }
            if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
                chef.setPassword(passwordEncoder.encode(request.getNewPassword()));
            }
        }

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            chef.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            chef.setEmail(request.getEmail());
        }
        if (request.getSpecialite() != null) {
            chef.setSpecialite(request.getSpecialite());
        }
        if (request.getScore() != null) {
            chef.setScore(request.getScore());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                chef.setImage(request.getImage().getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur lors de la lecture de l'image : " + e.getMessage());
            }
        }

        chefDeProjetRepository.save(chef);
        return ResponseEntity.ok(new MessageResponse("Chef mis à jour avec succès."));
    }
    
    @PutMapping("chef/update-photo/{id}")
    @PreAuthorize("hasRole('ROLE_CHEF')")
    public ResponseEntity<?> updateChefPhoto(
            @PathVariable(name = "id") Long id,
            @RequestParam("image") MultipartFile image,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long authenticatedId = userDetails.getId();

        if (!authenticatedId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous ne pouvez pas modifier cette photo.");
        }

        Optional<ChefDeProjet> optionalChef = chefDeProjetRepository.findById(id);
        if (optionalChef.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ChefDeProjet chef = optionalChef.get();

        try {
            chef.setImage(image.getBytes()); 
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du traitement de l'image : " + e.getMessage());
        }

        chefDeProjetRepository.save(chef);
        return ResponseEntity.ok(new MessageResponse("Photo de profil mise à jour avec succès."));
    }

}
