package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.model.*;
import com.cni.plateformetesttechnique.repository.*;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class DeveloppeurResponseService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private DeveloppeurResponseRepository developpeurResponseRepository;
    @Autowired
    private DeveloppeurTestScoreRepository developpeurTestScoreRepository;
    @Autowired
    private DeveloppeurRepository developpeurRepository;  // Assurez-vous que c'est bien cette variable qui est utilisée
    @Autowired
    private TestQuestionRepository testQuestionRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private GeminiService GeminiService;
    @Autowired
    private AnswerOptionRepository answerOptionRepository;
    @Autowired

    private ScoreService scoreService;
    public Double enregistrerReponse(Long testId, Long questionId, List<Long> selectedOptionIds, Long developpeurId, String reponseLibre) {
        System.out.println(">>> Début de la méthode enregistrerReponse()");
        Developpeur developpeur = developpeurRepository.findById(developpeurId)
                .orElseThrow(() -> new RuntimeException("Développeur non trouvé avec ID: " + developpeurId));

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test non trouvé avec ID: " + testId));

        // ✅ Récupérer ou créer la tentative en cours
        DeveloppeurTestScore developpeurTestScore = developpeurTestScoreRepository
                .findTopByDeveloppeur_IdAndTest_IdOrderByAttemptNumberDesc(developpeurId, testId)
                .orElse(null);

        boolean needsNewAttempt = true;
        if (developpeurTestScore != null) {
            long responsesCount = developpeurResponseRepository.countByDeveloppeurTestScore_Id(developpeurTestScore.getId());
            long totalQuestions = test.getTestQuestions().size();
//
//            // 🔒 Ne pas permettre une nouvelle réponse si déjà terminé
//            if (developpeurTestScore.getFinishedAt() != null) {
//                throw new RuntimeException("Ce test a déjà été terminé.");
//            }

            if (responsesCount < totalQuestions && developpeurTestScore.getFinishedAt() == null) {
                needsNewAttempt = false;
            }
        }

        if (needsNewAttempt) {
            int nextAttempt = developpeurTestScore != null ? developpeurTestScore.getAttemptNumber() + 1 : 1;
            developpeurTestScore = new DeveloppeurTestScore();
            developpeurTestScore.setDeveloppeur(developpeur);
            developpeurTestScore.setTest(test);
            developpeurTestScore.setScore(0.0);
            developpeurTestScore.setAttemptNumber(nextAttempt);
            developpeurTestScore.setCreatedAt(LocalDateTime.now());
            developpeurTestScore = developpeurTestScoreRepository.save(developpeurTestScore);
        }

        // ✅ Gérer le dépassement du temps
        LocalDateTime startedAt = developpeurTestScore.getCreatedAt();
        Integer dureeMinutes = test.getDuree(); // durée en minutes
        if (dureeMinutes != null && startedAt != null) {
            LocalDateTime expectedEndTime = startedAt.plusMinutes(dureeMinutes);
            if (LocalDateTime.now().isAfter(expectedEndTime)) {
                developpeurTestScore.setFinishedAt(LocalDateTime.now());
                developpeurTestScoreRepository.save(developpeurTestScore);
                throw new RuntimeException("Temps écoulé pour compléter le test.");
            }
        }

        // ✅ Vérification de la question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question non trouvée avec ID: " + questionId));

        Optional<TestQuestion> testQuestionOpt = testQuestionRepository.findByTestIdAndQuestionId(testId, questionId);
        Integer point = testQuestionOpt.map(TestQuestion::getPoints).orElse(0);

        boolean questionInTest = test.getTestQuestions().stream()
                .anyMatch(tq -> tq.getQuestion().getId().equals(questionId));
        if (!questionInTest) {
            throw new RuntimeException("Cette question n'appartient pas au test spécifié.");
        }

        // ✅ Vérification s'il a déjà répondu à cette question
        boolean alreadyAnswered = developpeurResponseRepository.existsByDeveloppeurTestScore_IdAndQuestion_Id(developpeurTestScore.getId(), questionId);
        if (alreadyAnswered) {

            // ✅ Supprimer la réponse précédente si elle existe
            Optional<DeveloppeurResponse> ancienneReponseOpt = developpeurResponseRepository
                    .findByDeveloppeurTestScore_IdAndQuestion_Id(developpeurTestScore.getId(), questionId);

            ancienneReponseOpt.ifPresent(ancienneReponse -> developpeurResponseRepository.delete(ancienneReponse));

//            throw new RuntimeException("Vous avez déjà répondu à cette question.");
        }

        // ✅ Enregistrement de la réponse
        if (question.getType() != TypeQuestion.QCM && reponseLibre != null) {
            EvaluationResult geminiResponse = GeminiService.evaluateDeveloperResponse(
                    question.getEnonce(), reponseLibre, point).block();

            DeveloppeurResponse developpeurResponse = new DeveloppeurResponse(
                    question, null, true
            );
            developpeurResponse.setDeveloppeurTestScore(developpeurTestScore);
            developpeurResponse.setIsCorrect(geminiResponse.getIsCorrecte());
            developpeurResponse.setReponseLibre(reponseLibre);
            developpeurResponse.setNote(geminiResponse.getNote());
            developpeurResponse.setExplication(geminiResponse.getExplication());
            developpeurResponse.setFeedback(geminiResponse.getFeedback());
            developpeurResponse.setReponseCorrecte(geminiResponse.getReponseCorrecte());

            developpeurResponseRepository.save(developpeurResponse);
        } else {
            List<AnswerOption> selectedOptions = answerOptionRepository.findAllByIdIn(selectedOptionIds);
            boolean allOptionsBelongToQuestion = selectedOptions.stream()
                    .allMatch(option -> option.getQuestion().getId().equals(questionId));
            if (!allOptionsBelongToQuestion) {
                throw new RuntimeException("Certaines options sélectionnées n'appartiennent pas à cette question.");
            }

            boolean isCorrect = selectedOptions.stream().allMatch(AnswerOption::getIsCorrect);
            DeveloppeurResponse developpeurResponse = new DeveloppeurResponse(
                    question, selectedOptions, isCorrect
            );
            developpeurResponse.setDeveloppeurTestScore(developpeurTestScore);
            developpeurResponseRepository.save(developpeurResponse);
        }

        // ✅ Vérification : toutes les questions sont-elles répondues ?
        long totalReponses = developpeurResponseRepository.countByDeveloppeurTestScore_Id(developpeurTestScore.getId());
        long totalQuestions = test.getTestQuestions().size();
        if (totalReponses == totalQuestions) {
            developpeurTestScore.setFinishedAt(LocalDateTime.now());
            developpeurTestScoreRepository.save(developpeurTestScore);
        }

        // ✅ Calcul du score
        System.out.println("Mise à jour du score après cette réponse.");
        Double score = scoreService.calculerScoreParTentative(developpeurTestScore.getId());

        return score;
    }


//    public Double enregistrerReponse(Long testId, Long questionId, List<Long> selectedOptionIds, Long developpeurId, String reponseLibre) {
//        System.out.println(">>> Début de la méthode enregistrerReponse()");
//        System.out.println("testId: " + testId);
//        System.out.println("questionId: " + questionId);
//        System.out.println("developpeurId: " + developpeurId);
//        System.out.println("selectedOptionIds: " + selectedOptionIds);
//        System.out.println("reponseLibre: " + reponseLibre);
//
//        Developpeur developpeur = developpeurRepository.findById(developpeurId)
//                .orElseThrow(() -> new RuntimeException("Développeur non trouvé avec ID: " + developpeurId));
//
//        Test test = testRepository.findById(testId)
//                .orElseThrow(() -> new RuntimeException("Test non trouvé avec ID: " + testId));
//        System.out.println("Test récupéré: " + test.getTitre());
//
//        // ✅ Trouver ou créer la bonne tentative
//        DeveloppeurTestScore developpeurTestScore = developpeurTestScoreRepository
//                .findTopByDeveloppeur_IdAndTest_IdOrderByAttemptNumberDesc(developpeurId, testId)
//                .orElse(null);
//
//        boolean needsNewAttempt = true;
//        if (developpeurTestScore != null) {
//            long responsesCount = developpeurResponseRepository
//                    .countByDeveloppeurTestScore_Id(developpeurTestScore.getId());
//            long totalQuestions = test.getTestQuestions().size();
//
//            if (responsesCount < totalQuestions) {
//                needsNewAttempt = false;
//            }
//        }
//
//        if (needsNewAttempt) {
//            int nextAttempt = developpeurTestScore != null ? developpeurTestScore.getAttemptNumber() + 1 : 1;
//            developpeurTestScore = new DeveloppeurTestScore();
//            developpeurTestScore.setDeveloppeur(developpeur);
//            developpeurTestScore.setTest(test);
//            developpeurTestScore.setScore(0.0);
//            developpeurTestScore.setAttemptNumber(nextAttempt);
//            developpeurTestScore = developpeurTestScoreRepository.save(developpeurTestScore);
//        }
//
//        Question question = questionRepository.findById(questionId)
//                .orElseThrow(() -> new RuntimeException("Question non trouvée avec ID: " + questionId));
//        System.out.println("Question récupérée: " + question.getEnonce());
//
//        Optional<TestQuestion> testQuestionOpt = testQuestionRepository.findByTestIdAndQuestionId(testId, questionId);
//        Integer point = testQuestionOpt.map(TestQuestion::getPoints).orElse(0);
//
//        boolean questionInTest = test.getTestQuestions().stream()
//                .anyMatch(tq -> tq.getQuestion().getId().equals(questionId));
//        if (!questionInTest) {
//            throw new RuntimeException("Cette question n'appartient pas au test spécifié.");
//        }
//
//        if (question.getType() != TypeQuestion.QCM && reponseLibre != null) {
//            EvaluationResult geminiResponse = GeminiService.evaluateDeveloperResponse(question.getEnonce(), reponseLibre, point).block();
//            DeveloppeurResponse developpeurResponse = new DeveloppeurResponse(
//                    question,
//                    null,
//                    true
//
//            );
//            developpeurResponse.setDeveloppeurTestScore(developpeurTestScore);
//            developpeurResponse.setIsCorrect(geminiResponse.getIsCorrecte());
//            developpeurResponse.setReponseLibre(reponseLibre);
//            developpeurResponse.setNote(geminiResponse.getNote());
//            developpeurResponse.setExplication(geminiResponse.getExplication());
//            developpeurResponse.setFeedback(geminiResponse.getFeedback());
//            developpeurResponse.setReponseCorrecte(geminiResponse.getReponseCorrecte());
//            developpeurResponseRepository.save(developpeurResponse);
//        } else {
//            List<AnswerOption> selectedOptions = answerOptionRepository.findAllByIdIn(selectedOptionIds);
//            boolean allOptionsBelongToQuestion = selectedOptions.stream()
//                    .allMatch(option -> option.getQuestion().getId().equals(questionId));
//            if (!allOptionsBelongToQuestion) {
//                throw new RuntimeException("Certaines options sélectionnées n'appartiennent pas à cette question.");
//            }
//            boolean isCorrect = selectedOptions.stream().allMatch(AnswerOption::getIsCorrect);
//            DeveloppeurResponse developpeurResponse = new DeveloppeurResponse(
//                    question,
//                    selectedOptions,
//                    isCorrect
//
//            );
//            developpeurResponse.setDeveloppeurTestScore(developpeurTestScore);
//            developpeurResponseRepository.save(developpeurResponse);
//        }
//
//        List<DeveloppeurResponse> responses = developpeurResponseRepository
//                .findByDeveloppeurTestScore_Developpeur_IdAndDeveloppeurTestScore_Test_Id(developpeurId, testId);
//        System.out.println("Mise à jour du score après cette réponse.");
//        Double score = scoreService.calculerScoreParTentative(developpeurTestScore.getId());
//        return score;
//
//    }





    @Transactional

    public void supprimerReponsesParTentative(Long developpeurTestScoreId) {
        // 1. Récupérer la tentative (DeveloppeurTestScore)
        Optional<DeveloppeurTestScore> optionalScore = developpeurTestScoreRepository.findById(developpeurTestScoreId);

        if (optionalScore.isEmpty()) {
            System.out.println("Tentative non trouvée avec l'ID : " + developpeurTestScoreId);
            return;
        }

        DeveloppeurTestScore score = optionalScore.get();
        Long developpeurId = score.getDeveloppeur().getId();
        Long testId = score.getTest().getId();

        // 2. Récupérer les réponses liées à cette tentative
        List<DeveloppeurResponse> responses = developpeurResponseRepository
                .findByDeveloppeurTestScore_Id(developpeurTestScoreId);

        if (responses.isEmpty()) {
            System.out.println("Aucune réponse trouvée pour cette tentative.");
            return;
        }

        System.out.println("Suppression des réponses pour la tentative ID : " + developpeurTestScoreId);

        // 3. Supprimer les réponses (⚠️ on ne supprime PAS les options sélectionnées elles-mêmes !)
        for (DeveloppeurResponse response : responses) {
            response.setSelectedAnswerOptions(null); // Si cascade persistée
            developpeurResponseRepository.delete(response);
            System.out.println("Réponse supprimée pour la question ID : " + response.getQuestion().getId());
        }

        // 4. Optionnel : supprimer aussi la tentative
        developpeurTestScoreRepository.delete(score);
        System.out.println("Tentative supprimée pour le développeur ID " + developpeurId + ", test ID " + testId);
    }

//    public void supprimerReponses(Long testId, Long developpeurId) {
//        // Récupérer le développeur
//        Developpeur developpeur = developpeurRepository.findById(developpeurId)
//                .orElseThrow(() -> new RuntimeException("Développeur non trouvé avec ID: " + developpeurId));
//
//        // Vérifier si le développeur a des réponses enregistrées pour ce test
//        List<DeveloppeurResponse> responses = developpeurResponseRepository.findByTest_IdAndDeveloppeur_Id(testId, developpeurId);
//
//        if (responses.isEmpty()) {
//            System.out.println("Aucune réponse trouvée pour ce développeur dans ce test.");
//            return;
//        }
//
//        // Affichage du nombre de réponses à supprimer
//        System.out.println("Réponses trouvées à supprimer pour le développeur : " + developpeur.getEmail());
//
//        // Supprimer les réponses associées
//        for (DeveloppeurResponse response : responses) {
//            developpeurResponseRepository.delete(response);
//            System.out.println("Réponse supprimée pour la question ID : " + response.getQuestion().getId());
//        }
//
//        // Optionnel : supprimer aussi les options associées si nécessaire
//        for (DeveloppeurResponse response : responses) {
//            List<AnswerOption> selectedOptions = response.getSelectedAnswerOptions();
//            for (AnswerOption option : selectedOptions) {
//                answerOptionRepository.delete(option); // Si tu souhaites aussi supprimer les options, mais cela dépend du cas d'utilisation.
//                System.out.println("Option de réponse supprimée : " + option.getId());
//            }
//        }
//
//        // Optionnel : Message de confirmation
//        System.out.println("Toutes les réponses pour le test ID " + testId + " ont été supprimées pour le développeur " + developpeur.getEmail());
//    }


}