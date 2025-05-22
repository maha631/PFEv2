/*package com.cni.plateformetesttechnique.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.service.DeveloppeurService;

@RestController
@RequestMapping("/api/developpeurs")
@CrossOrigin(origins = "*")
public class DeveloppeurController {

    @Autowired
    private DeveloppeurService developpeurService;

    @GetMapping
    public List<Developpeur> getAllDeveloppeurs() {
        return developpeurService.getAllDeveloppeurs();
    }
    
    @GetMapping("/non-assignes")
    public ResponseEntity<List<Developpeur>> getDeveloppeursNonAssignes() {
        List<Developpeur> nonAssignes = developpeurService.getDeveloppeursNonAssignes();
        return ResponseEntity.ok(nonAssignes);
    }
    @GetMapping("/{devId}/is-assigned")
    public ResponseEntity<Boolean> isAssigned(@PathVariable(name = "devId")  Long devId) {
        boolean assigned = developpeurService.isAssigned(devId);
        return ResponseEntity.ok(assigned);
    }


}
*/
package com.cni.plateformetesttechnique.controller;

import java.io.IOException;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.cni.plateformetesttechnique.dto.DeveloppeurDTO;
import com.cni.plateformetesttechnique.dto.UpdateDeveloppeurRequest;
import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.model.User;
import com.cni.plateformetesttechnique.repository.DeveloppeurRepository;
import com.cni.plateformetesttechnique.repository.UserRepository;
import com.cni.plateformetesttechnique.response.MessageResponse;
import com.cni.plateformetesttechnique.security.services.UserDetailsImpl;
import com.cni.plateformetesttechnique.service.DeveloppeurService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api/developpeurs")
@CrossOrigin(origins = "*")
public class DeveloppeurController {

    @Autowired
    private DeveloppeurService developpeurService;
    
    @Autowired
    private  UserRepository  userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DeveloppeurRepository developpeurRepository ;
    
    
    @GetMapping
    public List<Developpeur> getAllDeveloppeurs() {
        return developpeurService.getAllDeveloppeurs();
    }

    @GetMapping("/non-assignes")
    public ResponseEntity<List<Developpeur>> getDeveloppeursNonAssignes() {
        List<Developpeur> nonAssignes = developpeurService.getDeveloppeursNonAssignes();
        return ResponseEntity.ok(nonAssignes);
    }

    @GetMapping("/{devId}/is-assigned")
    public ResponseEntity<Boolean> isAssigned(@PathVariable(name = "devId") Long devId) {
        boolean assigned = developpeurService.isAssigned(devId);
        return ResponseEntity.ok(assigned);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Developpeur> getDeveloppeurById(@PathVariable(name = "id") Long id) {
        Developpeur developpeur = developpeurService.getDeveloppeurById(id);
        return ResponseEntity.ok(developpeur);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Developpeur> updateDeveloppeur(@PathVariable(name = "id") Long id, @RequestBody Developpeur profile) {
        Developpeur updatedDev = developpeurService.updateDeveloppeur(id, profile);
        return ResponseEntity.ok(updatedDev);
    }
   
    @GetMapping("developpeur/get/{id}")
    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')")
    public ResponseEntity<?> getDeveloppeurById2(@PathVariable(name = "id") Long id) {
        Optional<User> optionalDev = userRepository.findById(id);
        if (optionalDev.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(new MessageResponse("Développeur non trouvé"));
        }

        User dev = optionalDev.get();
        DeveloppeurDTO dto = new DeveloppeurDTO();
        dto.setId(dev.getId());
        dto.setUsername(dev.getUsername());
        dto.setEmail(dev.getEmail());

        if (dev.getImage() != null) {
            String base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(dev.getImage());
            dto.setImage(base64Image);
        }
        
        if (dev instanceof Developpeur developpeur) {
            dto.setSpecialite(developpeur.getSpecialite());
            dto.setScore(developpeur.getScore());
            dto.setTechnologies(developpeur.getTechnologies());
            dto.setExperience(developpeur.getExperience());
        }


        return ResponseEntity.ok(dto);
    }
    
    
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')")
    public ResponseEntity<?> updateDeveloppeur(
            @PathVariable(name="id") Long id,
            @ModelAttribute UpdateDeveloppeurRequest request,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body("Vous ne pouvez pas modifier un autre développeur.");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }

        User user = optionalUser.get();

        // Vérifie que l'utilisateur est bien un développeur
        if (!(user instanceof Developpeur)) {
            return ResponseEntity.badRequest().body("L'utilisateur n'est pas un développeur.");
        }

        Developpeur developpeur = (Developpeur) user;

        // Mise à jour mot de passe
        if (request.getOldPassword() != null && !request.getOldPassword().isBlank()) {
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body("Ancien mot de passe incorrect.");
            }
            if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            }
        }

        // Mises à jour générales
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                user.setImage(request.getImage().getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body("Erreur de lecture d'image : " + e.getMessage());
            }
        }

        // Mises à jour spécifiques au développeur
        if (request.getSpecialite() != null) {
            developpeur.setSpecialite(request.getSpecialite());
        }
        if (request.getScore() != null) {
            developpeur.setScore(request.getScore());
        }
        if (request.getTechnologies() != null) {
            developpeur.setTechnologies(request.getTechnologies());
        }
        if (request.getExperience() != null) {
            developpeur.setExperience(request.getExperience());
        }

        userRepository.save(developpeur);
        return ResponseEntity.ok(new MessageResponse("Développeur mis à jour avec succès."));
    }
    

    @PutMapping("/update-password/{id}")
    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')")
    public ResponseEntity<?> updateDeveloppeurPassword(
            Authentication authentication,
            @PathVariable(name="id") Long id,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body("Vous ne pouvez pas modifier un autre compte.");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body("Ancien mot de passe incorrect.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Mot de passe mis à jour avec succès."));
    }

    
    @PutMapping("/update-photo/{id}")
    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')")
    public ResponseEntity<?> updateDeveloppeurPhoto(
            @PathVariable(name="id") Long id,
            @RequestParam("image") MultipartFile image,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body("Vous ne pouvez pas modifier un autre compte.");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }

        User user = optionalUser.get();

        try {
            user.setImage(image.getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Erreur lors de la lecture de l'image : " + e.getMessage());
        }

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Photo mise à jour avec succès."));
    }


    @GetMapping("/photo/{id}")
    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')")
    public ResponseEntity<byte[]> getDeveloppeurPhoto(@PathVariable(name="id") Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty() || optionalUser.get().getImage() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageData = optionalUser.get().getImage();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // ou IMAGE_PNG si c’est du PNG
        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }

    
   
}

