/*package com.cni.plateformetesttechnique.controller;


import com.cni.plateformetesttechnique.dto.DeveloppeurResultResponse;
import com.cni.plateformetesttechnique.dto.TestStatsResponse;
import com.cni.plateformetesttechnique.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.cni.plateformetesttechnique.security.services.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/{testId}/{developpeurId}")

    @PreAuthorize("hasRole('DEVELOPPEUR') or hasRole('ADMIN') or hasRole('ROLE_CHEF')")

//appeler par le front pour recuperer le score pour le test deja passé

    public ResponseEntity<Map<String, Object>> getScoreByDeveolperAndTestId(
            @PathVariable Long testId,
            @PathVariable(required = false) Long developpeurId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            Long connectedDeveloperId = userDetails.getId();  // ID du développeur connecté

            // Vérification des rôles de l'utilisateur
            boolean isAdminOrChef = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_CHEF"));

            if (isAdminOrChef) {
                // Si l'utilisateur est admin ou chef, il peut consulter le score de n'importe quel développeur
                Double score = scoreService.getScoreByDeveloppeurAndTest(developpeurId, testId);
                Map<String, Object> response = new HashMap<>();
                if (score != null) {
                    response.put("status", "success");
                    response.put("score", score);
                } else {
                    response.put("status", "error");
                    response.put("message", "Aucun score trouvé pour ce développeur et ce test.");
                }
                return ResponseEntity.ok(response);
            } else {
                // Si l'utilisateur est un développeur, il ne peut voir que son propre score pour ce test
                if (connectedDeveloperId.equals(developpeurId)) {
                    Double score = scoreService.getScoreByDeveloppeurAndTest(developpeurId, testId);
                    Map<String, Object> response = new HashMap<>();
                    if (score != null) {
                        response.put("status", "success");
                        response.put("score", score);
                    } else {
                        response.put("status", "error");
                        response.put("message", "Aucun score trouvé pour ce test.");
                    }
                    return ResponseEntity.ok(response);
                } else {
                    // Si un développeur tente de consulter le score d'un autre développeur, erreur
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("status", "error");
                    errorResponse.put("message", "Vous ne pouvez pas voir le score d'un autre développeur.");
                    return ResponseEntity.status(403).body(errorResponse); // Forbidden
                }
            }

        } catch (Exception e) {
            // Gestion des erreurs
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
//    public ResponseEntity<Map<String, Object>> getScoreByDeveolperAndTestId(
//            @PathVariable Long testId,
//            @PathVariable Long developpeurId ) {
//
//        try {
//            // Récupérer le score pour ce test et ce développeur
//            Double score = scoreService.getScoreByDeveloppeurAndTest(developpeurId, testId);
//
//            // Construire la réponse
//            Map<String, Object> response = new HashMap<>();
//            if (score != null) {
//                response.put("status", "success");
//                response.put("score", score);
//            } else {
//                response.put("status", "error");
//                response.put("message", "Aucun score trouvé pour ce développeur et ce test.");
//            }
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            // Gestion des erreurs
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//    @GetMapping("/obtenir/{developpeurId}")
//    @PreAuthorize("hasRole('DEVELOPPEUR') or hasRole('ADMIN') or hasRole('CHEF')")
//    public ResponseEntity<Map<String, Object>> obtenirScore(
//            @PathVariable Long developpeurId) {
//        try {
//            // Obtenir le score global d'un développeur
//            Double score = scoreService.getGlobalScore(developpeurId);
//
//            // Retourner la réponse avec le score
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("score", score);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", e.getMessage());
//
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
@PreAuthorize("hasRole('DEVELOPPEUR') or hasRole('ADMIN') or hasRole('ROLE_CHEF')")
@GetMapping("/obtenir/developpeur")
public ResponseEntity<Map<String, Object>> obtenirScore(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(name = "developpeurId", required = false) Long developpeurId){
    try {
        Long connectedDeveloperId = userDetails.getId();  // ID du développeur connecté

        // Vérification des rôles de l'utilisateur
        boolean isAdminOrChef = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_CHEF"));

        // Si l'utilisateur est admin ou chef, il peut voir les scores d'un développeur spécifique
        if (isAdminOrChef) {
            if (developpeurId != null) {
                // Récupérer le score pour un développeur spécifique
                Double score = scoreService.getGlobalScore(developpeurId);
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("score", score);
                return ResponseEntity.ok(response);
            } else {
                // Si aucun developpeurId n'est spécifié, retourner une erreur
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "L'ID du développeur doit être spécifié.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } else {
            if (connectedDeveloperId.equals(developpeurId)) {

                // Si c'est un développeur, il ne peut voir que son propre score
                Double score = scoreService.getGlobalScore(connectedDeveloperId);
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("score", score);
                return ResponseEntity.ok(response);
            } else {
                // Si un développeur tente de consulter le score d'un autre développeur, erreur
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Vous ne pouvez pas voir le score d'un autre développeur.");
                return ResponseEntity.status(403).body(errorResponse); // Forbidden
            }
        }
    } catch (Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", e.getMessage());

        return ResponseEntity.badRequest().body(errorResponse);
    }
}

    @GetMapping("/calculer/{testId}")
    @PreAuthorize("hasRole('DEVELOPPEUR') or hasRole('ADMIN') or hasRole('ROLE_CHEF')")
    public ResponseEntity<Map<String, Object>> calculerScore(
            @PathVariable Long testId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            Long developpeurId = userDetails.getId(); // Récupérer l'ID du développeur connecté

            // Calculer le score
            Double score = scoreService.calculerScore(testId, developpeurId);

            // Construire la réponse JSON
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("score", score);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // En cas d'erreur, retourner un message d'erreur
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    
    @GetMapping("/chef/{chefDeProjet_id}")
    @PreAuthorize("hasRole('ROLE_CHEF') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getScoreChef(@PathVariable(name = "chefDeProjet_id") Long chefDeProjet_id) {
        try {
            //Double score = scoreService.calculerScoreChef(chefDeProjet_id);
            Double score = scoreService.updateGlobalScore(chefDeProjet_id);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("score", score);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } 
    }
    

    
    @PutMapping("/modifier/score")
    public ResponseEntity<?> modifierScore(
            @RequestParam("developpeurId") Long developpeurId,
            @RequestParam("nouveauScore") Double nouveauScore) {
        try {
        	scoreService.mettreAJourScoreDeveloppeur(developpeurId, nouveauScore);
            return ResponseEntity.ok("Score mis à jour !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur");
        }
    }


    @GetMapping("/stats/{testId}")
    public TestStatsResponse getStats(@PathVariable Long testId) {
        return scoreService.getTestStats(testId);
    }
    @GetMapping("/test/{testId}")
    public ResponseEntity<List<DeveloppeurResultResponse>> getResultatsParTest(@PathVariable Long testId) {
        List<DeveloppeurResultResponse> resultats = scoreService.getResultatsParTest(testId);
        return ResponseEntity.ok(resultats);
    }
    

    
    
    
  

}*/
package com.cni.plateformetesttechnique.controller;


import com.cni.plateformetesttechnique.dto.DeveloppeurResultResponse;
import com.cni.plateformetesttechnique.dto.TestStatsResponse;
import com.cni.plateformetesttechnique.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.cni.plateformetesttechnique.security.services.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/{testId}/{developpeurId}")
    @PreAuthorize("hasRole('DEVELOPPEUR') or hasRole('ADMIN') or hasRole('CHEF')")
//appeler par le front pour recuperer le score pour le test deja passé
    public ResponseEntity<Map<String, Object>> getScoreByDeveolperAndTestId(
            @PathVariable Long testId,
            @PathVariable(required = false) Long developpeurId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            Long connectedDeveloperId = userDetails.getId();  // ID du développeur connecté

            // Vérification des rôles de l'utilisateur
            boolean isAdminOrChef = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_CHEF"));

            if (isAdminOrChef) {
                // Si l'utilisateur est admin ou chef, il peut consulter le score de n'importe quel développeur
                Double score = scoreService.getScoreByDeveloppeurAndTest(developpeurId, testId);
                Map<String, Object> response = new HashMap<>();
                if (score != null) {
                    response.put("status", "success");
                    response.put("score", score);
                } else {
                    response.put("status", "error");
                    response.put("message", "Aucun score trouvé pour ce développeur et ce test.");
                }
                return ResponseEntity.ok(response);
            } else {
                // Si l'utilisateur est un développeur, il ne peut voir que son propre score pour ce test
                if (connectedDeveloperId.equals(developpeurId)) {
                    Double score = scoreService.getScoreByDeveloppeurAndTest(developpeurId, testId);
                    Map<String, Object> response = new HashMap<>();
                    if (score != null) {
                        response.put("status", "success");
                        response.put("score", score);
                    } else {
                        response.put("status", "error");
                        response.put("message", "Aucun score trouvé pour ce test.");
                    }
                    return ResponseEntity.ok(response);
                } else {
                    // Si un développeur tente de consulter le score d'un autre développeur, erreur
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("status", "error");
                    errorResponse.put("message", "Vous ne pouvez pas voir le score d'un autre développeur.");
                    return ResponseEntity.status(403).body(errorResponse); // Forbidden
                }
            }

        } catch (Exception e) {
            // Gestion des erreurs
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

@PreAuthorize("hasRole('DEVELOPPEUR') or hasRole('ADMIN') or hasRole('CHEF')")

@GetMapping("/obtenir/developpeur")
public ResponseEntity<Map<String, Object>> obtenirScore(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(required = false) Long developpeurId) {
    try {
        Long connectedDeveloperId = userDetails.getId();  // ID du développeur connecté

        // Vérification des rôles de l'utilisateur
        boolean isAdminOrChef = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_CHEF"));

        // Si l'utilisateur est admin ou chef, il peut voir les scores d'un développeur spécifique
        if (isAdminOrChef) {
            if (developpeurId != null) {
                // Récupérer le score pour un développeur spécifique
                Double score = scoreService.getGlobalScore(developpeurId);
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("score", score);
                return ResponseEntity.ok(response);
            } else {
                // Si aucun developpeurId n'est spécifié, retourner une erreur
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "L'ID du développeur doit être spécifié.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } else {
            if (connectedDeveloperId.equals(developpeurId)) {

                // Si c'est un développeur, il ne peut voir que son propre score
                Double score = scoreService.getGlobalScore(connectedDeveloperId);
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("score", score);
                return ResponseEntity.ok(response);
            } else {
                // Si un développeur tente de consulter le score d'un autre développeur, erreur
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Vous ne pouvez pas voir le score d'un autre développeur.");
                return ResponseEntity.status(403).body(errorResponse); // Forbidden
            }
        }
    } catch (Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", e.getMessage());

        return ResponseEntity.badRequest().body(errorResponse);
    }
}

    @GetMapping("/calculer/{testId}")
    @PreAuthorize("hasRole('DEVELOPPEUR') or hasRole('ADMIN') or hasRole('CHEF')")
    public ResponseEntity<Map<String, Object>> calculerScore(
            @PathVariable Long testId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            Long developpeurId = userDetails.getId(); // Récupérer l'ID du développeur connecté

            // Calculer le score
            Double score = scoreService.calculerScore(testId, developpeurId);

            // Construire la réponse JSON
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("score", score);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // En cas d'erreur, retourner un message d'erreur
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    
    @GetMapping("/chef/{chefDeProjet_id}")
    @PreAuthorize("hasRole('CHEF') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getScoreChef(@PathVariable(name = "chefDeProjet_id") Long chefDeProjet_id) {
        try {
            Double score = scoreService.calculerScoreChef(chefDeProjet_id);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("score", score);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/stats/{testId}")
    public TestStatsResponse getStats(@PathVariable Long testId) {
        return scoreService.getTestStats(testId);
    }
    @GetMapping("/test/{testId}")
    public ResponseEntity<List<DeveloppeurResultResponse>> getResultatsParTest(@PathVariable Long testId) {
        List<DeveloppeurResultResponse> resultats = scoreService.getResultatsParTest(testId);
        return ResponseEntity.ok(resultats);
    }
    
    
    
    

}