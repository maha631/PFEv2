//package com.cni.plateformetesttechnique.dto;
//
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotNull;
//import java.util.List;
//
//public class TestGenerationRequest {
//
//    @NotNull(message = "La liste des technologies ne peut pas être vide.")
//    private List<String> technologies;
//
//    @Min(value = 1, message = "La durée du test doit être d'au moins 1 minute.")
//    private int duree;
//
//    // QCM
//    private int nbQcmFacile;
//    private int nbQcmMoyen;
//    private int nbQcmDifficile;
//
//    // Code
//    private int nbCodeFacile;
//    private int nbCodeMoyen;
//    private int nbCodeDifficile;
//
//    // Getters et Setters
//
//    public List<String> getTechnologies() {
//        return technologies;
//    }
//
//    public void setTechnologies(List<String> technologies) {
//        this.technologies = technologies;
//    }
//
//    public int getDuree() {
//        return duree;
//    }
//
//    public void setDuree(int duree) {
//        this.duree = duree;
//    }
//
//    public int getNbQcmFacile() {
//        return nbQcmFacile;
//    }
//
//    public void setNbQcmFacile(int nbQcmFacile) {
//        this.nbQcmFacile = nbQcmFacile;
//    }
//
//    public int getNbQcmMoyen() {
//        return nbQcmMoyen;
//    }
//
//    public void setNbQcmMoyen(int nbQcmMoyen) {
//        this.nbQcmMoyen = nbQcmMoyen;
//    }
//
//    public int getNbQcmDifficile() {
//        return nbQcmDifficile;
//    }
//
//    public void setNbQcmDifficile(int nbQcmDifficile) {
//        this.nbQcmDifficile = nbQcmDifficile;
//    }
//
//    public int getNbCodeFacile() {
//        return nbCodeFacile;
//    }
//
//    public void setNbCodeFacile(int nbCodeFacile) {
//        this.nbCodeFacile = nbCodeFacile;
//    }
//
//    public int getNbCodeMoyen() {
//        return nbCodeMoyen;
//    }
//
//    public void setNbCodeMoyen(int nbCodeMoyen) {
//        this.nbCodeMoyen = nbCodeMoyen;
//    }
//
//    public int getNbCodeDifficile() {
//        return nbCodeDifficile;
//    }
//
//    public void setNbCodeDifficile(int nbCodeDifficile) {
//        this.nbCodeDifficile = nbCodeDifficile;
//    }
//}
package com.cni.plateformetesttechnique.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.time.LocalDateTime;

public class TestGenerationRequest {

    @NotNull(message = "La liste des technologies ne peut pas être vide.")
    private List<String> technologies;

    @Min(value = 1, message = "La durée du test doit être d'au moins 1 minute.")
    private int duree;

    // QCM
    private int nbQcmFacile;
    private int nbQcmMoyen;
    private int nbQcmDifficile;

    // Code
    private int nbCodeFacile;
    private int nbCodeMoyen;
    private int nbCodeDifficile;
    private int nbTexteFacile;
    private int nbTexteMoyen;
    private int nbTexteDifficile;
    // Propriétés manquantes
    private String titre;
    private String description;
    private int nbQuestions;
    private int pointsParQuestion;
    private LocalDateTime dateExpiration;
    private String niveauDifficulte;
    private String type;  // QCM, Algo, Mixte

    private boolean accesPublic;
    private int limiteTentatives;

    // Getters et Setters

    public List<String> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<String> technologies) {
        this.technologies = technologies;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public int getNbQcmFacile() {
        return nbQcmFacile;
    }

    public void setNbQcmFacile(int nbQcmFacile) {
        this.nbQcmFacile = nbQcmFacile;
    }

    public int getNbQcmMoyen() {
        return nbQcmMoyen;
    }

    public void setNbQcmMoyen(int nbQcmMoyen) {
        this.nbQcmMoyen = nbQcmMoyen;
    }

    public int getNbQcmDifficile() {
        return nbQcmDifficile;
    }

    public void setNbQcmDifficile(int nbQcmDifficile) {
        this.nbQcmDifficile = nbQcmDifficile;
    }

    public int getNbCodeFacile() {
        return nbCodeFacile;
    }

    public void setNbCodeFacile(int nbCodeFacile) {
        this.nbCodeFacile = nbCodeFacile;
    }

    public int getNbCodeMoyen() {
        return nbCodeMoyen;
    }

    public void setNbCodeMoyen(int nbCodeMoyen) {
        this.nbCodeMoyen = nbCodeMoyen;
    }

    public int getNbCodeDifficile() {
        return nbCodeDifficile;
    }

    public void setNbCodeDifficile(int nbCodeDifficile) {
        this.nbCodeDifficile = nbCodeDifficile;
    }
    public int getNbTexteFacile() {
        return nbTexteFacile;
    }

    public void setNbTexteFacile(int nbTexteFacile) {
        this.nbTexteFacile = nbTexteFacile;
    }

    public int getNbTexteMoyen() {
        return nbTexteMoyen;
    }

    public void setNbTexteMoyen(int nbTexteMoyen) {
        this.nbTexteMoyen = nbTexteMoyen;
    }

    public int getNbTexteDifficile() {
        return nbTexteDifficile;
    }

    public void setNbTexteDifficile(int nbTexteDifficile) {
        this.nbTexteDifficile = nbTexteDifficile;
    }
    // Méthodes manquantes
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNbQuestions() {
        return nbQuestions;
    }

    public void setNbQuestions(int nbQuestions) {
        this.nbQuestions = nbQuestions;
    }

    public int getPointsParQuestion() {
        return pointsParQuestion;
    }

    public void setPointsParQuestion(int pointsParQuestion) {
        this.pointsParQuestion = pointsParQuestion;
    }

    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getNiveauDifficulte() {
        return niveauDifficulte;
    }

    public void setNiveauDifficulte(String niveauDifficulte) {
        this.niveauDifficulte = niveauDifficulte;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getAccesPublic() {
        return accesPublic;
    }

    public void setAccesPublic(boolean accesPublic) {
        this.accesPublic = accesPublic;
    }

    public int getLimiteTentatives() {
        return limiteTentatives;
    }

    public void setLimiteTentatives(int limiteTentatives) {
        this.limiteTentatives = limiteTentatives;
    }
}
