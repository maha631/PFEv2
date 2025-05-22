//package com.cni.plateformetesttechnique.controller;
//
//import com.cni.plateformetesttechnique.model.Question;
//import com.cni.plateformetesttechnique.model.TestQuestion;
//import com.cni.plateformetesttechnique.service.TestQuestionService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/test-questions")
//public class TestQuestionController {
//
//    @Autowired
//    private TestQuestionService testQuestionService;
//
//    // Ajouter des questions à un test
//    @PostMapping("/add/{testId}")
//    public ResponseEntity<List<TestQuestion>> addQuestionsToTest(
//            @PathVariable Long testId,
//            @RequestBody List<TestQuestion> testQuestions) {
//        List<TestQuestion> savedTestQuestions = testQuestionService.addQuestionsToTest(testId, testQuestions);
//        return ResponseEntity.ok(savedTestQuestions);
//    }
//
//    // Récupérer les questions d'un test
//    @GetMapping("/test/{testId}")
//    public ResponseEntity<List<Question>> getQuestionsForTest(@PathVariable Long testId) {
//        List<Question> questions = testQuestionService.getQuestionsForTest(testId);
//        return ResponseEntity.ok(questions);
//    }
//
//    // Supprimer une question d'un test
//    @DeleteMapping("/remove/{testId}/{questionId}")
//    public ResponseEntity<Object> removeQuestionFromTest(@PathVariable Long testId, @PathVariable Long questionId) {
//        try {
//            testQuestionService.removeQuestionFromTest(testId, questionId);
//            // Réponse JSON avec statut et message
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
//                    "{ \"status\": \"Succès\", \"message\": \"La question a été supprimée du test avec succès.\" }"
//            );
//        } catch (Exception e) {
//            // En cas d'erreur, retour d'un message JSON d'erreur
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    "{ \"status\": \"Erreur\", \"message\": \"Impossible de supprimer la question : " + e.getMessage() + "\" }"
//            );
//        }
//    }
//}
package com.cni.plateformetesttechnique.controller;

import com.cni.plateformetesttechnique.dto.QuestionDTO;
import com.cni.plateformetesttechnique.model.Question;
import com.cni.plateformetesttechnique.model.TestQuestion;
import com.cni.plateformetesttechnique.service.TestQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-questions")
public class TestQuestionController {

    @Autowired
    private TestQuestionService testQuestionService;

    // Ajouter des questions à un test - accessible par ADMIN et ChefProjet uniquement
    @PostMapping("/add/{testId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ChefProjet')")
    public ResponseEntity<List<TestQuestion>> addQuestionsToTest(
    	    @PathVariable("testId") Long testId,
            @RequestBody List<TestQuestion> testQuestions) {
        List<TestQuestion> savedTestQuestions = testQuestionService.addQuestionsToTest(testId, testQuestions);
        return ResponseEntity.ok(savedTestQuestions);
    }

    // Récupérer les questions d'un test - accessible à tous
    @GetMapping("/test/{testId}")
<<<<<<< HEAD
//    public ResponseEntity<List<QuestionDTO>> getQuestionsForTest(@PathVariable Long testId) {
//        try {
//            // Appeler la méthode du service pour récupérer les questions avec points et ordre
//            List<QuestionDTO> questions = testQuestionService.getQuestionsForTest(testId);
//
//            // Si aucune question n'est trouvée pour ce test
//            if (questions.isEmpty()) {
//                return ResponseEntity.noContent().build();  // Code 204 No Content
//            }
//
//            // Retourner la liste des questions avec leurs points et ordre
//            return ResponseEntity.ok(questions);  // Code 200 OK
//        } catch (Exception e) {
//            // Gérer l'exception et renvoyer un code 500 en cas d'erreur
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
    public ResponseEntity<List<Question>> getQuestionsForTest( @PathVariable(name = "testId")  Long testId) {
=======

    public ResponseEntity<List<Question>> getQuestionsForTest(@PathVariable Long testId) {
>>>>>>> 6742670604363261b699a7cbbe83243f84dd841d
        List<Question> questions = testQuestionService.getQuestionsForTest(testId);
        return ResponseEntity.ok(questions);
    }

    // Supprimer une question d'un test - accessible par ADMIN et ChefProjet uniquement
    @DeleteMapping("/remove/{testId}/{questionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ChefProjet')")
    public ResponseEntity<Object> removeQuestionFromTest(@PathVariable Long testId, @PathVariable Long questionId) {
        try {
            testQuestionService.removeQuestionFromTest(testId, questionId);
            // Réponse JSON avec statut et message
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    "{ \"status\": \"Succès\", \"message\": \"La question a été supprimée du test avec succès.\" }"
            );
        } catch (Exception e) {
            // En cas d'erreur, retour d'un message JSON d'erreur
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "{ \"status\": \"Erreur\", \"message\": \"Impossible de supprimer la question : " + e.getMessage() + "\" }"
            );
        }
    }
}
