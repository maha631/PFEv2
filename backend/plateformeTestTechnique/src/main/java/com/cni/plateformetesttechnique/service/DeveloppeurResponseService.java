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

import java.util.List;
import java.util.Optional;


@Service
public class DeveloppeurResponseService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private DeveloppeurResponseRepository developpeurResponseRepository;

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
        System.out.println("testId: " + testId);
        System.out.println("questionId: " + questionId);
        System.out.println("developpeurId: " + developpeurId);
        System.out.println("selectedOptionIds: " + selectedOptionIds);
        System.out.println("reponseLibre: " + reponseLibre);

        // 1. Récupérer le développeur
        Developpeur developpeur = developpeurRepository.findById(developpeurId)
                .orElseThrow(() -> new RuntimeException("Développeur non trouvé avec ID: " + developpeurId));

        // 2. Récupérer le test et la question
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test non trouvé avec ID: " + testId));
        System.out.println("Test récupéré: " + test.getTitre());

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question non trouvée avec ID: " + questionId));
        System.out.println("Question récupérée: " + question.getEnonce());
        Optional<TestQuestion> testQuestionOpt = testQuestionRepository.findByTestIdAndQuestionId(testId, questionId);
        Integer point = testQuestionOpt.map(TestQuestion::getPoints).orElse(0); // valeur par défaut si non trouvé

        System.out.println(">>> Points de cette question dans ce test: " + point);
        // 3. Vérifier si la question est bien dans le test
        boolean questionInTest = test.getTestQuestions().stream()
                .anyMatch(tq -> tq.getQuestion().getId().equals(questionId));

        System.out.println("questionInTest: " + questionInTest);

        if (!questionInTest) {
            throw new RuntimeException("Cette question n'appartient pas au test spécifié.");
        }

        // 4. Si question libre (code)
        if (question.getType() != TypeQuestion.QCM && reponseLibre != null)
        {
            System.out.println("Traitement d'une réponse libre (code)");

            EvaluationResult geminiResponse = GeminiService.evaluateDeveloperResponse(question.getEnonce(), reponseLibre,point).block();
            System.out.println("Réponse de Gemini: " + geminiResponse);

            DeveloppeurResponse developpeurResponse = new DeveloppeurResponse(test, question, null, true, developpeur);
            developpeurResponse.setIsCorrect(geminiResponse.getIsCorrecte());
            developpeurResponse.setReponseLibre(reponseLibre);
            developpeurResponse.setNote(geminiResponse.getNote());
            developpeurResponse.setExplication(geminiResponse.getExplication());
            developpeurResponse.setFeedback(geminiResponse.getFeedback());
            developpeurResponse.setReponseCorrecte(geminiResponse.getReponseCorrecte());
            System.out.println("===== DEBUG - DEVELOPPEUR RESPONSE =====");
            System.out.println("isCorrect      : " + developpeurResponse.getIsCorrect());
            System.out.println("note           : " + developpeurResponse.getNote());
            System.out.println("explication    : " + developpeurResponse.getExplication());
            System.out.println("reponseCorrecte: " + developpeurResponse.getReponseCorrecte());
            System.out.println("feedback       : " + developpeurResponse.getFeedback());
            System.out.println("reponseLibre   : " + developpeurResponse.getReponseLibre());
            System.out.println("========================================");

            developpeurResponseRepository.save(developpeurResponse);
            System.out.println("Réponse libre sauvegardée.");

            List<DeveloppeurResponse> responses = developpeurResponseRepository.findByTest_IdAndDeveloppeur_Id(testId, developpeurId);
            System.out.println("Nombre total de réponses du développeur: " + responses.size());

            if (responses.size() == test.getTestQuestions().size()) {
                System.out.println("Toutes les questions sont répondues. Calcul du score.");
                return scoreService.calculerScore(testId, developpeurId);
            }

            return null;
        }

        // 5. Si question à choix
        List<AnswerOption> selectedOptions = answerOptionRepository.findAllByIdIn(selectedOptionIds);
        System.out.println("Options sélectionnées récupérées: " + selectedOptions.size());

        boolean allOptionsBelongToQuestion = selectedOptions.stream()
                .allMatch(option -> option.getQuestion().getId().equals(questionId));

        System.out.println("Toutes les options appartiennent à la question: " + allOptionsBelongToQuestion);
        if (!allOptionsBelongToQuestion) {
            throw new RuntimeException("Certaines options sélectionnées n'appartiennent pas à cette question.");
        }

        boolean isCorrect = selectedOptions.stream().allMatch(AnswerOption::getIsCorrect);
        System.out.println("Les options sélectionnées sont toutes correctes ? " + isCorrect);

        DeveloppeurResponse existingResponse = developpeurResponseRepository.findByDeveloppeurAndTestAndQuestion(developpeur, test, question);

        if (existingResponse != null) {
            System.out.println("Mise à jour d'une réponse existante.");
            existingResponse.setSelectedAnswerOptions(selectedOptions);
            existingResponse.setIsCorrect(isCorrect);
            developpeurResponseRepository.save(existingResponse);
        } else {
            System.out.println("Création d'une nouvelle réponse.");
            DeveloppeurResponse developpeurResponse = new DeveloppeurResponse(test, question, selectedOptions, isCorrect, developpeur);
            developpeurResponseRepository.save(developpeurResponse);
        }

        List<DeveloppeurResponse> responses = developpeurResponseRepository.findByTest_IdAndDeveloppeur_Id(testId, developpeurId);
        System.out.println("Total de réponses après ajout: " + responses.size());

        if (responses.size() == test.getTestQuestions().size()) {
            System.out.println("Toutes les questions sont répondues. Calcul du score.");
            return scoreService.calculerScore(testId, developpeurId);
        }

        System.out.println(">>> Fin de la méthode enregistrerReponse()");
        return null;
    }


    @Transactional
    public void supprimerReponses(Long testId, Long developpeurId) {
        // Récupérer le développeur
        Developpeur developpeur = developpeurRepository.findById(developpeurId)
                .orElseThrow(() -> new RuntimeException("Développeur non trouvé avec ID: " + developpeurId));

        // Vérifier si le développeur a des réponses enregistrées pour ce test
        List<DeveloppeurResponse> responses = developpeurResponseRepository.findByTest_IdAndDeveloppeur_Id(testId, developpeurId);

        if (responses.isEmpty()) {
            System.out.println("Aucune réponse trouvée pour ce développeur dans ce test.");
            return;
        }

        // Affichage du nombre de réponses à supprimer
        System.out.println("Réponses trouvées à supprimer pour le développeur : " + developpeur.getEmail());

        // Supprimer les réponses associées
        for (DeveloppeurResponse response : responses) {
            developpeurResponseRepository.delete(response);
            System.out.println("Réponse supprimée pour la question ID : " + response.getQuestion().getId());
        }

        // Optionnel : supprimer aussi les options associées si nécessaire
        for (DeveloppeurResponse response : responses) {
            List<AnswerOption> selectedOptions = response.getSelectedAnswerOptions();
            for (AnswerOption option : selectedOptions) {
                answerOptionRepository.delete(option); // Si tu souhaites aussi supprimer les options, mais cela dépend du cas d'utilisation.
                System.out.println("Option de réponse supprimée : " + option.getId());
            }
        }

        // Optionnel : Message de confirmation
        System.out.println("Toutes les réponses pour le test ID " + testId + " ont été supprimées pour le développeur " + developpeur.getEmail());
    }


}