package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.dto.DeveloppeurDashboardDTO;
import com.cni.plateformetesttechnique.dto.TestStatDTO;
import com.cni.plateformetesttechnique.model.*;
import com.cni.plateformetesttechnique.repository.DeveloppeurRepository;
import com.cni.plateformetesttechnique.repository.DeveloppeurResponseRepository;
import com.cni.plateformetesttechnique.repository.DeveloppeurTestScoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class DashboardService {

    @Autowired
    private DeveloppeurRepository developpeurRepository;

    @Autowired
    private DeveloppeurResponseRepository responseRepository;

    @Autowired
    private DeveloppeurTestScoreRepository testScoreRepository;

    public DeveloppeurDashboardDTO getDashboardByDeveloppeurId(Long developpeurId) {

        // Vérifier si le développeur existe
        Developpeur developpeur = developpeurRepository.findById(developpeurId)
                .orElseThrow(() -> new RuntimeException("Développeur non trouvé"));

        // Récupérer tous les scores de test du développeur
        List<DeveloppeurTestScore> testScores = testScoreRepository.findByDeveloppeur_Id(developpeurId);

        int totalCorrectAnswers = 0;
        int totalQuestions = 0;
        List<TestStatDTO> testStats = new ArrayList<>();

        for (DeveloppeurTestScore score : testScores) {
            Test test = score.getTest();

            // Récupérer les réponses liées à ce score
            List<DeveloppeurResponse> responsesForScore = responseRepository.findByDeveloppeurTestScore(score);

            long correctAnswers = responsesForScore.stream().filter(r -> Boolean.TRUE.equals(r.getIsCorrect())).count();
            long total = responsesForScore.size();
            long incorrectAnswers = total - correctAnswers;
            double scoreTest = total > 0 ? (correctAnswers * 100.0 / total) : 0.0;

            totalCorrectAnswers += correctAnswers;
            totalQuestions += total;

            testStats.add(new TestStatDTO(
                    test.getId(),
                    test.getTitre(),
                    total,
                    correctAnswers,
                    incorrectAnswers,
                    scoreTest
            ));
        }

        double globalScore = totalQuestions > 0 ? (totalCorrectAnswers * 100.0 / totalQuestions) : 0.0;

        DeveloppeurDashboardDTO dashboard = new DeveloppeurDashboardDTO();
        dashboard.setNomDeveloppeur(developpeur.getUsername());
        dashboard.setChefEquipe(developpeur.getChefDeProjet() != null
                ? developpeur.getChefDeProjet().getUsername()
                : "Aucun chef");
        dashboard.setScoreGlobal(globalScore);
        dashboard.setTests(testStats);

        return dashboard;
    }
}
