package com.cni.plateformetesttechnique.controller;


import com.cni.plateformetesttechnique.model.ChefDeProjet;
import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.service.ChefDeProjetService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chefdeprojet")
@CrossOrigin(origins = "*")
public class ChefProjetController {

    @Autowired
    private ChefDeProjetService chefDeProjetService;
 

   
    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ChefDeProjet ajouterChef(@RequestBody ChefDeProjet chef) {
        return chefDeProjetService.ajouterChefDeProjet(chef);
    }

   
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHEF')")
    public ChefDeProjet modifierChef(@PathVariable(name = "id") Long id, @RequestBody ChefDeProjet nouveauChef) {
        return chefDeProjetService.modifierChefDeProjet(id, nouveauChef);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void supprimerChef(@PathVariable(name = "id")  Long id) {
        chefDeProjetService.supprimerChefDeProjet(id);
    }

  
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ChefDeProjet getChefById(@PathVariable(name = "id") Long id) {
        return chefDeProjetService.getChefDeProjetById(id);
    }

  
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ChefDeProjet> getAllChefs() {
        return chefDeProjetService.getAllChefsDeProjet();
    }
    @PostMapping("/{chefId}/assign/{devId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ChefDeProjet assignerDeveloppeur(@PathVariable(name = "chefId") Long chefId, @PathVariable(name = "devId") Long devId) {
        return chefDeProjetService.assignerDeveloppeur(chefId, devId);
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
    public ResponseEntity<List<Developpeur>> getDeveloppeursParChef(@PathVariable(name = "id")  Long id) {
        List<Developpeur> developpeurs = chefDeProjetService.getDeveloppeursParChef(id);
        return ResponseEntity.ok(developpeurs);
    }
}
