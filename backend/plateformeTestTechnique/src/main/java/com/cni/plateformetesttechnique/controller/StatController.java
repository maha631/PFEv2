package com.cni.plateformetesttechnique.controller;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cni.plateformetesttechnique.model.ChefDeProjet;
import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.model.NiveauQuestion;
import com.cni.plateformetesttechnique.model.Test;
import com.cni.plateformetesttechnique.model.User;
import com.cni.plateformetesttechnique.repository.ChefDeProjetRepository;
import com.cni.plateformetesttechnique.repository.DeveloppeurRepository;
import com.cni.plateformetesttechnique.repository.QuestionRepository;
import com.cni.plateformetesttechnique.repository.UserRepository;
import com.cni.plateformetesttechnique.repository.TestRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
public class StatController {

    @Autowired
    private ChefDeProjetRepository chefDeProjetRepository;

    @Autowired
    private DeveloppeurRepository developpeurRepository;

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository UserRepository;
    @Autowired
    
    private  TestRepository TestRepository;

    
    @GetMapping("/developers-per-manager")
    public Map<String, Long> getDevelopersPerManager() {
        return chefDeProjetRepository.findAll()
            .stream()
            .collect(Collectors.toMap(
                chef -> chef.getUsername(),
                chef -> Optional.ofNullable(chef.getDeveloppeurs())
                                .map(List::size)
                                .map(Integer::longValue)
                                .orElse(0L)
            ));
    }

  
    @GetMapping("/team-stats")
    public List<TeamStatsDTO> getTeamStatistics() {
        return chefDeProjetRepository.findAll()
            .stream()
            .map(chef -> {
                TeamStatsDTO stats = new TeamStatsDTO();
                stats.setManagerName(chef.getUsername());

                List<Developpeur> devs = Optional.ofNullable(chef.getDeveloppeurs())
                                                 .orElse(Collections.emptyList());
                stats.setDeveloperCount(devs.size());

                // Calcul du score moyen sécurisé
                double avgScore = devs.stream()
                    .map(Developpeur::getScore)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
                stats.setAverageScore(avgScore);

              
                Map<String, Long> specialties = devs.stream()
                    .map(Developpeur::getSpecialite)
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(
                        specialite -> specialite,
                        Collectors.counting()
                    ));
                stats.setSpecialties(specialties);

                return stats;
            })
            .collect(Collectors.toList());
    }
    @GetMapping("/questions-difficulty")
    public Map<String, Long> getQuestionStatsByDifficulty() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("Facile", questionRepository.countByNiveau(NiveauQuestion.FACILE));
        stats.put("Moyen", questionRepository.countByNiveau(NiveauQuestion.MOYEN));
        stats.put("Difficile", questionRepository.countByNiveau(NiveauQuestion.DIFFICILE));
        return stats;
    }

    public static class TeamStatsDTO {
        private String managerName;
        private int developerCount;
        private double averageScore;
        private Map<String, Long> specialties;
        private long testCount;
        private double successRate;

        public long getTestCount() { return testCount; }
        public void setTestCount(long testCount) { this.testCount = testCount; }

        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }


        public String getManagerName() { return managerName; }
        public void setManagerName(String managerName) { this.managerName = managerName; }
        public int getDeveloperCount() { return developerCount; }
        public void setDeveloperCount(int developerCount) { this.developerCount = developerCount; }
        public double getAverageScore() { return averageScore; }
        public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
        public Map<String, Long> getSpecialties() { return specialties; }
        public void setSpecialties(Map<String, Long> specialties) { this.specialties = specialties; }
    }
    
    @GetMapping("/developers")
    public ResponseEntity<Long> getDevelopersCount() {
        long count = developpeurRepository.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/chefs-projet")
    public ResponseEntity<Long> getChefsProjetCount() {
        long count = chefDeProjetRepository.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/demandes-activation")
    public ResponseEntity<Long> getDemandesActivationCount() {
        long count = UserRepository.countByActiveFalse(); // ou actif == false
        return ResponseEntity.ok(count);
    }
    @GetMapping("/tests/publies/count")
    public ResponseEntity<Long> getNombreTestsPublies() {
        long count = TestRepository.countByStatut("publie");
        return ResponseEntity.ok(count);
    }
    
    @PreAuthorize("hasRole('ROLE_CHEF')")
    @GetMapping("/my-team")
    public ResponseEntity<TeamStatsDTO> getMyTeamStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // nom d'utilisateur du chef connecté

        Optional<ChefDeProjet> optionalChef = chefDeProjetRepository.findByUsername(username);

        if (optionalChef.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ChefDeProjet chef = optionalChef.get();

        TeamStatsDTO stats = new TeamStatsDTO();
        stats.setManagerName(chef.getUsername());

        List<Developpeur> devs = Optional.ofNullable(chef.getDeveloppeurs())
                                         .orElse(Collections.emptyList());
        stats.setDeveloperCount(devs.size());

        double avgScore = devs.stream()
            .map(Developpeur::getScore)
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
        stats.setAverageScore(avgScore);

        Map<String, Long> specialties = devs.stream()
            .map(Developpeur::getSpecialite)
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(
                specialite -> specialite,
                Collectors.counting()
            ));
        stats.setSpecialties(specialties);

        return ResponseEntity.ok(stats);
    }
   /* @PreAuthorize("hasRole('ROLE_CHEF')")
    @GetMapping("/count-tests")
    public ResponseEntity<Long> countTestsEquipe(Authentication authentication) {
        String username = authentication.getName();

        Optional<ChefDeProjet> optionalChef = chefDeProjetRepository.findByUsername(username);

        if (optionalChef.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ChefDeProjet chef = optionalChef.get();

        List<Developpeur> devs = Optional.ofNullable(chef.getDeveloppeurs())
                                         .orElse(Collections.emptyList());

        List<Long> devIds = devs.stream()
                                .map(Developpeur::getId)
                                .collect(Collectors.toList());

        long count = TestRepository.findAll().stream()
                .filter(test -> test.getDeveloppeur() != null && devIds.contains(test.getDeveloppeur().getId()))
                .count();

        return ResponseEntity.ok(count);
    }*/

    @PreAuthorize("hasRole('ROLE_CHEF')")
    @GetMapping("/count-developers-parchef")
    public ResponseEntity<Integer> countDeveloppeursEquipe(Authentication authentication) {
        String username = authentication.getName();

        Optional<ChefDeProjet> optionalChef = chefDeProjetRepository.findByUsername(username);

        if (optionalChef.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ChefDeProjet chef = optionalChef.get();
        int count = chef.getDeveloppeurs() != null ? chef.getDeveloppeurs().size() : 0;

        return ResponseEntity.ok(count);
    }





    @PreAuthorize("hasRole('ROLE_CHEF')")
    @GetMapping("/count-tests-publies")
    public ResponseEntity<Long> countTestsPubliesParChef(Authentication authentication) {
        // Récupérer l'ID de l'utilisateur connecté (chef de projet)
        String username = authentication.getName();

        // Récupérer l'utilisateur connecté via le service ou repository
        Optional<User> optionalUser = UserRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Récupérer l'ID de l'utilisateur (créateur)
        Long createurId = optionalUser.get().getId();

        // Trouver les tests créés par ce chef de projet
        List<Test> tests =  TestRepository.findByCreateurId(createurId);

        // Filtrer les tests avec statut "PUBLIE"
        long count = tests.stream()
                          .filter(test -> "PUBLIE".equals(test.getStatut()))  // Assure-toi que le statut est bien une String ou modifie cette partie si c'est un Enum
                          .count();

        return ResponseEntity.ok(count);
    }


    
    
    
    
}