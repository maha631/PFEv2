package com.cni.plateformetesttechnique.controller;
import com.cni.plateformetesttechnique.dto.ReponseDTO;
import com.cni.plateformetesttechnique.service.DeveloppeurResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/responses")
public class DeveloppeurResponseController {

    @Autowired
    private DeveloppeurResponseService developpeurResponseService;


//    public ResponseEntity<Map<String, Object>> enregistrerReponse(
//            @RequestParam Long testId,
//            @RequestParam Long questionId,
//            @RequestBody List<Long> selectedOptionIds,
//            @RequestParam Long developpeurId) {
//
//        try {
//            developpeurResponseService.enregistrerReponse(testId, questionId, selectedOptionIds, developpeurId);
//
//            // Construire une réponse JSON
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Réponse enregistrée avec succès.");
//            response.put("testId", testId);
//            response.put("questionId", questionId);
//            response.put("developpeurId", developpeurId);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            // En cas d'erreur, retourner un JSON avec le message d'erreur
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", e.getMessage());
//
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> enregistrerReponse(@RequestBody ReponseDTO reponseDTO) {
        try {
            developpeurResponseService.enregistrerReponse(
                    reponseDTO.getTestId(),
                    reponseDTO.getQuestionId(),
                    reponseDTO.getSelectedOptionIds(),
                    reponseDTO.getDeveloppeurId()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Réponse enregistrée avec succès.");
            response.put("testId", reponseDTO.getTestId());
            response.put("questionId", reponseDTO.getQuestionId());
            response.put("developpeurId", reponseDTO.getDeveloppeurId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/supprimer/{testId}/{developpeurId}")
    public ResponseEntity<String> supprimerReponses(@PathVariable Long testId, @PathVariable Long developpeurId) {
        try {
            // Appel à la méthode de service pour supprimer les réponses
            developpeurResponseService.supprimerReponses(testId, developpeurId);
            return ResponseEntity.ok("Réponses supprimées avec succès.");
        } catch (RuntimeException e) {
            // Si une exception est levée, retourner une réponse d'erreur
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }


}
