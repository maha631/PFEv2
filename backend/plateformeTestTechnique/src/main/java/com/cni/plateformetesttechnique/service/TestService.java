package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.model.*;
import com.cni.plateformetesttechnique.repository.*;

import com.cni.plateformetesttechnique.security.services.UserDetailsImpl;
import jakarta.transaction.Transactional;
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
    private InvitationTestService invitationTestService;
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
    @Autowired
    private UserRepository userRepository;
    @Transactional
    public Test createTest(Test test, Long createurId) {
        User createur = userRepository.findById(createurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        test.setCreateur(createur);
        test.setStatut("BROUILLON");
        test.setDateCreation(LocalDateTime.now());
        return testRepository.save(test);
    }
    public List<Test> getTestsForCurrentUser(UserDetailsImpl userDetails) {
        if (userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            return testRepository.findAll(); // Admin voit tout
        } else if (userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_CHEF"))) {
            return testRepository.findByCreateurId(userDetails.getId()); // Chef de projet voit ses tests
        } else {
            // Développeur voit uniquement les tests publiés
            return testRepository.findByStatut("PUBLIE");
        }
    }

    public Optional<Test> getTestById(Long testId) {
        return testRepository.findById(testId);
    }
    @Transactional
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
        return testRepository.findByAccesPublicTrueAndStatutAndDateExpirationIsNullOrDateExpirationAfter("PUBLIE", now);
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
    public Test publishTest(Long testId, Boolean accesRestreint, List<Long> developerIds) {
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

        if (!accesRestreint) { // Test privé
            if (developerIds == null || developerIds.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vous devez fournir au moins un développeur invité pour un test privé.");
            }
            invitationTestService.inviteDevelopers(testId, developerIds);

        }

        return publishedTest;
    }


    public boolean isTestCompleted(Long testId, Long developpeurId) {
        int totalQuestions = testQuestionRepository.countByTest_Id(testId);
        int answeredQuestions = developpeurResponseRepository.countByTest_IdAndDeveloppeur_Id(testId, developpeurId);
        return answeredQuestions == totalQuestions;
    }
}
