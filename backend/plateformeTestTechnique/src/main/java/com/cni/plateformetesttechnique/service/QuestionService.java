package com.cni.plateformetesttechnique.service;
import com.cni.plateformetesttechnique.dto.AnswerOptionRequest;
import com.cni.plateformetesttechnique.dto.QuestionRequest;
import com.cni.plateformetesttechnique.dto.RemplacementRequest;
import com.cni.plateformetesttechnique.model.*;
import com.cni.plateformetesttechnique.repository.QuestionRepository;
import com.cni.plateformetesttechnique.repository.TestQuestionRepository;
import com.cni.plateformetesttechnique.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.List;
import java.util.Random;
@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private TestQuestionRepository testQuestionRepository;
    public Long getQuestionIdByEnonce(String enonce) {
        return questionRepository.findByEnonce(enonce)
                .map(Question::getId)
                .orElseThrow(() -> new RuntimeException("Aucune question trouvée avec cet énoncé: " + enonce));
    }


    public Question remplacerQuestion(RemplacementRequest request) {
        // Récupérer les questions candidates qui correspondent aux critères
        List<Question> candidates = questionRepository.findByTypeAndNiveauAndIdNotAndTechnologie(
                request.getType(),
                request.getNiveau(),
                request.getId(),
                request.getTechnologie()
        );

        // Si aucune question disponible, on lève une exception
        if (candidates.isEmpty()) {
            throw new RuntimeException("Aucune question disponible pour le remplacement.");
        }

        // Sélection aléatoire réelle
        Random random = new Random();
        int randomIndex = random.nextInt(candidates.size());
        return candidates.get(randomIndex);
    }

    public Question ajouterQuestionDepuisDTO(QuestionRequest request) {
        if (request.getType() == TypeQuestion.QCM) {
            MultipleChoiceQuestion mcq = new MultipleChoiceQuestion();
            mcq.setEnonce(request.getEnonce());
            mcq.setNiveau(request.getNiveau());
            mcq.setType(request.getType());

            // Nouveaux attributs
            mcq.setTechnologie(request.getTechnologie());
            mcq.setTempsEstime(request.getTempsEstime());

            List<AnswerOption> options = new ArrayList<>();
            for (AnswerOptionRequest optionReq : request.getAnswerOptions()) {
                AnswerOption option = new AnswerOption();
                option.setText(optionReq.getText());
                option.setIsCorrect(optionReq.getIsCorrect());
                option.setQuestion(mcq);
                options.add(option);
            }

            mcq.setAnswerOptions(options);
            return questionRepository.save(mcq);

        } else if (request.getType() == TypeQuestion.Code) {
            CodeQuestion codeQ = new CodeQuestion();
            codeQ.setEnonce(request.getEnonce());
            codeQ.setNiveau(request.getNiveau());
            codeQ.setType(request.getType());
            codeQ.setLanguage(request.getLanguage());

            // Nouveaux attributs
            codeQ.setTechnologie(request.getTechnologie());
            codeQ.setTempsEstime(request.getTempsEstime());

            return questionRepository.save(codeQ);
        } else {
            throw new IllegalArgumentException("Type de question non supporté");
        }
    }



//    public Question ajouterQuestion(Question question) {
//        if (question instanceof MultipleChoiceQuestion) {
//            MultipleChoiceQuestion mcQuestion = (MultipleChoiceQuestion) question;
//            mcQuestion.getAnswerOptions().forEach(option -> option.setQuestion(mcQuestion));
//            return questionRepository.save(mcQuestion);
//        } else if (question instanceof CodeQuestion) {
//            // Sauvegarde spécifique pour les CodeQuestion
//            return questionRepository.save((CodeQuestion) question);
//        } else {
//            return questionRepository.save(question);
//        }
//    }

    // Récupérer toutes les questions
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Récupérer une question par son ID
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question non trouvée avec ID: " + id));
    }

    // Récupérer des questions par type
    public List<Question> getQuestionsByType(TypeQuestion type) {
        return questionRepository.findByType(type);
    }

    // Mettre à jour une question existante
    public Question updateQuestion(Long id, Question questionUpdated) {
        Question question = getQuestionById(id);
        question.setEnonce(questionUpdated.getEnonce());
        question.setNiveau(questionUpdated.getNiveau());
        question.setType(questionUpdated.getType());

        if (question instanceof CodeQuestion && questionUpdated instanceof CodeQuestion) {
            CodeQuestion codeQuestion = (CodeQuestion) question;
            CodeQuestion updatedCodeQuestion = (CodeQuestion) questionUpdated;
            codeQuestion.setLanguage(updatedCodeQuestion.getLanguage());
        }

        // Pour MultipleChoiceQuestion, mise à jour des AnswerOptions
        if (question instanceof MultipleChoiceQuestion && questionUpdated instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcQuestion = (MultipleChoiceQuestion) question;
            MultipleChoiceQuestion mcUpdatedQuestion = (MultipleChoiceQuestion) questionUpdated;
            mcQuestion.getAnswerOptions().clear();
            mcQuestion.getAnswerOptions().addAll(mcUpdatedQuestion.getAnswerOptions());
            mcQuestion.getAnswerOptions().forEach(option -> option.setQuestion(mcQuestion));
        }

        return questionRepository.save(question);
    }

    // Supprimer une question par son ID
    public void deleteQuestion(Long id) {
        Question question = getQuestionById(id);
        questionRepository.delete(question);
    }
    public Question ajouterQuestionAuTest(Long testId, MultipleChoiceQuestion question, Integer points, Integer ordre) {
        // Récupérer le test
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test non trouvé avec ID: " + testId));

        // Enregistrer d'abord la question
        question.getAnswerOptions().forEach(option -> option.setQuestion(question));
        Question savedQuestion = questionRepository.save(question);

        // Associer la question au test avec points et ordre
        TestQuestion testQuestion = new TestQuestion();
        testQuestion.setTest(test);
        testQuestion.setQuestion(savedQuestion);
        testQuestion.setPoints(points);
        testQuestion.setOrdre(ordre);

        // Sauvegarder le TestQuestion
        testQuestionRepository.save(testQuestion);

        return savedQuestion;
    }

}