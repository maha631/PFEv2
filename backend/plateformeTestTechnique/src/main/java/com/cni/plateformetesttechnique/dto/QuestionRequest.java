package com.cni.plateformetesttechnique.dto;

import com.cni.plateformetesttechnique.model.NiveauQuestion;
import com.cni.plateformetesttechnique.model.TypeQuestion;

import java.util.List;

public class QuestionRequest {
    private String enonce;
    private NiveauQuestion niveau;
    private TypeQuestion type;

    private List<AnswerOptionRequest> answerOptions;
    private String language;
    private String technologie; // ex: Java, Python
    private int tempsEstime;    // en minutes
    public String getEnonce() {
        return enonce;
    }

    public void setEnonce(String enonce) {
        this.enonce = enonce;
    }

    public NiveauQuestion getNiveau() {
        return niveau;
    }

    public void setNiveau(NiveauQuestion niveau) {
        this.niveau = niveau;
    }
    public String getTechnologie() {
        return technologie;
    }

    public void setTechnologie(String technologie) {
        this.technologie = technologie;
    }

    public int getTempsEstime() {
        return tempsEstime;
    }

    public void setTempsEstime(int tempsEstime) {
        this.tempsEstime = tempsEstime;
    }
    public TypeQuestion getType() {
        return type;
    }

    public void setType(TypeQuestion type) {
        this.type = type;
    }

    public List<AnswerOptionRequest> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<AnswerOptionRequest> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
