package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.dto.QuestionDTO;
import com.cni.plateformetesttechnique.model.Question;
import com.cni.plateformetesttechnique.model.Test;
import com.cni.plateformetesttechnique.model.TestQuestion;
import com.cni.plateformetesttechnique.repository.QuestionRepository;
import com.cni.plateformetesttechnique.repository.TestQuestionRepository;
import com.cni.plateformetesttechnique.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TestQuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private TestQuestionRepository testQuestionRepository;
    @Autowired
    private TestRepository testRepository;

    public List<Question> getQuestionsForTest(Long testId) {
        List<TestQuestion> testQuestions = testQuestionRepository.findByTestId(testId);
        testQuestions.sort(Comparator.comparingInt(TestQuestion::getOrdre));

        List<Question> questions = new ArrayList<>();
        for (TestQuestion testQuestion : testQuestions) {
            questions.add(testQuestion.getQuestion());
        }
        return questions;
    }
    public List<Integer> getPointsForTest(Long testId) {
        List<TestQuestion> testQuestions = testQuestionRepository.findByTestId(testId);

        // Récupérer la liste des points pour chaque question
        List<Integer> points = new ArrayList<>();
        for (TestQuestion testQuestion : testQuestions) {
            points.add(testQuestion.getPoints());
        }

        return points;
    }
    public List<Integer> getOrdreForTest(Long testId) {
        List<TestQuestion> testQuestions = testQuestionRepository.findByTestId(testId);

        // Récupérer la liste des ordres pour chaque question
        List<Integer> ordres = new ArrayList<>();
        for (TestQuestion testQuestion : testQuestions) {
            ordres.add(testQuestion.getOrdre());
        }

        return ordres;
    }

//public List<QuestionDTO> getQuestionsForTest(Long testId) {
//    // Récupérer les questions associées au test
//    List<TestQuestion> testQuestions = testQuestionRepository.findByTestId(testId);
//
//    // Trier les questions par ordre
//    testQuestions.sort(Comparator.comparingInt(TestQuestion::getOrdre));
//
//    // Créer une liste pour stocker les questions avec leurs métadonnées
//    List<QuestionDTO> questionDTOs = new ArrayList<>();
//
//    // Parcourir chaque TestQuestion et créer un DTO pour inclure l'énoncé et les métadonnées
//    for (TestQuestion testQuestion : testQuestions) {
//        Question question = testQuestion.getQuestion(); // Question associée
//        int points = testQuestion.getPoints();  // Points pour cette question
//        int ordre = testQuestion.getOrdre();   // Ordre pour cette question
//
//        // Ajouter le DTO avec la question, les points et l'ordre
//        QuestionDTO questionDTO = new QuestionDTO(question, points, ordre);
//        questionDTOs.add(questionDTO);
//    }
//
//    // Retourner la liste des DTO
//    return questionDTOs;
//}

    public List<TestQuestion> addQuestionsToTest(Long testId, List<TestQuestion> testQuestions) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test non trouvé"));

        if (!"BROUILLON".equals(test.getStatut())) {
            throw new RuntimeException("Impossible d'ajouter des questions à un test déjà publié");
        }

        List<TestQuestion> savedTestQuestions = new ArrayList<>();

        for (TestQuestion tq : testQuestions) {
            Question question = questionRepository.findById(tq.getQuestion().getId())
                    .orElseThrow(() -> new RuntimeException("Question non trouvée : ID " + tq.getQuestion().getId()));

            boolean alreadyExists = testQuestionRepository.findByTestId(testId).stream()
                    .anyMatch(q -> q.getQuestion().getId().equals(question.getId()));

            if (alreadyExists) {
                throw new RuntimeException("La question ID " + question.getId() + " est déjà associée à ce test");
            }

            TestQuestion newTestQuestion = new TestQuestion(test, question, tq.getPoints(), tq.getOrdre());
            savedTestQuestions.add(testQuestionRepository.save(newTestQuestion));
        }

        return savedTestQuestions;
    }

    public void removeQuestionFromTest(Long testId, Long questionId) {
        TestQuestion testQuestion = testQuestionRepository.findByTestIdAndQuestionId(testId, questionId)
                .orElseThrow(() -> new RuntimeException("Association Test-Question non trouvée"));

        testQuestionRepository.delete(testQuestion);
    }

    public TestQuestionService(TestQuestionRepository testQuestionRepository) {
        this.testQuestionRepository = testQuestionRepository;
    }

    public TestQuestion addTestQuestion(TestQuestion testQuestion) {
        return testQuestionRepository.save(testQuestion);
    }
}
