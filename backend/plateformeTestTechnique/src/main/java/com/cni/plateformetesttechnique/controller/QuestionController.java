//package com.cni.plateformetesttechnique.controller;
//
//import com.cni.plateformetesttechnique.model.AnswerOption;
//import com.cni.plateformetesttechnique.model.NiveauQuestion;
//import com.cni.plateformetesttechnique.model.Question;
//import com.cni.plateformetesttechnique.model.TypeQuestion;
//import com.cni.plateformetesttechnique.service.QuestionService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//
//@RequestMapping("/api/questions")
//public class QuestionController {
//
//    @Autowired
//    private QuestionService questionService;
//
//    // Ajouter une nouvelle question
//    @PostMapping("/add")
//    public Question ajouterQuestion(@RequestBody Question question) {
//        return questionService.ajouterQuestion(question);
//    }
//
//    // Récupérer toutes les questions
//    @GetMapping("/all")
//    public List<Question> getAllQuestions() {
//        return questionService.getAllQuestions();
//    }
//
//    // Récupérer une question par son ID
//    @GetMapping("/{id}")
//    public Question getQuestionById(@PathVariable Long id) {
//        return questionService.getQuestionById(id);
//    }
//
//    // Récupérer des questions par type
//    @GetMapping("/type")
//    public List<Question> getQuestionsByType(@RequestParam TypeQuestion type) {
//        return questionService.getQuestionsByType(type);
//    }
//
//    // Mettre à jour une question existante
//    @PutMapping("/update/{id}")
//    public Question updateQuestion(@PathVariable Long id, @RequestBody Question questionUpdated) {
//        return questionService.updateQuestion(id, questionUpdated);
//    }
//
//    // Supprimer une question par son ID
//    @DeleteMapping("/delete/{id}")
//    public void deleteQuestion(@PathVariable Long id) {
//        questionService.deleteQuestion(id);
//    }
//    @PostMapping("/ajouterAuTest/{testId}")
//    public Question ajouterQuestionAuTest(
//            @PathVariable Long testId,
//            @RequestBody Map<String, Object> requestBody) {
//
//        Question question = new Question();
//        question.setEnonce((String) requestBody.get("enonce"));
//        question.setNiveau(NiveauQuestion.valueOf((String) requestBody.get("niveau")));
//        question.setType(TypeQuestion.valueOf((String) requestBody.get("type")));
//
//        // Ajouter les options de réponse
//        List<Map<String, Object>> options = (List<Map<String, Object>>) requestBody.get("answerOptions");
//        List<AnswerOption> answerOptions = new ArrayList<>();
//        for (Map<String, Object> optionData : options) {
//            AnswerOption option = new AnswerOption();
//            option.setText((String) optionData.get("text"));
//            option.setIsCorrect((Boolean) optionData.get("isCorrect"));
//            option.setQuestion(question); // Associer à la question
//            answerOptions.add(option);
//        }
//        question.setAnswerOptions(answerOptions);
//
//        // Récupérer points et ordre
//        Integer points = (Integer) requestBody.get("points");
//        Integer ordre = (Integer) requestBody.get("ordre");
//
//        return questionService.ajouterQuestionAuTest(testId, question, points, ordre);
//    }
//
//}
package com.cni.plateformetesttechnique.controller;

import com.cni.plateformetesttechnique.model.AnswerOption;
import com.cni.plateformetesttechnique.model.NiveauQuestion;
import com.cni.plateformetesttechnique.model.Question;
import com.cni.plateformetesttechnique.model.TypeQuestion;
import com.cni.plateformetesttechnique.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // Ajouter une nouvelle question - accessible par ADMIN et ChefProjet uniquement
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ChefProjet')")
    public Question ajouterQuestion(@RequestBody Question question) {
        return questionService.ajouterQuestion(question);
    }

    // Récupérer toutes les questions - accessible à tous
    @GetMapping("/all")
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    // Récupérer une question par son ID - accessible à tous
    @GetMapping("/{id}")
    public Question getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    // Récupérer des questions par type - accessible à tous
    @GetMapping("/type")
    public List<Question> getQuestionsByType(@RequestParam TypeQuestion type) {
        return questionService.getQuestionsByType(type);
    }

    // Mettre à jour une question existante - accessible par ADMIN et ChefProjet uniquement
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ChefProjet')")
    public Question updateQuestion(@PathVariable Long id, @RequestBody Question questionUpdated) {
        return questionService.updateQuestion(id, questionUpdated);
    }

    // Supprimer une question par son ID - accessible par ADMIN et ChefProjet uniquement
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ChefProjet')")
    public void deleteQuestion(@PathVariable(name="id") Long id) {
        questionService.deleteQuestion(id);
    }

    // Ajouter une question à un test - accessible par ADMIN et ChefProjet uniquement
    @PostMapping("/ajouterAuTest/{testId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ChefProjet')")
    public Question ajouterQuestionAuTest(
            @PathVariable Long testId,
            @RequestBody Map<String, Object> requestBody) {

        Question question = new Question();
        question.setEnonce((String) requestBody.get("enonce"));
        question.setNiveau(NiveauQuestion.valueOf((String) requestBody.get("niveau")));
        question.setType(TypeQuestion.valueOf((String) requestBody.get("type")));

        // Ajouter les options de réponse
        List<Map<String, Object>> options = (List<Map<String, Object>>) requestBody.get("answerOptions");
        List<AnswerOption> answerOptions = new ArrayList<>();
        for (Map<String, Object> optionData : options) {
            AnswerOption option = new AnswerOption();
            option.setText((String) optionData.get("text"));
            option.setIsCorrect((Boolean) optionData.get("isCorrect"));
            option.setQuestion(question); // Associer à la question
            answerOptions.add(option);
        }
        question.setAnswerOptions(answerOptions);

        // Récupérer points et ordre
        Integer points = (Integer) requestBody.get("points");
        Integer ordre = (Integer) requestBody.get("ordre");

        return questionService.ajouterQuestionAuTest(testId, question, points, ordre);
    }
}
