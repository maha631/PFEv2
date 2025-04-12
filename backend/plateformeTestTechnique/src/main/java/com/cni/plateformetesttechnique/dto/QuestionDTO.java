package com.cni.plateformetesttechnique.dto;

import com.cni.plateformetesttechnique.model.Question;

public class QuestionDTO {
    private Question question;
    private int points;
    private int ordre;

    public QuestionDTO(Question question, int points, int ordre) {
        this.question = question;
        this.points = points;
        this.ordre = ordre;
    }

    // Getters et Setters
    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }
}

