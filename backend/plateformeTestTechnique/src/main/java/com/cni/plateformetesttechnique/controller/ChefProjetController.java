package com.cni.plateformetesttechnique.controller;


import com.cni.plateformetesttechnique.model.ChefDeProjet;
import com.cni.plateformetesttechnique.service.ChefDeProjetService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ChefDeProjet getChefById(@PathVariable Long id) {
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
}
