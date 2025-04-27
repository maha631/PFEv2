package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.model.*;
import com.cni.plateformetesttechnique.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service

public class ScoreService {
    @Autowired
    private DeveloppeurResponseRepository developpeurResponseRepository;
    @Autowired
    private TestQuestionRepository testQuestionRepository;
    @Autowired
    private DeveloppeurRepository developpeurRepository;
    @Autowired
    private DeveloppeurTestScoreRepository developpeurTestScoreRepository;
    @Autowired
    private TestRepository testRepository;
    public Double calculerScore(Long testId, Long developpeurId) {
        // Récupérer toutes les réponses du développeur pour ce test
        List<DeveloppeurResponse> responses = developpeurResponseRepository.findByTest_IdAndDeveloppeur_Id(testId, developpeurId);

        // Récupérer toutes les questions du test
        List<TestQuestion> testQuestions = testQuestionRepository.findByTestId(testId);

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
        updateGlobalScore(developpeur);
        // Retourner le score final en pourcentage
        return scoreFinal;
    }
    public Double getScoreByDeveloppeurAndTest(Long developpeurId, Long testId) {
        // Recherche de l'entité DeveloppeurTestScore
        DeveloppeurTestScore developpeurTestScore = developpeurTestScoreRepository
                .findFirstByDeveloppeurIdAndTestIdOrderByIdDesc(developpeurId, testId);

        // Retourner uniquement le score ou null si pas trouvé
        return developpeurTestScore != null ? developpeurTestScore.getScore() : null;
    }

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
    }
    public Double getGlobalScore(Long developpeurId) {
        // Récupérer le développeur à partir de son ID
        Developpeur developpeur = developpeurRepository.findById(developpeurId)
                .orElseThrow(() -> new RuntimeException("Développeur introuvable"));

        // Retourner le score global du développeur
        return developpeur.getScore();
    }
    
    
    public Double calculerScoreChef(Long chefDeProjet_id) {
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

        return totalScore;
    }
}