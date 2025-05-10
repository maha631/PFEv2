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

    public Double calculerScoreParTentative(Long developpeurTestScoreId) {
        // Étape 1 : récupérer l'objet DeveloppeurTestScore
        DeveloppeurTestScore developpeurTestScore = developpeurTestScoreRepository.findById(developpeurTestScoreId)
                .orElseThrow(() -> new RuntimeException("Tentative non trouvée"));

        Long testId = developpeurTestScore.getTest().getId();
        Long developpeurId = developpeurTestScore.getDeveloppeur().getId();

        // Étape 2 : récupérer toutes les réponses de CETTE tentative
        List<DeveloppeurResponse> responses = developpeurResponseRepository
                .findByDeveloppeurTestScore_Id(developpeurTestScoreId);

        // Étape 3 : récupérer les questions et total des points
        List<TestQuestion> testQuestions = testQuestionRepository.findByTestId(testId);
        double totalPoints = testQuestions.stream().mapToDouble(TestQuestion::getPoints).sum();

        if (totalPoints == 0) {
            throw new RuntimeException("Le test ne contient aucune question avec points.");
        }

        // Étape 4 : calculer les points obtenus
        double pointsObtenus = 0;
        for (DeveloppeurResponse response : responses) {
            if (Boolean.TRUE.equals(response.getIsCorrect())) {
                TestQuestion tq = testQuestions.stream()
                        .filter(q -> q.getQuestion().getId().equals(response.getQuestion().getId()))
                        .findFirst().orElse(null);
                if (tq != null) {
                    pointsObtenus += tq.getPoints();
                }
            }
        }

        // Étape 5 : calcul final du score
        double scoreFinal = (pointsObtenus / totalPoints) * 100;

        // Étape 6 : enregistrer dans la tentative actuelle
        developpeurTestScore.setScore(scoreFinal);
        developpeurTestScoreRepository.save(developpeurTestScore);

        // Mise à jour globale si tu veux
        updateGlobalScore(developpeurTestScore.getDeveloppeur());

        return scoreFinal;
    }

    public Double calculerScore(Long testId, Long developpeurId) {
        System.out.println(">>> Début du calcul du score");
        System.out.println("testId: " + testId);
        System.out.println("developpeurId: " + developpeurId);

        // Étape 1 : récupérer test & développeur
        Developpeur developpeur = developpeurRepository.findById(developpeurId)
                .orElseThrow(() -> new RuntimeException("Développeur non trouvé"));
        System.out.println("Développeur trouvé: " + developpeur.getUsername());

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test non trouvé"));
        System.out.println("Test trouvé: " + test.getTitre());

        // Étape 2 : récupérer la dernière tentative
        DeveloppeurTestScore developpeurTestScore = developpeurTestScoreRepository
                .findTopByDeveloppeur_IdAndTest_IdOrderByAttemptNumberDesc(developpeurId, testId)
                .orElseThrow(() -> new RuntimeException("Aucune tentative trouvée"));
        System.out.println("Tentative récupérée - Attempt #: " + developpeurTestScore.getAttemptNumber());

        // Étape 3 : récupérer toutes les réponses de cette tentative
        List<DeveloppeurResponse> responses = developpeurResponseRepository
                .findByDeveloppeurTestScore_Id(developpeurTestScore.getId());

        System.out.println("Nombre de réponses récupérées: " + responses.size());

        // Étape 4 : récupérer les questions du test et total des points
        List<TestQuestion> testQuestions = testQuestionRepository.findByTestId(testId);
        double totalPoints = testQuestions.stream().mapToDouble(TestQuestion::getPoints).sum();
        System.out.println("Nombre de questions dans le test: " + testQuestions.size());
        System.out.println("Total des points du test: " + totalPoints);

        if (totalPoints == 0) {
            throw new RuntimeException("Le test ne contient aucune question avec points.");
        }

        // Étape 5 : calculer les points obtenus
        double pointsObtenus = 0;
        for (DeveloppeurResponse response : responses) {
            System.out.println("Traitement réponse - Question ID: " + response.getQuestion().getId());
            if (Boolean.TRUE.equals(response.getIsCorrect())) {
                TestQuestion tq = testQuestions.stream()
                        .filter(q -> q.getQuestion().getId().equals(response.getQuestion().getId()))
                        .findFirst().orElse(null);
                if (tq != null) {
                    System.out.println("Bonne réponse trouvée, ajout de " + tq.getPoints() + " points");
                    pointsObtenus += tq.getPoints();
                } else {
                    System.out.println("Aucun point attribué pour cette question (non trouvée dans TestQuestion)");
                }
            } else {
                System.out.println("Réponse incorrecte.");
            }
        }

        // Étape 6 : calcul final du score
        double scoreFinal = (pointsObtenus / totalPoints) * 100;
        System.out.println("Points obtenus: " + pointsObtenus);
        System.out.println("Score final calculé: " + scoreFinal);

        // Étape 7 : enregistrer le score dans DeveloppeurTestScore
        developpeurTestScore.setScore(scoreFinal);
        developpeurTestScoreRepository.save(developpeurTestScore);
        System.out.println("Score enregistré dans DeveloppeurTestScore");

        // Étape 8 : mettre à jour le score global du développeur
        updateGlobalScore(developpeur);
        System.out.println("Score global du développeur mis à jour");

        System.out.println(">>> Fin du calcul du score");
        return scoreFinal;
    }

    //    public Double calculerScore(Long testId, Long developpeurId) {
//        // Récupérer toutes les réponses du développeur pour ce test
////        List<DeveloppeurResponse> responses = developpeurResponseRepository.findByTest_IdAndDeveloppeur_Id(testId, developpeurId);
//
//        // Récupérer toutes les questions du test
//        List<TestQuestion> testQuestions = testQuestionRepository.findByTestId(testId);
//
//        // Calculer le total des points du test
//        double totalPoints = testQuestions.stream().mapToDouble(TestQuestion::getPoints).sum();
//
//        // Calculer le score
//        double pointsObtenus = 0;
//        for (DeveloppeurResponse response : responses) {
//            if (response.getIsCorrect()) {
//                // Trouver les points associés à cette question
//                TestQuestion testQuestion = testQuestions.stream()
//                        .filter(tq -> tq.getQuestion().getId().equals(response.getQuestion().getId()))
//                        .findFirst()
//                        .orElse(null);
//
//                if (testQuestion != null) {
//                    pointsObtenus += testQuestion.getPoints();
//                }
//            }
//        }
//
//        // Calculer le score en pourcentage
//        double scoreFinal = (pointsObtenus / totalPoints) * 100;
//
//        // Créer une instance de DeveloppeurTestScore et l'enregistrer
//        Developpeur developpeur = developpeurRepository.findById(developpeurId)
//                .orElseThrow(() -> new RuntimeException("Développeur non trouvé"));
//        Test test = testRepository.findById(testId)
//                .orElseThrow(() -> new RuntimeException("Test non trouvé"));
//
//        DeveloppeurTestScore developpeurTestScore = new DeveloppeurTestScore(developpeur, test, scoreFinal);
//
//        // Sauvegarder le score
//        developpeurTestScoreRepository.save(developpeurTestScore);
//// Mettre à jour le score global du développeur
//        updateGlobalScore(developpeur);
//        // Retourner le score final en pourcentage
//        return scoreFinal;
//    }
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