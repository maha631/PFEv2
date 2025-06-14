//package com.cni.plateformetesttechnique.controller;
//
//import com.cni.plateformetesttechnique.model.Test;
//import com.cni.plateformetesttechnique.service.TestService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/tests")
//public class TestController {
//
//    @Autowired
//    private TestService testService;
//
//    @GetMapping
//    public List<Test> getAllTests() {
//        return testService.getAllTests();
//    }
//
//    @PostMapping("/create")
//    public ResponseEntity<Test> createTest(@RequestBody Test test) {
//        Test createdTest = testService.createTest(test);
//        return ResponseEntity.ok(createdTest);
//    }
//
//    @PutMapping("/{testId}")
//    public ResponseEntity<Test> updateTest(@PathVariable Long testId, @RequestBody Test updatedTest) {
//        Test updated = testService.updateTest(testId, updatedTest);
//        return ResponseEntity.ok(updated);
//    }
//
//    @GetMapping("/public")
//    public List<Test> getAvailablePublicTests() {
//        return testService.getAvailablePublicTests();
//    }
//
//    @GetMapping("/{testId}/details")
//    public ResponseEntity<Test> getTestDetails(@PathVariable Long testId) {
//        Test testDetails = testService.getTestDetails(testId);
//        return ResponseEntity.ok(testDetails);
//    }
//
//    @PutMapping("/{testId}/publish")
//    public ResponseEntity<Test> publishTest(@PathVariable Long testId, @RequestParam Boolean accesRestreint) {
//        Test publishedTest = testService.publishTest(testId, accesRestreint);
//        return ResponseEntity.ok(publishedTest);
//    }
//
//    @GetMapping("/isCompleted")
//    public boolean isTestCompleted(
//            @RequestParam Long testId,
//            @RequestParam Long developpeurId) {
//        return testService.isTestCompleted(testId, developpeurId);
//    }
//}
package com.cni.plateformetesttechnique.controller;

import com.cni.plateformetesttechnique.dto.PublishTestRequest;
import com.cni.plateformetesttechnique.dto.TestGenerationRequest;
import com.cni.plateformetesttechnique.model.Question;
import com.cni.plateformetesttechnique.model.Test;
import com.cni.plateformetesttechnique.security.services.UserDetailsImpl;
import com.cni.plateformetesttechnique.service.TestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tests")
public class TestController {

    @Autowired
    private TestService testService;

//    @GetMapping("/suggestions")
//    public ResponseEntity<List<Test>> getSuggestionsPourDev(
//            @RequestParam String email,
//            @RequestParam String technologie,
//            @RequestParam String niveau) {
//        List<Test> suggestions = testService.getTestsSuggeresPourDeveloppeur(email, technologie, niveau);
//        return ResponseEntity.ok(suggestions);
//    }

    @GetMapping("/count/niveau/{niveau}")
    public long getTestCountByNiveau(@PathVariable(name="niveau") String niveau) {
        return testService.getTestCountByNiveau(niveau);
    }
@PostMapping("/suggestions")
public ResponseEntity<List<Test>> getTestsSuggeres(@RequestBody Map<String, Object> payload) {
    try {
        String emailDev = (String) payload.get("emailDev");
        String technologie = (String) payload.get("technologie");
        String niveauDifficulte = (String) payload.get("niveauDifficulte");
        Boolean isNext = (Boolean) payload.get("isNext");

        if (emailDev == null || technologie == null || niveauDifficulte == null || isNext == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Test> testsSuggeres = testService.getTestsSuggeresPourDeveloppeur(
                emailDev,
                technologie,
                niveauDifficulte,
                isNext
        );
        return ResponseEntity.ok(testsSuggeres);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(null);
    }
}
    @DeleteMapping("/{testId}")
    public ResponseEntity<Map<String, String>> deleteTest(@PathVariable Long testId) {
        boolean deleted = testService.deleteTest(testId);
        Map<String, String> response = new HashMap<>();

        if (deleted) {
            response.put("message", "Test supprimé avec succès");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Le test n'existe pas ou n'est pas en statut Brouillon");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Récupérer tous les tests - accessible à tous
    @GetMapping
    public List<Test> getAllTests() {
        return testService.getAllTests();
    }
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<Test>> getTestsByUser(@PathVariable Long userId) {
        List<Test> tests = testService.getTestsPubliesByUserId(userId);
        return ResponseEntity.ok(tests);
    }
    @GetMapping("/publies/admin")
    public ResponseEntity<List<Test>> getTestsPubliesDeAdmin() {
        return ResponseEntity.ok(testService.getTestsPubliesDeAdmin());
    }
    @GetMapping("/publies/du-chef")
    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')")

    public ResponseEntity<List<Test>> getTestsDuChef() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String emailDev = userDetails.getEmail(); // ou getEmail() selon ton UserDetails
        List<Test> tests = testService.getTestsPubliesDuChefDuDev(emailDev);
        return ResponseEntity.ok(tests);
    }

    @PostMapping("/generate")
    public Test generateTest(@RequestBody  TestGenerationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        return testService.createTestFromRequest(request, userId);
    }
    @PostMapping("/questions")
    public ResponseEntity<?> generateQs(@Valid @RequestBody TestGenerationRequest request) {
        try {
            List<Question> questions = testService.getQuestionsForAutoGeneration(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Questions générées avec succès");
            response.put("totalQuestions", questions.size());
            response.put("questions", questions);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Créer un test - accessible par ADMIN et ChefProjet uniquement
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_CHEF')")
    public ResponseEntity<Test> createTest(@RequestBody Test test) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId(); // ID de l'admin ou chef de projet connecté


            // Initialisation de la date de création si non définie
            if (test.getDateCreation() == null) {
                test.setDateCreation(LocalDateTime.now());
            }
            // Sauvegarde du test
            Test createdTest = testService.createTest(test,userId);
            return new ResponseEntity<>(createdTest, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();  // Utilisez des logs dans un vrai projet
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/ForCurrentUser")
    public List<Test> getTestsForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return testService.getTestsForCurrentUser(userDetails);
    }

    // Mettre à jour un test - accessible par ADMIN et ChefProjet uniquement
    @PutMapping("/{testId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_CHEF')")
    public ResponseEntity<Test> updateTest(@PathVariable Long testId, @RequestBody Test updatedTest) {
        Test updated = testService.updateTest(testId, updatedTest);
        return ResponseEntity.ok(updated);
    }

    // Récupérer les tests publics disponibles - accessible à tous
    @GetMapping("/public")
    public List<Test> getAvailablePublicTests() {
        return testService.getAvailablePublicTests();
    }

    // Récupérer les détails d'un test - accessible à tous
    @GetMapping("/{testId}/details")
    public ResponseEntity<Test> getTestDetails(@PathVariable(name="testId") Long testId) {
        Test testDetails = testService.getTestDetails(testId);
        return ResponseEntity.ok(testDetails);
    }


    @PutMapping("/{testId}/publish")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_CHEF')")
    public ResponseEntity<Test> publishTest(
            @PathVariable("testId") Long testId,
            @RequestBody PublishTestRequest request // DTO avec accesRestreint et developerIds
    ) {
        Test publishedTest = testService.publishTest(testId, request.getAccesRestreint(), request.getDeveloperIds());
        return ResponseEntity.ok(publishedTest);
    }

    // Vérifier si un test est complété - accessible à tous
//    @GetMapping("/isCompleted")
//    public boolean isTestCompleted(
//            @RequestParam Long testId,
//            @RequestParam Long developpeurId) {
//        return testService.isTestCompleted(testId, developpeurId);
//    }
}
