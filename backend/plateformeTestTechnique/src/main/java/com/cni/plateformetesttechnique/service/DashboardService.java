package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.dto.DeveloppeurDashboardDTO;
import com.cni.plateformetesttechnique.dto.TestStatDTO;
import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.model.DeveloppeurResponse;
import com.cni.plateformetesttechnique.model.Test;
import com.cni.plateformetesttechnique.repository.DeveloppeurRepository;
import com.cni.plateformetesttechnique.repository.DeveloppeurResponseRepository;
import com.cni.plateformetesttechnique.repository.TestRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DashboardService {

    @Autowired
    private DeveloppeurRepository developpeurRepository;

    @Autowired
    private DeveloppeurResponseRepository responseRepository;

    @Autowired
    private TestRepository testRepository;

    public DeveloppeurDashboardDTO getDashboardByDeveloppeurId(Long developpeurId) {
        Developpeur dev = developpeurRepository.findById(developpeurId)
                .orElseThrow(() -> new RuntimeException("Développeur non trouvé"));

        List<DeveloppeurResponse> responses = responseRepository.findByDeveloppeurId(developpeurId);

        // Group responses by test
        Map<Test, List<DeveloppeurResponse>> responsesByTest = responses.stream()
                .collect(Collectors.groupingBy(DeveloppeurResponse::getTest));

        List<TestStatDTO> testStats = new ArrayList<>();
        int totalCorrectAnswers = 0;
        int totalQuestions = 0;

        for (Map.Entry<Test, List<DeveloppeurResponse>> entry : responsesByTest.entrySet()) {
            Test test = entry.getKey();
            List<DeveloppeurResponse> testResponses = entry.getValue();

            long correctAnswers = testResponses.stream().filter(r -> Boolean.TRUE.equals(r.getIsCorrect())).count();
            long total = testResponses.size();
            long incorrectAnswers = total - correctAnswers;
            double scoreTest = total > 0 ? (correctAnswers * 100.0 / total) : 0.0;

            testStats.add(new TestStatDTO(
                    test.getId(),
                    test.getTitre(),
                    total,
                    correctAnswers,
                    incorrectAnswers,
                    scoreTest
            ));

            totalCorrectAnswers += correctAnswers;
            totalQuestions += total;
        }

        double globalScore = totalQuestions > 0 ? (totalCorrectAnswers * 100.0 / totalQuestions) : 0.0;

        DeveloppeurDashboardDTO dashboard = new DeveloppeurDashboardDTO();
        dashboard.setNomDeveloppeur(dev.getUsername());
        dashboard.setChefEquipe(dev.getChefDeProjet() != null ? dev.getChefDeProjet().getUsername() : "Aucun chef");
        dashboard.setScoreGlobal(globalScore);
        dashboard.setTests(testStats);

        return dashboard;
    }
}
