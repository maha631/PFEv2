package com.cni.plateformetesttechnique.controller;

import com.cni.plateformetesttechnique.dto.CodeAnalysisRequest;
import com.cni.plateformetesttechnique.dto.ReponseDTO;
import com.cni.plateformetesttechnique.model.DeveloppeurResponse;
import com.cni.plateformetesttechnique.model.DeveloppeurTestScore;
import com.cni.plateformetesttechnique.repository.DeveloppeurResponseRepository;
import com.cni.plateformetesttechnique.repository.DeveloppeurTestScoreRepository;
import com.cni.plateformetesttechnique.security.services.UserDetailsImpl;
import com.cni.plateformetesttechnique.service.DeveloppeurResponseService;
import com.cni.plateformetesttechnique.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/responses")
public class DeveloppeurResponseController {

    @Autowired
    private DeveloppeurResponseService developpeurResponseService;
    @Autowired

    private DeveloppeurResponseRepository developpeurResponseRepository;

    @Autowired

    private DeveloppeurTestScoreRepository developpeurTestScoreRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/enregistrer")
    public ResponseEntity<Map<String, Object>> enregistrerReponse(@RequestBody ReponseDTO reponseDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long developpeurId = userDetails.getId(); // ID du développeur connecté
            Double score = developpeurResponseService.enregistrerReponse(
                    reponseDTO.getTestId(),
                    reponseDTO.getQuestionId(),
                    reponseDTO.getSelectedOptionIds(),
                    developpeurId,
                    reponseDTO.getReponseLibre()
            );
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Réponse enregistrée avec succès.");
            response.put("testId", reponseDTO.getTestId());
            response.put("questionId", reponseDTO.getQuestionId());
            response.put("developpeurId", developpeurId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }

    }
//    @GetMapping("/review/{testId}/{developerId}")
//    public List<DeveloppeurResponse> getReview(
//            @PathVariable Long testId,
//            @PathVariable Long developpeurId
//    ) {
//        return developpeurResponseRepository.findByTestIdAndDeveloppeurId(testId, developpeurId);
//    }

    // Enregistrer une réponse - accessible par DEVELOPPEUR uniquement
//    @PostMapping("/submit")
//    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')")
//    public ResponseEntity<Map<String, Object>> enregistrerReponse(@RequestBody ReponseDTO reponseDTO) {
//        try {
//            // Récupérer l'utilisateur connecté
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//            Long developpeurId = userDetails.getId(); // ID du développeur connecté
//
//            // Appeler le service avec l'ID du développeur connecté
//            developpeurResponseService.enregistrerReponse(
//                    reponseDTO.getTestId(),
//                    reponseDTO.getQuestionId(),
//                    reponseDTO.getSelectedOptionIds(),
//                    developpeurId
//            );
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Réponse enregistrée avec succès.");
//            response.put("testId", reponseDTO.getTestId());
//            response.put("questionId", reponseDTO.getQuestionId());
//            response.put("developpeurId", developpeurId);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", e.getMessage());
//
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//    @GetMapping("/mes-reponses/{testId}")
//    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')")
//    public ResponseEntity<List<DeveloppeurResponse>> getMesReponses(@PathVariable Long testId) {
//        // Récupérer l'utilisateur connecté
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        Long developpeurId = userDetails.getId(); // ID du développeur connecté
//
//        // Récupérer les réponses de CE développeur pour CE test
//        List<DeveloppeurResponse> mesReponses = developpeurResponseRepository
//                .findByDeveloppeurTestScore_Developpeur_IdAndDeveloppeurTestScore_Test_Id(developpeurId, testId);
//
//        return ResponseEntity.ok(mesReponses);
//    }
//    @GetMapping("/mes-reponses/{testId}/{developpeurId}")
//    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR') or hasRole('ADMIN') or hasRole('CHEF')")
//    public ResponseEntity<List<DeveloppeurResponse>> getMesReponses(
//            @PathVariable Long testId,
//            @PathVariable Long developpeurId,
//            @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        try {
//            Long connectedDeveloperId = userDetails.getId();
//
//            // Vérifier si l'utilisateur est ADMIN ou CHEF
//            boolean isAdminOrChef = userDetails.getAuthorities().stream()
//                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_CHEF"));
//
//            // Si l'utilisateur n'est pas admin/chef et tente d'accéder à un autre développeur → interdit
//            if (!isAdminOrChef && !connectedDeveloperId.equals(developpeurId)) {
//                return ResponseEntity.status(403).body(Collections.emptyList());
//            }
//
//            // Récupérer la dernière tentative du développeur pour ce test
//            Optional<DeveloppeurTestScore> lastAttemptOpt = developpeurTestScoreRepository
//                    .findTopByDeveloppeur_IdAndTest_IdOrderByAttemptNumberDesc(developpeurId, testId);
//
//            if (lastAttemptOpt.isEmpty()) {
//                return ResponseEntity.ok(Collections.emptyList());
//            }
//
//            DeveloppeurTestScore lastAttempt = lastAttemptOpt.get();
//
//            // Récupérer les réponses liées à cette tentative
//            List<DeveloppeurResponse> mesReponses = developpeurResponseRepository
//                    .findByDeveloppeurTestScore_Id(lastAttempt.getId());
//
//            return ResponseEntity.ok(mesReponses);
//
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(Collections.emptyList());
//        }
//    }

    @GetMapping("/mes-reponses/{testId}")
    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')or hasRole('ADMIN') or hasRole('CHEF')")
    public ResponseEntity<List<DeveloppeurResponse>> getMesReponses(@PathVariable Long testId) {
        // 🔐 Récupérer l'utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long developpeurId = userDetails.getId(); // ID du développeur connecté

        // 🔄 Récupérer la dernière tentative du développeur pour ce test
        Optional<DeveloppeurTestScore> lastAttemptOpt = developpeurTestScoreRepository
                .findTopByDeveloppeur_IdAndTest_IdOrderByAttemptNumberDesc(developpeurId, testId);

        if (lastAttemptOpt.isEmpty()) {
            // 🚫 Aucun score trouvé, donc aucune tentative
            return ResponseEntity.ok(Collections.emptyList());
        }

        DeveloppeurTestScore lastAttempt = lastAttemptOpt.get();

        // ✅ Récupérer les réponses liées à CETTE tentative (et pas toutes les tentatives)
        List<DeveloppeurResponse> mesReponses = developpeurResponseRepository
                .findByDeveloppeurTestScore_Id(lastAttempt.getId());

        return ResponseEntity.ok(mesReponses);
    }
////
@GetMapping("/dev-reponses/{testId}/{developpeurId}")
@PreAuthorize("hasRole('ROLE_DEVELOPPEUR')or hasRole('ADMIN') or hasRole('CHEF')")
public ResponseEntity<List<DeveloppeurResponse>> getdevReponses(@PathVariable Long testId,@PathVariable Long developpeurId) {

    // 🔄 Récupérer la dernière tentative du développeur pour ce test
    Optional<DeveloppeurTestScore> lastAttemptOpt = developpeurTestScoreRepository
            .findTopByDeveloppeur_IdAndTest_IdOrderByAttemptNumberDesc(developpeurId, testId);

    if (lastAttemptOpt.isEmpty()) {
        // 🚫 Aucun score trouvé, donc aucune tentative
        return ResponseEntity.ok(Collections.emptyList());
    }

    DeveloppeurTestScore lastAttempt = lastAttemptOpt.get();

    // ✅ Récupérer les réponses liées à CETTE tentative (et pas toutes les tentatives)
    List<DeveloppeurResponse> mesReponses = developpeurResponseRepository
            .findByDeveloppeurTestScore_Id(lastAttempt.getId());

    return ResponseEntity.ok(mesReponses);
}
    ///
    @DeleteMapping("/supprimer/{developpeurTestScoreId}")
    public ResponseEntity<String> supprimerReponses(@PathVariable Long developpeurTestScoreId) {
        try {
            developpeurResponseService.supprimerReponsesParTentative(developpeurTestScoreId);
            return ResponseEntity.ok("Réponses supprimées avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }
}
    // Méthode pour extraire le token JWT de l'en-tête Authorization

//package com.cni.plateformetesttechnique.controller;
//
//import com.cni.plateformetesttechnique.dto.ReponseDTO;
//import com.cni.plateformetesttechnique.service.DeveloppeurResponseService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/responses")
//public class DeveloppeurResponseController {
//
//    @Autowired
//    private DeveloppeurResponseService developpeurResponseService;
//
//    @PostMapping("/submit")
//    public ResponseEntity<Map<String, Object>> enregistrerReponse(@RequestBody ReponseDTO reponseDTO) {
//        try {
//            developpeurResponseService.enregistrerReponse(
//                    reponseDTO.getTestId(),
//                    reponseDTO.getQuestionId(),
//                    reponseDTO.getSelectedOptionIds(),
//                    reponseDTO.getDeveloppeurId()
//            );
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Réponse enregistrée avec succès.");
//            response.put("testId", reponseDTO.getTestId());
//            response.put("questionId", reponseDTO.getQuestionId());
//            response.put("developpeurId", reponseDTO.getDeveloppeurId());
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", e.getMessage());
//
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }

//    @DeleteMapping("/supprimer/{testId}/{developpeurId}")
//    public ResponseEntity<String> supprimerReponses(@PathVariable Long testId, @PathVariable Long developpeurId) {
//        try {
//            developpeurResponseService.supprimerReponses(testId, developpeurId);
//            return ResponseEntity.ok("Réponses supprimées avec succès.");
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
//        }
//    }
//}
//package com.cni.plateformetesttechnique.controller;
//
//import com.cni.plateformetesttechnique.dto.ReponseDTO;
//import com.cni.plateformetesttechnique.model.User;
//import com.cni.plateformetesttechnique.service.DeveloppeurResponseService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.security.core.Authentication;
//
//import org.springframework.security.core.context.SecurityContextHolder;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/responses")
//public class DeveloppeurResponseController {
//
//    @Autowired
//    private DeveloppeurResponseService developpeurResponseService;
//
//    // Enregistrer une réponse - accessible par DEVELOPPEUR uniquement
//    @PostMapping("/submit")
//    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')") public ResponseEntity<Map<String, Object>> enregistrerReponse(@RequestBody ReponseDTO reponseDTO) {
//        try {
//            // Récupérer l'ID du développeur connecté
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            User user = (User) authentication.getPrincipal();
//
//            // Si l'ID est stocké dans les authorities ou dans un attribut personnalisé, adapte cela
//            Long developpeurId = Long.parseLong(user.getUsername());  // Si tu stockes l'ID dans le username
//
//            developpeurResponseService.enregistrerReponse(
//                    reponseDTO.getTestId(),
//                    reponseDTO.getQuestionId(),
//                    reponseDTO.getSelectedOptionIds(),
//                    developpeurId
//            );
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Réponse enregistrée avec succès.");
//            response.put("testId", reponseDTO.getTestId());
//            response.put("questionId", reponseDTO.getQuestionId());
//            response.put("developpeurId", developpeurId);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", e.getMessage());
//
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
////    public ResponseEntity<Map<String, Object>> enregistrerReponse(@RequestBody ReponseDTO reponseDTO) {
////        try {
////            developpeurResponseService.enregistrerReponse(
////                    reponseDTO.getTestId(),
////                    reponseDTO.getQuestionId(),
////                    reponseDTO.getSelectedOptionIds(),
////                    reponseDTO.getDeveloppeurId()
////            );
////
////            Map<String, Object> response = new HashMap<>();
////            response.put("status", "success");
////            response.put("message", "Réponse enregistrée avec succès.");
////            response.put("testId", reponseDTO.getTestId());
////            response.put("questionId", reponseDTO.getQuestionId());
////            response.put("developpeurId", reponseDTO.getDeveloppeurId());
////
////            return ResponseEntity.ok(response);
////
////        } catch (Exception e) {
////            Map<String, Object> errorResponse = new HashMap<>();
////            errorResponse.put("status", "error");
////            errorResponse.put("message", e.getMessage());
////
////            return ResponseEntity.badRequest().body(errorResponse);
////        }
////    }
//
//    // Supprimer les réponses - accessible par ADMIN et ChefProjet uniquement
//    @DeleteMapping("/supprimer/{testId}/{developpeurId}")
////    @PreAuthorize("hasRole('ADMIN') or hasRole('ChefProjet')")
//    public ResponseEntity<String> supprimerReponses(@PathVariable Long testId, @PathVariable Long developpeurId) {
//        try {
//            developpeurResponseService.supprimerReponses(testId, developpeurId);
//            return ResponseEntity.ok("Réponses supprimées avec succès.");
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
//        }
//    }
//}
