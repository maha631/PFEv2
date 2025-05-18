package com.cni.plateformetesttechnique.model;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class DeveloppeurTestScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "developpeur_id", nullable = false)
    private Developpeur developpeur;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;
    private int attemptNumber; // Numéro de la tentative
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;


    private Double score;  // Score final du développeur pour ce test

    public DeveloppeurTestScore() {}

    public DeveloppeurTestScore(Developpeur developpeur, Test test, Double score) {
        this.developpeur = developpeur;
        this.test = test;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Developpeur getDeveloppeur() {
        return developpeur;
    }

    public void setDeveloppeur(Developpeur developpeur) {
        this.developpeur = developpeur;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }
    public int getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
    @Transient // Optionnel si tu veux calculer la durée
    public Duration getDuree() {
        if (createdAt != null && finishedAt != null) {
            return Duration.between(createdAt, finishedAt);
        }
        return Duration.ZERO;
    }
    @Override
    public String toString() {
        return "DeveloppeurTestScore{" +
                "id=" + id +
                ", developpeur=" + developpeur +
                ", test=" + test +
                ", attemptNumber=" + attemptNumber +
                ", score=" + score +
                '}';
    }

}
