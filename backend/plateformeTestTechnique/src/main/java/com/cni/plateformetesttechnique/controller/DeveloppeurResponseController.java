package com.cni.plateformetesttechnique.controller;

import com.cni.plateformetesttechnique.dto.ReponseDTO;
import com.cni.plateformetesttechnique.service.DeveloppeurResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/responses")
public class DeveloppeurResponseController {

    @Autowired
    private DeveloppeurResponseService developpeurResponseService;

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
            developpeurResponseService.supprimerReponses(testId, developpeurId);
            return ResponseEntity.ok("Réponses supprimées avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }
}
