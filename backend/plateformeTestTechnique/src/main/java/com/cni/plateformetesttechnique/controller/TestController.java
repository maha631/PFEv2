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

import com.cni.plateformetesttechnique.model.Test;
import com.cni.plateformetesttechnique.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tests")
public class TestController {

    @Autowired
    private TestService testService;

    // Récupérer tous les tests - accessible à tous
    @GetMapping
    public List<Test> getAllTests() {
        return testService.getAllTests();
    }

    // Créer un test - accessible par ADMIN et ChefProjet uniquement
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ChefProjet')")
    public ResponseEntity<Test> createTest(@RequestBody Test test) {
        Test createdTest = testService.createTest(test);
        return ResponseEntity.ok(createdTest);
    }

    // Mettre à jour un test - accessible par ADMIN et ChefProjet uniquement
    @PutMapping("/{testId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ChefProjet')")
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
    public ResponseEntity<Test> getTestDetails(@PathVariable Long testId) {
        Test testDetails = testService.getTestDetails(testId);
        return ResponseEntity.ok(testDetails);
    }

    // Publier un test - accessible par ADMIN et ChefProjet uniquement
    @PutMapping("/{testId}/publish")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ChefProjet')")
    public ResponseEntity<Test> publishTest(@PathVariable Long testId, @RequestParam Boolean accesRestreint) {
        Test publishedTest = testService.publishTest(testId, accesRestreint);
        return ResponseEntity.ok(publishedTest);
    }

    // Vérifier si un test est complété - accessible à tous
    @GetMapping("/isCompleted")
    public boolean isTestCompleted(
            @RequestParam Long testId,
            @RequestParam Long developpeurId) {
        return testService.isTestCompleted(testId, developpeurId);
    }
}
