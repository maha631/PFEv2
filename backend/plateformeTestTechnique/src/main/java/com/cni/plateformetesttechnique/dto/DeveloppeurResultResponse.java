package com.cni.plateformetesttechnique.dto;
public class DeveloppeurResultResponse {
    private Long id;

    private String nom;
    private String email;
    private double score;
    private String tentative;
    private String temps;
    private String etat;
    private String badge;
    private String apparence;

    // Constructeur complet
    public DeveloppeurResultResponse(String nom, String email, double score, String tentative,
                                     String temps, String etat, String badge, String apparence) {
        this.nom = nom;
        this.email = email;
        this.score = score;
        this.tentative = tentative;
        this.temps = temps;
        this.etat = etat;
        this.badge = badge;
        this.apparence = apparence;
    }

    // Constructeur vide (nécessaire pour certaines bibliothèques comme Jackson)
    public DeveloppeurResultResponse() {}
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getTentative() {
        return tentative;
    }

    public void setTentative(String tentative) {
        this.tentative = tentative;
    }

    public String getTemps() {
        return temps;
    }

    public void setTemps(String temps) {
        this.temps = temps;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getApparence() {
        return apparence;
    }

    public void setApparence(String apparence) {
        this.apparence = apparence;
    }
}
