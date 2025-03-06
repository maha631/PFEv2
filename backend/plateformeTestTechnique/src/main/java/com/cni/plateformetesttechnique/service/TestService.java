package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.model.*;
import com.cni.plateformetesttechnique.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;
    @Autowired
    private DeveloppeurResponseRepository developpeurResponseRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private DeveloppeurRepository developpeurRepository;
    @Autowired
    private TestQuestionRepository testQuestionRepository;
    @Autowired
    private InvitationTestRepository invitationTestRepository;
    @Autowired
    private EmailService emailService;

    public Test createTest(Test test) {
        test.setStatut("BROUILLON");
        test.setDateCreation(LocalDateTime.now());
        return testRepository.save(test);
    }

    public Optional<Test> getTestById(Long testId) {
        return testRepository.findById(testId);
    }

    public Test updateTest(Long testId, Test updatedTest) {
        Test existingTest = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test non trouvé"));

        if (!"BROUILLON".equals(existingTest.getStatut())) {
            throw new RuntimeException("Impossible de modifier un test qui n'est pas en brouillon");
        }

        existingTest.setTitre(updatedTest.getTitre());
        existingTest.setDescription(updatedTest.getDescription());
        existingTest.setDuree(updatedTest.getDuree());
        existingTest.setType(updatedTest.getType());
        existingTest.setAccesPublic(updatedTest.getAccesPublic());
        existingTest.setLimiteTentatives(updatedTest.getLimiteTentatives());
        existingTest.setDateExpiration(updatedTest.getDateExpiration());

        return testRepository.save(existingTest);
    }

    public List<Test> getAvailablePublicTests() {
        LocalDateTime now = LocalDateTime.now();
        return testRepository.findByAccesPublicTrueAndStatutAndDateExpirationAfter("PUBLIE", now);
    }

    public Test getTestDetails(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test non trouvé"));

        List<TestQuestion> testQuestions = testQuestionRepository.findByTestId(testId);
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

        if (!"BROUILLON".equals(test.getStatut())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seuls les tests en brouillon peuvent être publiés");
        }

        if (testQuestionRepository.findByTestId(testId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un test doit contenir au moins une question avant d'être publié");
        }

        test.setStatut("PUBLIE");
        test.setAccesPublic(accesRestreint);
        test.setDateExpiration(LocalDateTime.now().plusDays(30));

        Test publishedTest = testRepository.save(test);

        if (accesRestreint) {
            sendInvitationEmails(test);
        }

        return publishedTest;
    }

    public boolean isTestCompleted(Long testId, Long developpeurId) {
        int totalQuestions = testQuestionRepository.countByTest_Id(testId);
        int answeredQuestions = developpeurResponseRepository.countByTest_IdAndDeveloppeur_Id(testId, developpeurId);
        return answeredQuestions == totalQuestions;
    }
}
