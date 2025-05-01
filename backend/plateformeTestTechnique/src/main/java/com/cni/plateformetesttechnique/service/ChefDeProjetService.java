package com.cni.plateformetesttechnique.service;
import com.cni.plateformetesttechnique.model.ChefDeProjet;
import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.model.ERole;
import com.cni.plateformetesttechnique.model.Role;
import com.cni.plateformetesttechnique.repository.ChefDeProjetRepository;
import com.cni.plateformetesttechnique.repository.DeveloppeurRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;

import com.cni.plateformetesttechnique.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class ChefDeProjetService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ChefDeProjetRepository chefDeProjetRepository;
    @Autowired
    private DeveloppeurRepository developpeurRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private ScoreService scoreService;


    public ChefDeProjet ajouterChefDeProjet(ChefDeProjet chef) {
        // Vérifier si le rôle CHEF existe
        Role roleChef = roleRepository.findByName(ERole.ROLE_CHEF)
                .orElseThrow(() -> new RuntimeException("Error: Role CHEF not found."));

        // Attribuer automatiquement le rôle CHEF
        Set<Role> roles = new HashSet<>();
        roles.add(roleChef);
        chef.setRoles(roles);

        // Générer un mot de passe aléatoire sécurisé
        String generatedPassword = RandomStringUtils.randomAlphanumeric(10); // 10 caractères
        System.out.println("Mot de passe généré : " + generatedPassword); // Pour test

        // Crypter le mot de passe avant l'enregistrement
        chef.setPassword(encoder.encode(generatedPassword));
        chef.setActive(true);
        // Sauvegarder le chef de projet
        ChefDeProjet savedChef = chefDeProjetRepository.save(chef);

        // Envoyer le mot de passe par email (optionnel)
        sendGeneratedPasswordByEmail(chef.getEmail(),chef.getUsername(), generatedPassword);

        return savedChef;
    }
    public void sendGeneratedPasswordByEmail(String email, String username, String generatedPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email); // ✅ Email temporaire pour le test
        message.setSubject("Votre compte Chef de Projet a été créé");
        message.setText("Bonjour,\n\nVotre compte a été créé avec succès.\n"
                +"votre username est : "+username+"\n"
                + "Votre mot de passe temporaire est : " + generatedPassword + "\n"
                + "Merci de le changer après votre première connexion.\n\n"
                + "Cordialement,\nL'équipe de gestion");

        mailSender.send(message);
    }

//    public ChefDeProjet ajouterChefDeProjet(ChefDeProjet chef) {
//        // Récupérer le rôle Chef à partir de l'énumération ERole
//        Role roleChef = roleRepository.findByName(ERole.ROLE_CHEF)
//
//                .orElseThrow(() -> new RuntimeException("Error: Role CHEF not found."));
//
//        // Attribuer automatiquement le rôle Chef au ChefDeProjet
//        Set<Role> roles = new HashSet<>();
//        roles.add(roleChef);
//        chef.setRoles(roles);
//        chef.setPassword(encoder.encode(chef.getPassword()));
//
//        // Sauvegarder le chef de projet dans la base de données
//        return chefDeProjetRepository.save(chef);
//
//    }

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
    
   /* public ChefDeProjet assignerDeveloppeur(Long chefDeProjet_id, Long devId) {
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
    }*/
/*    public ChefDeProjet assignerDeveloppeur(Long chefDeProjet_id, Long devId) {
        Optional<ChefDeProjet> chefOpt = chefDeProjetRepository.findById(chefDeProjet_id);
        Optional<Developpeur> devOpt = developpeurRepository.findById(devId);

        if (chefOpt.isPresent() && devOpt.isPresent()) {
            ChefDeProjet chef = chefOpt.get();
            Developpeur developpeur = devOpt.get();

            // Ajout du développeur au chef de projet
            chef.getDeveloppeurs().add(developpeur);
            developpeur.setChefDeProjet(chef);

            // ✅ Mise à jour du champ is_assigned
            developpeur.setAssigned(true);

            // Sauvegarde des changements
            developpeurRepository.save(developpeur);
            return chefDeProjetRepository.save(chef);
        } else {
            throw new EntityNotFoundException("Chef de projet ou développeur introuvable.");
        }
    }*/
   /* public ChefDeProjet assignerDeveloppeur(Long chefId, Long devId) {
        ChefDeProjet chef = chefDeProjetRepository.findById(chefId)
            .orElseThrow(() -> new RuntimeException("Chef de projet non trouvé"));

        Developpeur developpeur = developpeurRepository.findById(devId)
            .orElseThrow(() -> new RuntimeException("Développeur non trouvé"));

        developpeur.setChefDeProjet(chef);
        developpeurRepository.save(developpeur);
        chefDeProjetRepository.save(chef);

        return chef;
    }*/
    @Transactional
    public ChefDeProjet assignerDeveloppeur(Long chefId, Long devId) {
        ChefDeProjet chef = chefDeProjetRepository.findById(chefId)
            .orElseThrow(() -> new RuntimeException("Chef introuvable"));

        Developpeur dev = developpeurRepository.findById(devId)
            .orElseThrow(() -> new RuntimeException("Développeur introuvable"));

        // Si le développeur a pas encore de score, initialiser à 0.0
        if (dev.getScore() == null) {
            dev.setScore(0.0);
        }

        // Assigner le chef au développeur
        dev.setChefDeProjet(chef);
        developpeurRepository.save(dev);

        // Recalcul du score du chef
        Double nouveauScore = scoreService.calculerScoreChef(chefId);
        chef.setScore(nouveauScore);

        return chefDeProjetRepository.save(chef);
    }


    
    public boolean existeParUsername(String username) {
        return chefDeProjetRepository.existsByUsername(username);
    }

    public boolean existeParEmail(String email) {
        return chefDeProjetRepository.existsByEmail(email);
    }
    @JsonIgnore
    public List<Developpeur> getDeveloppeursParChef(Long chefDeProjetId) {
        Optional<ChefDeProjet> chefOpt = chefDeProjetRepository.findById(chefDeProjetId);
        if (chefOpt.isPresent()) {
            return chefOpt.get().getDeveloppeurs();
        } else {
            throw new EntityNotFoundException("Chef de projet non trouvé avec l'ID: " + chefDeProjetId);
        }
    }

}
