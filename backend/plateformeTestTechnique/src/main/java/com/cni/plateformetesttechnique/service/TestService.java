package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.dto.TestGenerationRequest;
import com.cni.plateformetesttechnique.model.*;
import com.cni.plateformetesttechnique.repository.*;

import com.cni.plateformetesttechnique.security.services.UserDetailsImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

public List<Question> getQuestionsForAutoGeneration(TestGenerationRequest request) {
    List<Question> allQuestions = questionRepository.findAll();

    // 1. Filtrer par technologies
    List<Question> filtered = allQuestions.stream()
            .filter(q -> q.getTechnologie() != null)
            .filter(q -> request.getTechnologies().stream()
                    .anyMatch(t -> t.equalsIgnoreCase(q.getTechnologie())))
            .collect(Collectors.toList());

    // 2. Sélection par type et niveau

    // QCM
    List<Question> qcmFacile = filtered.stream()
            .filter(q -> q.getType() == TypeQuestion.QCM && q.getNiveau() == NiveauQuestion.FACILE)
            .limit(request.getNbQcmFacile())
            .collect(Collectors.toList());

    List<Question> qcmMoyen = filtered.stream()
            .filter(q -> q.getType() == TypeQuestion.QCM && q.getNiveau() == NiveauQuestion.MOYEN)
            .limit(request.getNbQcmMoyen())
            .collect(Collectors.toList());

    List<Question> qcmDifficile = filtered.stream()
            .filter(q -> q.getType() == TypeQuestion.QCM && q.getNiveau() == NiveauQuestion.DIFFICILE)
            .limit(request.getNbQcmDifficile())
            .collect(Collectors.toList());

    // Code
    List<Question> codeFacile = filtered.stream()
            .filter(q -> q.getType() == TypeQuestion.Code && q.getNiveau() == NiveauQuestion.FACILE)
            .limit(request.getNbCodeFacile())
            .collect(Collectors.toList());

    List<Question> codeMoyen = filtered.stream()
            .filter(q -> q.getType() == TypeQuestion.Code && q.getNiveau() == NiveauQuestion.MOYEN)
            .limit(request.getNbCodeMoyen())
            .collect(Collectors.toList());

    List<Question> codeDifficile = filtered.stream()
            .filter(q -> q.getType() == TypeQuestion.Code && q.getNiveau() == NiveauQuestion.DIFFICILE)
            .limit(request.getNbCodeDifficile())
            .collect(Collectors.toList());
// Texte
    List<Question> texteFacile = filtered.stream()
            .filter(q -> q.getType() == TypeQuestion.Text && q.getNiveau() == NiveauQuestion.FACILE)
            .limit(request.getNbTexteFacile())
            .collect(Collectors.toList());

    List<Question> texteMoyen = filtered.stream()
            .filter(q -> q.getType() == TypeQuestion.Text && q.getNiveau() == NiveauQuestion.MOYEN)
            .limit(request.getNbTexteMoyen())
            .collect(Collectors.toList());

    List<Question> texteDifficile = filtered.stream()
            .filter(q -> q.getType() == TypeQuestion.Text && q.getNiveau() == NiveauQuestion.DIFFICILE)
            .limit(request.getNbTexteDifficile())
            .collect(Collectors.toList());

    // 3. Fusion finale
    List<Question> result = new ArrayList<>();
    result.addAll(qcmFacile);
    result.addAll(qcmMoyen);
    result.addAll(qcmDifficile);
    result.addAll(codeFacile);
    result.addAll(codeMoyen);
    result.addAll(codeDifficile);
    result.addAll(texteFacile);  // Ajoutez les questions de type Texte
    result.addAll(texteMoyen);   // Ajoutez les questions de type Texte
    result.addAll(texteDifficile);  // Ajoutez les questions de type Texte
    Collections.shuffle(result); // optionnel

    // 4. Vérification de la durée si spécifiée
    int dureeSpecifiee = request.getDuree();
    int dureeTotaleEstimee = result.stream()
            .mapToInt(Question::getTempsEstime)
            .sum();

    if (dureeSpecifiee > 0) {
        double marge = dureeTotaleEstimee * 0.6; // 10% de tolérance
        if (Math.abs(dureeSpecifiee - dureeTotaleEstimee) > marge) {
            throw new IllegalArgumentException("La durée spécifiée (" + dureeSpecifiee + " min) est trop différente de la durée estimée (" + dureeTotaleEstimee + " min).");
        }
    }

    // ✅ tout est bon
    return result;
}
    public Test createTestFromRequest(TestGenerationRequest request,  Long createurId) {
        User createur = userRepository.findById(createurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Test newTest = new Test(
                request.getTitre(),
                request.getDescription(),
                request.getDuree(),
                request.getNbQuestions(),
                request.getNiveauDifficulte(),
                request.getType(),
                request.getAccesPublic(),
                request.getLimiteTentatives(),
                "PUBLIE", // statut
                LocalDateTime.now(),
                request.getDateExpiration(),
                request.getTechnologies(),
                createur
        );


        // 2. Sauvegarder le test dans la base de données
        testRepository.save(newTest);

        // 3. Générer les questions pour ce test
        List<Question> questionsForTest = getQuestionsForAutoGeneration(request);

        // 4. Associer les questions au test avec des points et un ordre
        List<TestQuestion> testQuestions = new ArrayList<>();
        int ordre = 1;  // Initialisation de l'ordre des questions
        for (Question question : questionsForTest) {
            TestQuestion testQuestion = new TestQuestion();
            testQuestion.setTest(newTest);
            testQuestion.setQuestion(question);

            // 💡 Définir les points dynamiquement selon le type et le niveau
            int basePoints = (question.getType() == TypeQuestion.Code) ? 5 : 3;

            int bonus = switch (question.getNiveau()) {
                case FACILE -> 0;
                case MOYEN -> 2;
                case DIFFICILE -> 5;
                default -> 1;
            };

            testQuestion.setPoints(basePoints + bonus);

            testQuestion.setOrdre(ordre++);
            testQuestions.add(testQuestion);
        }

//        for (Question question : questionsForTest) {
//            TestQuestion testQuestion = new TestQuestion();
//            testQuestion.setTest(newTest);
//            testQuestion.setQuestion(question);
//            testQuestion.setPoints(request.getPointsParQuestion());  // Définir les points attribués à chaque question
//            testQuestion.setOrdre(ordre++);  // Assigner un ordre
//            testQuestions.add(testQuestion);
//        }

        // 5. Sauvegarder les associations des questions avec le test
        testQuestionRepository.saveAll(testQuestions);

        // 6. Retourner le test créé
        return newTest;
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


//    public boolean isTestCompleted(Long testId, Long developpeurId) {
//        int totalQuestions = testQuestionRepository.countByTest_Id(testId);
//        int answeredQuestions = developpeurResponseRepository.countByTest_IdAndDeveloppeur_Id(testId, developpeurId);
//        return answeredQuestions == totalQuestions;
//    }
    public List<Test> getTestsPubliesByUserId(Long userId) {
        return testRepository.findByCreateur_IdAndStatut(userId, "PUBLIE");
    }
    public List<Test> getTestsPubliesDeAdmin() {
        Long adminId = 6L;
        return testRepository.findByCreateur_IdAndStatut(adminId, "PUBLIE");
    }
    public List<Test> getTestsPubliesDuChefDuDev(String emailDev) {
        System.out.println(">>> Début de la méthode getTestsPubliesDuChefDuDev");
        System.out.println("Email du développeur : " + emailDev);

        Developpeur developpeur = developpeurRepository.findByEmail(emailDev)
                .orElseThrow(() -> {
                    System.out.println("!!! Développeur introuvable pour l'email : " + emailDev);
                    return new RuntimeException("Développeur introuvable");
                });

        System.out.println("Développeur trouvé : " + developpeur.getUsername() + " (id=" + developpeur.getId() + ")");

        ChefDeProjet chef = developpeur.getChefDeProjet();
        if (chef == null) {
            System.out.println("!!! Aucun chef de projet associé au développeur.");
            return new ArrayList<>();
        }

        System.out.println("Chef de projet trouvé : " + chef.getUsername() + " (id=" + chef.getId() + ")");

        List<Test> tests = testRepository.findByCreateur_IdAndStatut(chef.getId(), "PUBLIE");
        System.out.println("Nombre de tests publiés trouvés : " + tests.size());

        for (Test test : tests) {
            System.out.println(" - Test : " + test.getTitre() + " (id=" + test.getId() + ")");
        }

        return tests;
    }

    //    public List<Test> getTestsPubliesDuChefDuDev(String emailDev) {
//        Developpeur developpeur = developpeurRepository.findByEmail(emailDev)
//                .orElseThrow(() -> new RuntimeException("Développeur introuvable"));
//
//        ChefDeProjet chef = developpeur.getChefDeProjet();
//        if (chef == null) {
//            return new ArrayList<>(); // ou une exception si c'est obligatoire
//        }
//
//        return testRepository.findByCreateur_IdAndStatut(chef.getId(), "PUBLIE");
//    }
    public boolean deleteTest(Long testId) {
        Optional<Test> testOpt = testRepository.findById(testId);
        if (testOpt.isPresent()) {
            Test test = testOpt.get();
            // Si tu veux vérifier si le test est encore en brouillon avant de le supprimer
            if ("BROUILLON".equals(test.getStatut())) {
                testRepository.delete(test);
                return true;
            }
    }
        return false; // ← Ajout du return manquant

    }


    public List<Test> getTestsSuggeresPourDeveloppeur(String emailDev, String technologie, String niveauDifficulte, boolean isNext) {
        System.out.println("Début de getTestsSuggeresPourDeveloppeur");
        System.out.println("emailDev = " + emailDev);
        System.out.println("technologie = " + technologie);
        System.out.println("niveauDifficulte = " + niveauDifficulte);
        System.out.println("isNext = " + isNext);

        List<Test> suggestions = new ArrayList<>();

        // 1. Récupérer le développeur
        Developpeur developpeur = developpeurRepository.findByEmail(emailDev)
                .orElseThrow(() -> {
                    System.out.println("Développeur introuvable pour email : " + emailDev);
                    return new RuntimeException("Développeur introuvable");
                });
        System.out.println("Développeur trouvé : " + developpeur.getEmail());

        ChefDeProjet chef = developpeur.getChefDeProjet();
        if (chef == null) {
            System.out.println("Aucun chef de projet associé au développeur");
            throw new RuntimeException("Aucun chef de projet associé au développeur.");
        }
        System.out.println("Chef de projet id : " + chef.getId());

        Long chefId = chef.getId();
        Long adminId = 6L;

        // 2. Définir les niveaux dans l’ordre croissant (en respectant la casse)
        List<String> niveaux = List.of("Facile", "Intermédiaire", "Difficile");
        int currentIndex = niveaux.indexOf(niveauDifficulte);
        System.out.println("Index niveau actuel = " + currentIndex);

        if (currentIndex == -1) {
            System.out.println("Niveau actuel invalide : " + niveauDifficulte);
            throw new RuntimeException("Niveau actuel invalide : " + niveauDifficulte);
        }

        // 3. Chercher tous les tests PUBLIE créés par le chef ou l’admin
        List<Test> tousTests = testRepository.findTestsPubliesByCreateurIds(List.of(chefId, adminId));
        System.out.println("Nombre de tests publiés trouvés : " + tousTests.size());

        // 4. Filtrer par technologie et niveau selon isNext
        for (Test test : tousTests) {
            System.out.println("Test id=" + test.getId() + " niveau=" + test.getNiveauDifficulte() + " technologies=" + test.getTechnologies());
            if (test.getTechnologies().contains(technologie)) {
                int testNiveauIndex = niveaux.indexOf(test.getNiveauDifficulte());
                if (testNiveauIndex == -1) {
                    System.out.println("Test ignoré niveau invalide : " + test.getNiveauDifficulte());
                    continue; // Ignorer tests avec niveau invalide
                }

                if (isNext) {
                    if (testNiveauIndex >= currentIndex) {
                        System.out.println("Ajout test (isNext=true) id=" + test.getId());
                        suggestions.add(test);
                    }
                } else {
                    if (testNiveauIndex == currentIndex) {
                        System.out.println("Ajout test (isNext=false) id=" + test.getId());
                        suggestions.add(test);
                    }
                }
            } else {
                System.out.println("Test ignoré technologie non correspondante");
            }
        }

        System.out.println("Nombre de tests suggérés : " + suggestions.size());
        return suggestions;
    }


//    public List<Test> getTestsSuggeresPourDeveloppeur(String emailDev, String technologie, String niveauActuel) {
//        List<Test> suggestions = new ArrayList<>();
//
//        // 1. Récupérer le développeur
//        Developpeur developpeur = developpeurRepository.findByEmail(emailDev)
//                .orElseThrow(() -> new RuntimeException("Développeur introuvable"));
//
//        ChefDeProjet chef = developpeur.getChefDeProjet();
//        if (chef == null) {
//            throw new RuntimeException("Aucun chef de projet associé au développeur.");
//        }
//
//        Long chefId = chef.getId();
//        Long adminId = 6L;
//
//        // 2. Définir les niveaux dans l’ordre croissant
//        List<String> niveaux = List.of("debutant", "intermediaire", "difficile");
//        int currentIndex = niveaux.indexOf(niveauActuel.toLowerCase());
//
//        if (currentIndex == -1) {
//            throw new RuntimeException("Niveau actuel invalide : " + niveauActuel);
//        }
//
//        // 3. Chercher tous les tests PUBLIE créés par le chef ou l’admin
//        List<Test> tousTests = testRepository.findTestsPubliesByCreateurIds(List.of(chefId, adminId));
//
//        // 4. Filtrer par technologie et niveau >= niveau actuel
//        for (Test test : tousTests) {
//            if (
//                    test.getTechnologies().contains(technologie)
//                            && niveaux.indexOf(test.getNiveauDifficulte().toLowerCase()) >= currentIndex
//            ) {
//                suggestions.add(test);
//            }
//        }
//
//        return suggestions;
//    }

    public long getTestCountByNiveau(String niveau) {
        return testRepository.countByNiveauDifficulte(niveau.toUpperCase().trim());
    }


}
