package com.cni.plateformetesttechnique.service;
import com.cni.plateformetesttechnique.model.ChefDeProjet;
import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.repository.ChefDeProjetRepository;
import com.cni.plateformetesttechnique.repository.DeveloppeurRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ChefDeProjetService {

    @Autowired
    private ChefDeProjetRepository chefDeProjetRepository;
    @Autowired
    private DeveloppeurRepository developpeurRepository;

    // Ajouter un Chef de Projet
    public ChefDeProjet ajouterChefDeProjet(ChefDeProjet chef) {
        return chefDeProjetRepository.save(chef);
    }

    public ChefDeProjet modifierChefDeProjet(Long id, ChefDeProjet nouveauChef) {
        Optional<ChefDeProjet> optionalChef = chefDeProjetRepository.findById(id);
        if (optionalChef.isPresent()) {
            ChefDeProjet chefExistant = optionalChef.get();

            // Mise à jour des attributs hérités de User
            chefExistant.setUsername(nouveauChef.getUsername());
            chefExistant.setEmail(nouveauChef.getEmail());
            chefExistant.setPassword(nouveauChef.getPassword());
            chefExistant.setActive(nouveauChef.getActive());

            // Mise à jour des attributs spécifiques à ChefDeProjet
            chefExistant.setSpecialite(nouveauChef.getSpecialite());
            chefExistant.setScore(nouveauChef.getScore());

            return chefDeProjetRepository.save(chefExistant);
        } else {
            throw new EntityNotFoundException("Chef de projet non trouvé avec l'ID: " + id);
        }
    }


    // Supprimer un Chef de Projet
    public void supprimerChefDeProjet(Long id) {
        chefDeProjetRepository.deleteById(id);
    }

    // Récupérer un Chef de Projet par ID
    public ChefDeProjet getChefDeProjetById(Long id) {
        return chefDeProjetRepository.findById(id).orElse(null);
    }

    // Récupérer tous les Chefs de Projet
    public List<ChefDeProjet> getAllChefsDeProjet() {
        return chefDeProjetRepository.findAll();
    }
    
    public ChefDeProjet assignerDeveloppeur(Long chefDeProjet_id, Long devId) {
        Optional<ChefDeProjet> chefOpt = chefDeProjetRepository.findById(chefDeProjet_id);
        Optional<Developpeur> devOpt = developpeurRepository.findById(devId);

        if (chefOpt.isPresent() && devOpt.isPresent()) {
            ChefDeProjet chef = chefOpt.get();
            Developpeur developpeur = devOpt.get();

            // Ajout du développeur au chef de projet
            chef.getDeveloppeurs().add(developpeur);
            developpeur.setChefDeProjet(chef);

            developpeurRepository.save(developpeur);
            return chefDeProjetRepository.save(chef);
        } else {
            throw new EntityNotFoundException("Chef de projet ou développeur introuvable.");
        }
    }

}

