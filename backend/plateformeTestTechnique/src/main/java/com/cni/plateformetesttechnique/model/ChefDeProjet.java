package com.cni.plateformetesttechnique.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class ChefDeProjet extends User {  

    private String specialite;
    private Double score; 
    
    @OneToMany(mappedBy = "chefDeProjet", cascade = CascadeType.ALL)
    private List<Developpeur> developpeurs; 


    public ChefDeProjet() {
        super();
    }

    public ChefDeProjet(String specialite, Double score) {
        super();
        this.specialite = specialite;
        this.score = score;
       
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

  
	public List<Developpeur> getDeveloppeurs() {
		return developpeurs;
	}

	public void setDeveloppeurs(List<Developpeur> developpeurs) {
		this.developpeurs = developpeurs;
	}
    
}
