//package com.cni.plateformetesttechnique.model;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import jakarta.persistence.*;
//
//import java.util.List;
//
//@Entity
//public class DeveloppeurResponse {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "test_id", nullable = false)
//    @JsonIgnore
//    private Test test; // Le test auquel le développeur répond
//
//    @ManyToOne
//    @JoinColumn(name = "question_id", nullable = false)
//    @JsonIgnoreProperties({"developpeurResponses"})
//    private Question question; // La question à laquelle le développeur répond
//
//    @ManyToMany
//    @JoinTable(
//            name = "Developpeur_answer_options",
//            joinColumns = @JoinColumn(name = "Developpeur_response_id"),
//            inverseJoinColumns = @JoinColumn(name = "answer_option_id")
//    )
//    private List<AnswerOption> selectedAnswerOptions; // Options sélectionnées par le développeur
//
//    private Boolean isCorrect; // True si toutes les options sélectionnées sont correctes
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "developpeur_id", nullable = false)
//    @JsonIgnore
//    private Developpeur developpeur; // Le développeur qui a répondu à la question
//
//    private String reponseLibre; // La réponse libre donnée par le développeur (si applicable)
//
//    private Integer note; // La note attribuée par Gemini (de 0 à 10)
//    @Lob
//    private String explication; // Explication donnée par Gemini pour la correction
//    @Lob
//    private String feedback; // Feedback donné par Gemini pour s'améliorer
//    @Lob
//
//    private String reponseCorrecte; // La réponse correcte suggérée par Gemini
//
//    // Constructeur par défaut
//    public DeveloppeurResponse() {}
//
//    // Constructeur avec paramètres
//    public DeveloppeurResponse(Test test, Question question, List<AnswerOption> selectedAnswerOptions, Boolean isCorrect, Developpeur developpeur) {
//        this.test = test;
//        this.question = question;
//        this.selectedAnswerOptions = selectedAnswerOptions;
//        this.isCorrect = isCorrect;
//        this.developpeur = developpeur;
//    }
//    public DeveloppeurResponse(Test test, Question question, Boolean isCorrect, Developpeur developpeur, String reponseLibre, Integer note, String explication, String feedback, String reponseCorrecte) {
//        this.test = test;
//        this.question = question;
//        this.isCorrect = isCorrect;
//        this.developpeur = developpeur;
//        this.reponseLibre = reponseLibre;
//        this.note = note;
//        this.explication = explication;
//        this.feedback = feedback;
//        this.reponseCorrecte = reponseCorrecte;
//    }
//
//    // Getters et Setters
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Test getTest() {
//        return test;
//    }
//
//    public void setTest(Test test) {
//        this.test = test;
//    }
//
//    public Question getQuestion() {
//        return question;
//    }
//
//    public void setQuestion(Question question) {
//        this.question = question;
//    }
//
//    public List<AnswerOption> getSelectedAnswerOptions() {
//        return selectedAnswerOptions;
//    }
//
//    public void setSelectedAnswerOptions(List<AnswerOption> selectedAnswerOptions) {
//        this.selectedAnswerOptions = selectedAnswerOptions;
//    }
//
//    public Boolean getIsCorrect() {
//        return isCorrect;
//    }
//
//    public void setIsCorrect(Boolean isCorrect) {
//        this.isCorrect = isCorrect;
//    }
//
//    public Developpeur getDeveloppeur() {
//        return developpeur;
//    }
//
//    public void setDeveloppeur(Developpeur developpeur) {
//        this.developpeur = developpeur;
//    }
//
//    public String getReponseLibre() {
//        return reponseLibre;
//    }
//
//    public void setReponseLibre(String reponseLibre) {
//        this.reponseLibre = reponseLibre;
//    }
//
//    public Integer getNote() {
//        return note;
//    }
//
//    public void setNote(Integer note) {
//        this.note = note;
//    }
//
//    public String getExplication() {
//        return explication;
//    }
//
//    public void setExplication(String explication) {
//        this.explication = explication;
//    }
//
//    public String getFeedback() {
//        return feedback;
//    }
//
//    public void setFeedback(String feedback) {
//        this.feedback = feedback;
//    }
//
//    public String getReponseCorrecte() {
//        return reponseCorrecte;
//    }
//
//    public void setReponseCorrecte(String reponseCorrecte) {
//        this.reponseCorrecte = reponseCorrecte;
//    }
//}
package com.cni.plateformetesttechnique.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class DeveloppeurResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Plus de lien direct avec Test
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developpeur_test_score_id", nullable = false)
    @JsonIgnore
    private DeveloppeurTestScore developpeurTestScore;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnoreProperties({"developpeurResponses"})
    private Question question;

    @ManyToMany
    @JoinTable(
            name = "Developpeur_answer_options",
            joinColumns = @JoinColumn(name = "Developpeur_response_id"),
            inverseJoinColumns = @JoinColumn(name = "answer_option_id")
    )
    private List<AnswerOption> selectedAnswerOptions;

    private Boolean isCorrect;


    @Lob
    private String reponseLibre;

    private Integer note;

    @Lob
    private String explication;

    @Lob
    private String feedback;

    @Lob
    private String reponseCorrecte;

    public DeveloppeurResponse() {}

    // QCM
    public DeveloppeurResponse(Question question, List<AnswerOption> selectedAnswerOptions, Boolean isCorrect) {
        this.question = question;
        this.selectedAnswerOptions = selectedAnswerOptions;
        this.isCorrect = isCorrect;
    }

    // Réponse libre (code)
    public DeveloppeurResponse(Question question, Boolean isCorrect, String reponseLibre, Integer note, String explication, String feedback, String reponseCorrecte) {
        this.question = question;
        this.isCorrect = isCorrect;
        this.reponseLibre = reponseLibre;
        this.note = note;
        this.explication = explication;
        this.feedback = feedback;
        this.reponseCorrecte = reponseCorrecte;
    }

    // Getters et Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) { this.question = question; }

    public List<AnswerOption> getSelectedAnswerOptions() { return selectedAnswerOptions; }

    public void setSelectedAnswerOptions(List<AnswerOption> selectedAnswerOptions) {
        this.selectedAnswerOptions = selectedAnswerOptions;
    }

    public Boolean getIsCorrect() { return isCorrect; }

    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }

//    public Developpeur getDeveloppeur() { return developpeur; }
//
//    public void setDeveloppeur(Developpeur developpeur) { this.developpeur = developpeur; }

    public String getReponseLibre() { return reponseLibre; }

    public void setReponseLibre(String reponseLibre) { this.reponseLibre = reponseLibre; }

    public Integer getNote() { return note; }

    public void setNote(Integer note) { this.note = note; }

    public String getExplication() { return explication; }

    public void setExplication(String explication) { this.explication = explication; }

    public String getFeedback() { return feedback; }

    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getReponseCorrecte() { return reponseCorrecte; }

    public void setReponseCorrecte(String reponseCorrecte) { this.reponseCorrecte = reponseCorrecte; }

    public DeveloppeurTestScore getDeveloppeurTestScore() { return developpeurTestScore; }

    public void setDeveloppeurTestScore(DeveloppeurTestScore developpeurTestScore) {
        this.developpeurTestScore = developpeurTestScore;
    }
}
