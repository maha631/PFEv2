package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.model.*;
import com.cni.plateformetesttechnique.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;
    @Autowired
    private DeveloppeurResponseRepository developpeurResponseRepository;

    @Autowired
    private QuestionRepository questionRepository; // Correction ici
    @Autowired
    private DeveloppeurRepository developpeurRepository; // ✅ Ajout de l’injection du repository

    @Autowired
    private TestQuestionRepository testQuestionRepository; // Correction ici
    @Autowired
    private InvitationTestRepository invitationTestRepository;
    @Autowired
    private EmailService emailService;

    public Test createTest(Test test) {

        // Définir les valeurs par défaut
        test.setStatut("BROUILLON");
        test.setDateCreation(LocalDateTime.now());

        // Sauvegarde du test
        return testRepository.save(test);
    }
    public Optional<Test> getTestById(Long testId) {
        return testRepository.findById(testId);
    }
    public Test updateTest(Long testId, Test updatedTest) {
        // Vérifier si le test existe
        Test existingTest = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test non trouvé"));

        // Vérifier si le test est encore en brouillon
        if (!"BROUILLON".equals(existingTest.getStatut())) {
            throw new RuntimeException("Impossible de modifier un test qui n'est pas en brouillon");
        }

        // Mettre à jour les champs modifiables
        existingTest.setTitre(updatedTest.getTitre());
        existingTest.setDescription(updatedTest.getDescription());
        existingTest.setDuree(updatedTest.getDuree());
        existingTest.setType(updatedTest.getType());
        existingTest.setAccesPublic(updatedTest.getAccesPublic());
        existingTest.setLimiteTentatives(updatedTest.getLimiteTentatives());
        existingTest.setDateExpiration(updatedTest.getDateExpiration());

        // Sauvegarder les modifications
        return testRepository.save(existingTest);
    }
    public List<Test> getAvailablePublicTests() {
        LocalDateTime now = LocalDateTime.now();
        return testRepository.findByAccesPublicTrueAndStatutAndDateExpirationAfter("PUBLIE", now);
    }

    public Test getTestDetails(Long testId) {
        // Vérifier si le test existe
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test non trouvé"));

        // Charger les questions associées avec ordre et points
        List<TestQuestion> testQuestions = testQuestionRepository.findByTestId(testId);

        // Ajouter les questions récupérées dans l'objet test
        test.setTestQuestions(testQuestions);

        return test;
    }
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }
    public void sendInvitationEmails(Test test) {
        List<InvitationTest> invitations = invitationTestRepository.findByTest(test);

        for (InvitationTest invitation : invitations) {
            Developpeur developer = invitation.getDeveloppeur();
            emailService.sendTestPublishedNotification(test, developer);
        }
    }
    public Test publishTest(Long testId, Boolean accesRestreint) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test non trouvé"));

        // Vérifier si le test est en brouillon avant de le publier
        if (!"BROUILLON".equals(test.getStatut())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seuls les tests en brouillon peuvent être publiés");
        }

        // Vérifier que le test contient au moins une question avant publication
        if (testQuestionRepository.findByTestId(testId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un test doit contenir au moins une question avant d'être publié");
        }

        // Mise à jour du statut et accès restreint
        test.setStatut("PUBLIE");
        test.setAccesPublic(accesRestreint);
        test.setDateExpiration(LocalDateTime.now().plusDays(30)); // Archivage après 30 jours

        Test publishedTest = testRepository.save(test);

        // Si le test est sur invitation, envoyer un email aux développeurs invités
        if (accesRestreint) {
            sendInvitationEmails(test);
        }

        return publishedTest;
    }
    public boolean isTestCompleted(Long testId, Long developpeurId) {
        // Récupérer le nombre total de questions du test
        int totalQuestions = testQuestionRepository.countByTest_Id(testId);

        // Récupérer le nombre de questions auxquelles le développeur a répondu
        int answeredQuestions = developpeurResponseRepository.countByTest_IdAndDeveloppeur_Id(testId, developpeurId);

        // Si le développeur a répondu à toutes les questions, le test est complété
        return answeredQuestions == totalQuestions;
    }

}
