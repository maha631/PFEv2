package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.dto.DeveloppeurResultResponse;
import com.cni.plateformetesttechnique.dto.TestStatsResponse;
import com.cni.plateformetesttechnique.model.*;
import com.cni.plateformetesttechnique.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class ScoreService {
    @Autowired
    private ChefDeProjetRepository chefDeProjetRepository;
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
        if (developpeurTestScore.getFinishedAt() == null) {
            developpeurTestScore.setFinishedAt(LocalDateTime.now());
            developpeurTestScoreRepository.save(developpeurTestScore);
        }
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
    
    
//    public Double calculerScoreChef(Long chefDeProjet_id) {
//        // Récupérer tous les développeurs sous la responsabilité du chef
//        List<Long> developpeurs = developpeurRepository.findDeveloppeursBychefDeProjet_id(chefDeProjet_id);
//
//        if (developpeurs.isEmpty()) {
//            return 0.0; // Aucun développeur, donc score de 0
//        }
//
//        // Calculer la moyenne des scores de ses développeurs
//        double totalScore = developpeurs.stream()
//                .mapToDouble(developpeurId -> getGlobalScore(developpeurId))
//                .average()
//                .orElse(0.0);
//
//        return totalScore;
//    }

    public Double calculerScoreChef(Long chefDeProjet_id) {
        // Récupérer tous les développeurs sous la responsabilité du chef
        List<Long> developpeurs = developpeurRepository.findDeveloppeursIdsByChefDeProjetId(chefDeProjet_id);

        if (developpeurs.isEmpty()) {
            return 0.0; // Aucun développeur, donc score de 0
        }
        // Calculer la moyenne des scores de ses développeurs
//        double totalScore = developpeurs.stream()
//                .mapToDouble(developpeurId -> getGlobalScore(developpeurId))
//                .average()
//                .orElse(0.0);
        double totalScore = developpeurs.stream()
                .mapToDouble(developpeurId -> {
                    Double score = getGlobalScore(developpeurId);
                    return (score != null) ? score : 0.0;
                })
                .average()
                .orElse(0.0);

        return totalScore;
    }

    public TestStatsResponse getTestStats(Long testId) {
        List<DeveloppeurTestScore> allAttempts = developpeurTestScoreRepository.findByTestId(testId);

        // Regrouper par développeur et garder la tentative avec le meilleur score
        Map<Long, DeveloppeurTestScore> bestAttempts = allAttempts.stream()
                .collect(Collectors.toMap(
                        s -> s.getDeveloppeur().getId(), // clé = id développeur
                        s -> s,
                        (s1, s2) -> s1.getScore() >= s2.getScore() ? s1 : s2 // garder le meilleur score
                ));

        List<DeveloppeurTestScore> finalScores = new ArrayList<>(bestAttempts.values());

        int participants = finalScores.size();

        double moyenne = finalScores.stream()
                .mapToDouble(DeveloppeurTestScore::getScore)
                .average()
                .orElse(0);

        long dureeMoyen = (long) finalScores.stream()
                .map(DeveloppeurTestScore::getDuree)
                .filter(Objects::nonNull)
                .mapToLong(d -> d.toMinutes())
                .average()
                .orElse(0);

        DeveloppeurTestScore meilleur = finalScores.stream()
                .max(Comparator.comparing(DeveloppeurTestScore::getScore))
                .orElse(null);

        DeveloppeurTestScore pire = finalScores.stream()
                .min(Comparator.comparing(DeveloppeurTestScore::getScore))
                .orElse(null);

        int totalReussite = (int) finalScores.stream().filter(s -> s.getScore() >= 50).count();
        int totalEchec = participants - totalReussite;

        double tauxReussite = participants == 0 ? 0 : (totalReussite * 100.0) / participants;
        double tauxEchec = 100 - tauxReussite;

        return new TestStatsResponse(
                moyenne,
                dureeMoyen,
                meilleur != null
                        ? new TestStatsResponse.ScoreInfo(meilleur.getDeveloppeur().getUsername(), meilleur.getScore())
                        : null,
                pire != null
                        ? new TestStatsResponse.ScoreInfo(pire.getDeveloppeur().getUsername(), pire.getScore())
                        : null,
                new TestStatsResponse.ResultStats(tauxEchec, totalEchec, participants),
                new TestStatsResponse.ResultStats(tauxReussite, totalReussite, participants)
        );

    }
    public List<DeveloppeurResultResponse> getResultatsParTest(Long testId) {
        List<DeveloppeurTestScore> allAttempts = developpeurTestScoreRepository.findByTestId(testId);

        Map<Long, List<DeveloppeurTestScore>> groupedByDev = allAttempts.stream()
                .collect(Collectors.groupingBy(s -> s.getDeveloppeur().getId()));

        List<DeveloppeurResultResponse> results = new ArrayList<>();

        for (Map.Entry<Long, List<DeveloppeurTestScore>> entry : groupedByDev.entrySet()) {
            List<DeveloppeurTestScore> tentatives = entry.getValue();
            tentatives.sort(Comparator.comparing(DeveloppeurTestScore::getCreatedAt)); // chronologique

            DeveloppeurTestScore best = tentatives.stream()
                    .max(Comparator.comparing(DeveloppeurTestScore::getScore))
                    .orElse(null);

            if (best == null) continue;

            int attemptNumber = tentatives.indexOf(best) + 1;

            // Construire badge et état
            String etat = best.getScore() >= 65 ? "succès" : "échec";
            String badge = "";
            String apparence = "";

            if (tentatives.size() >= 3 && best.getScore() < 65) {
                badge = "🔄 Persévérant";
                apparence = "Fond bleu";
            } else if (best.getScore() >= 65) {
                badge = "✅ Réussi";
                apparence = "Fond vert";
            } else {
                badge = "❌ Échec";
                apparence = "Fond rouge";
            }

            if (attemptNumber == 1) {
                badge += " 🆕 Nouveau";
            }

            String temps = best.getDuree() != null
                    ? String.format("%dmin %ds", best.getDuree().toMinutes(), best.getDuree().toSecondsPart())
                    : "N/A";

            DeveloppeurResultResponse res = new DeveloppeurResultResponse();
            res.setId(best.getDeveloppeur().getId());
            res.setNom(best.getDeveloppeur().getUsername());
            res.setEmail(best.getDeveloppeur().getEmail());
            res.setScore(best.getScore());
            res.setTentative(attemptNumber + "ème");
            res.setTemps(temps);
            res.setEtat(etat);
            res.setBadge(badge);
            res.setApparence(apparence);

            results.add(res);
        }

        // Gérer podium (🥇 🥈 🥉) pour tests publics uniquement
        // Tri décroissant + set badge selon index
        Test test = testRepository.findById(testId).orElse(null);
        if (test != null) {
            results.sort(Comparator.comparing(DeveloppeurResultResponse::getScore).reversed());
            for (int i = 0; i < results.size(); i++) {
                if (i == 0) {
                    results.get(i).setBadge("🥇 1er");
                    results.get(i).setApparence("Dégradé or");
                } else if (i == 1) {
                    results.get(i).setBadge("🥈 2e");
                } else if (i == 2) {
                    results.get(i).setBadge("🥉 3e");
                }
            }
        }

        return results;
    }
  
    
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
}