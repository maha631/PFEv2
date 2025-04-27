package com.cni.plateformetesttechnique.model;

public class EvaluationResult {
    private int note;
    private Boolean isCorrecte; // 1 = correcte, 0 = incorrecte
    private String explication;
    private String reponseCorrecte;
    private String feedback;

    // Getters et Setters

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public Boolean getIsCorrecte() {
        return isCorrecte;
    }

    public void setIsCorrecte(Boolean isCorrecte) {
        this.isCorrecte = isCorrecte;
    }

    public String getExplication() {
        return explication;
    }

    public void setExplication(String explication) {
        this.explication = explication;
    }

    public String getReponseCorrecte() {
        return reponseCorrecte;
    }

    public void setReponseCorrecte(String reponseCorrecte) {
        this.reponseCorrecte = reponseCorrecte;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    @Override
    public String toString() {
        return "EvaluationResult{" +
                "note=" + note +
                ", isCorrecte=" + isCorrecte +
                ", explication='" + explication + '\'' +
                ", reponseCorrecte='" + reponseCorrecte + '\'' +
                ", feedback='" + feedback + '\'' +
                '}';
    }

}
