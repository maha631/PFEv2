//package com.cni.plateformetesttechnique.controller;
//
//import com.cni.plateformetesttechnique.model.Question;
//import com.cni.plateformetesttechnique.model.Test;
//import com.cni.plateformetesttechnique.model.TestQuestion;
//import com.cni.plateformetesttechnique.service.TestService;
//import com.cni.plateformetesttechnique.service.DeveloppeurResponseService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
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
//    // 2️⃣ Modifier un test (seulement en brouillon)
//    @PutMapping("/{testId}")
//    public ResponseEntity<Test> updateTest(@PathVariable Long testId, @RequestBody Test updatedTest) {
//        Test updated = testService.updateTest(testId, updatedTest);
//        return ResponseEntity.ok(updated);
//    }
//<<<<<<< HEAD
//    @GetMapping("/public")
//    public List<Test> getAvailablePublicTests() {
//        return testService.getAvailablePublicTests();
//    }
//
//=======
//    @PostMapping("/{testId}/questions")
////    public ResponseEntity<List<TestQuestion>> addQuestionsToTest(@PathVariable Long testId, @RequestBody List<TestQuestion> testQuestions) {
////        List<TestQuestion> addedQuestions = testService.addQuestionsToTest(testId, testQuestions);
////        return ResponseEntity.ok(addedQuestions);
////    }
////    @GetMapping("/{testId}/questions")
////    public ResponseEntity<List<Question>> getQuestionsForTest(@PathVariable Long testId) {
////        List<Question> questions = testService.getQuestionsForTest(testId);
////        return ResponseEntity.ok(questions);
////    }
//>>>>>>> 4c235a5a9568c22657f6c3895b04b17802edff1f
//    @GetMapping("/{testId}/details")
//    public ResponseEntity<Test> getTestDetails(@PathVariable Long testId) {
//        Test testDetails = testService.getTestDetails(testId);
//        return ResponseEntity.ok(testDetails);
//    }
//    @PutMapping("/{testId}/publish")
//    public ResponseEntity<Test> publishTest(@PathVariable Long testId, @RequestParam Boolean accesRestreint) {
//        Test publishedTest = testService.publishTest(testId, accesRestreint);
//        return ResponseEntity.ok(publishedTest);
//    }
//<<<<<<< HEAD
//
//=======
////    @PostMapping("/{testId}/invite")
////    public ResponseEntity<String> inviteDevelopers(@PathVariable Long testId, @RequestBody List<Long> developerIds) {
////        testService.inviteDevelopers(testId, developerIds);
////        return ResponseEntity.ok("Invitations envoyées avec succès");
////    }
//>>>>>>> 4c235a5a9568c22657f6c3895b04b17802edff1f
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tests")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping
    public List<Test> getAllTests() {
        return testService.getAllTests();
    }

    @PostMapping("/create")
    public ResponseEntity<Test> createTest(@RequestBody Test test) {
        Test createdTest = testService.createTest(test);
        return ResponseEntity.ok(createdTest);
    }

    @PutMapping("/{testId}")
    public ResponseEntity<Test> updateTest(@PathVariable Long testId, @RequestBody Test updatedTest) {
        Test updated = testService.updateTest(testId, updatedTest);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/public")
    public List<Test> getAvailablePublicTests() {
        return testService.getAvailablePublicTests();
    }

    @GetMapping("/{testId}/details")
    public ResponseEntity<Test> getTestDetails(@PathVariable Long testId) {
        Test testDetails = testService.getTestDetails(testId);
        return ResponseEntity.ok(testDetails);
    }

    @PutMapping("/{testId}/publish")
    public ResponseEntity<Test> publishTest(@PathVariable Long testId, @RequestParam Boolean accesRestreint) {
        Test publishedTest = testService.publishTest(testId, accesRestreint);
        return ResponseEntity.ok(publishedTest);
    }

    @GetMapping("/isCompleted")
    public boolean isTestCompleted(
            @RequestParam Long testId,
            @RequestParam Long developpeurId) {
        return testService.isTestCompleted(testId, developpeurId);
    }
}
