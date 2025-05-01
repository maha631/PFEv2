package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.model.*;
import com.cni.plateformetesttechnique.repository.*;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
@Service

public class ScoreService {
    @Autowired
    private DeveloppeurResponseRepository developpeurResponseRepository;
    @Autowired
    private TestQuestionRepository testQuestionRepository;
    @Autowired
    private DeveloppeurRepository developpeurRepository;
    @Autowired
    private ChefDeProjetRepository chefDeProjetRepository;
    @Autowired
    private DeveloppeurTestScoreRepository developpeurTestScoreRepository;
    @Autowired
    private TestRepository testRepository;
    public Double calculerScore(Long testId, Long developpeurId) {
        // Récupérer toutes les réponses du développeur pour ce test
        List<DeveloppeurResponse> responses = developpeurResponseRepository.findByTest_IdAndDeveloppeur_Id(testId, developpeurId);

        // Récupérer toutes les questions du test
        List<TestQuestion> testQuestions = testQuestionRepository.findByTestId(testId);
//        if (responses.size() < testQuestions.size()) {
//            throw new RuntimeException("Le développeur n'a pas encore complété toutes les questions du test !");
//        }
        // Calculer le total des points du test
        double totalPoints = testQuestions.stream().mapToDouble(TestQuestion::getPoints).sum();

        // Calculer le score
        double pointsObtenus = 0;
        for (DeveloppeurResponse response : responses) {
            if (response.getIsCorrect()) {
                // Trouver les points associés à cette question
                TestQuestion testQuestion = testQuestions.stream()
                        .filter(tq -> tq.getQuestion().getId().equals(response.getQuestion().getId()))
                        .findFirst()
                        .orElse(null);

                if (testQuestion != null) {
                    pointsObtenus += testQuestion.getPoints();
                }
            }
        }

        // Calculer le score en pourcentage
        double scoreFinal = (pointsObtenus / totalPoints) * 100;

        // Créer une instance de DeveloppeurTestScore et l'enregistrer
        Developpeur developpeur = developpeurRepository.findById(developpeurId)
                .orElseThrow(() -> new RuntimeException("Développeur non trouvé"));
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test non trouvé"));

        DeveloppeurTestScore developpeurTestScore = new DeveloppeurTestScore(developpeur, test, scoreFinal);

        // Sauvegarder le score
        developpeurTestScoreRepository.save(developpeurTestScore);
// Mettre à jour le score global du développeur
        updateDeveloppeurGlobalScore(developpeur);
       
        return scoreFinal;
    }
    public Double getScoreByDeveloppeurAndTest(Long developpeurId, Long testId) {
        // Recherche de l'entité DeveloppeurTestScore
        DeveloppeurTestScore developpeurTestScore = developpeurTestScoreRepository
                .findFirstByDeveloppeurIdAndTestIdOrderByIdDesc(developpeurId, testId);

        // Retourner uniquement le score ou null si pas trouvé
        return developpeurTestScore != null ? developpeurTestScore.getScore() : null;
    }
/*
    private void updateGlobalScore(Developpeur developpeur) {
        // Récupérer tous les scores du développeur dans tous les tests qu'il a passés
        List<DeveloppeurTestScore> scores = developpeurTestScoreRepository.findByDeveloppeur(developpeur);

        // Calculer la moyenne des scores
        double globalScore = scores.stream().mapToDouble(DeveloppeurTestScore::getScore).average().orElse(0.0);

        // Mettre à jour le score du développeur
        developpeur.setScore(globalScore);

        // Sauvegarder le développeur avec son score mis à jour
        developpeurRepository.save(developpeur);

        System.out.println("Score global mis à jour pour le développeur : " + globalScore);
        ChefDeProjet chef = developpeur.getChefDeProjet();
        if (chef != null) {
            updateChefScore(chef);
            System.out.println("Score mis à jour pour le chef de projet (ID: " + chef.getId() + ")");
            chefDeProjetRepository.save(chef);
            
        }
        
    }*/
    public Double getGlobalScore(Long developpeurId) {
        // Récupérer le développeur à partir de son ID
        Developpeur developpeur = developpeurRepository.findById(developpeurId)
                .orElseThrow(() -> new RuntimeException("Développeur introuvable"));

        // Retourner le score global du développeur
        return developpeur.getScore();
        
    }
    
    
   /*public Double calculerScoreChef(Long chefDeProjet_id) {
        // Récupérer tous les développeurs sous la responsabilité du chef
        List<Long> developpeurs = developpeurRepository.findDeveloppeursBychefDeProjet_id(chefDeProjet_id);

        if (developpeurs.isEmpty()) {
            return 0.0; // Aucun développeur, donc score de 0
        }

        // Calculer la moyenne des scores de ses développeurs
        double totalScore = developpeurs.stream()
                .mapToDouble(developpeurId -> getGlobalScore(developpeurId))
                .average()
                .orElse(0.0);

        return totalScore; }*/
    
    public Double calculerScoreChef(Long chefId) {
        List<Developpeur> developpeurs = developpeurRepository.findByChefDeProjetId(chefId);

        if (developpeurs.isEmpty()) return 0.0;

        double total = 0.0;
        for (Developpeur dev : developpeurs) {
            total += dev.getScore() != null ? dev.getScore() : 0.0;
        }

        return total / developpeurs.size(); // moyenne
        
    } 
  /*  public void updateChefScore(ChefDeProjet chef) {
        if (chef != null && chef.getDeveloppeurs() != null) {
            double avgScore = chef.getDeveloppeurs().stream()
                .map(Developpeur::getScore)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

            chef.setScore(avgScore);
            chefDeProjetRepository.save(chef);
            System.out.println("Score du chef " + chef.getUsername() + " mis à jour : " + avgScore);
        }
    }*/
    public double updateChefScore(Long chefId) {
        ChefDeProjet chef = chefDeProjetRepository.findById(chefId).orElse(null);
        if (chef == null) return 0.0;

        List<Developpeur> devs = chef.getDeveloppeurs() != null ? chef.getDeveloppeurs() : Collections.emptyList();

        double avgScore = devs.stream()
            .map(Developpeur::getScore)
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);

        chef.setScore(avgScore);
        chefDeProjetRepository.save(chef);

        return avgScore;
    }

    @Transactional
    protected void updateDeveloppeurGlobalScore(Developpeur developpeur) {
        double globalScore = developpeurTestScoreRepository.findByDeveloppeur(developpeur)
            .stream()
            .mapToDouble(DeveloppeurTestScore::getScore)
            .average()
            .orElse(0.0);

        developpeur.setScore(globalScore);
        developpeurRepository.save(developpeur);

        // Mise à jour du chef si le développeur en a un
        if (developpeur.getChefDeProjet() != null) {
            updateChefScore(developpeur.getChefDeProjet());
        }
    }

    @Transactional
    public void updateChefScore(ChefDeProjet chef) {
        double avgScore = chef.getDeveloppeurs().stream()
            .map(Developpeur::getScore)
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);

        chef.setScore(avgScore);
        chefDeProjetRepository.save(chef);
    }
	
	// Exemple : recalcul du score du chef quand un dev est modifié
	public void mettreAJourScoreDeveloppeur(Long id, Double nouveauScore) {
	    Developpeur dev = developpeurRepository.findById(id).orElseThrow();
	    dev.setScore(nouveauScore);
	    developpeurRepository.save(dev);

	    ChefDeProjet chef = dev.getChefDeProjet();
	    updateChefScore(chef); // cette méthode agrège les scores des développeurs
	}

   }

    




