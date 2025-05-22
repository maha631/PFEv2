package com.cni.plateformetesttechnique.model;

import jakarta.persistence.Entity;

@Entity
public class CodeQuestion extends Question {

    private String language;  // Le langage de programmation utilisé pour cette question

    // Constructeur, getters, setters
    public CodeQuestion() {
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
