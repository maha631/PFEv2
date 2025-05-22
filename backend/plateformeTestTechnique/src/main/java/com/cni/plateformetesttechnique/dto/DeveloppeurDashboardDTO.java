package com.cni.plateformetesttechnique.dto;

import java.util.List;

public class DeveloppeurDashboardDTO {

    private String nomDeveloppeur;
    private String chefEquipe;
    private double scoreGlobal;
    private List<TestStatDTO> tests;

    public String getNomDeveloppeur() {
        return nomDeveloppeur;
    }

    public void setNomDeveloppeur(String nomDeveloppeur) {
        this.nomDeveloppeur = nomDeveloppeur;
    }

    public String getChefEquipe() {
        return chefEquipe;
    }

    public void setChefEquipe(String chefEquipe) {
        this.chefEquipe = chefEquipe;
    }

    public double getScoreGlobal() {
        return scoreGlobal;
    }

    public void setScoreGlobal(double scoreGlobal) {
        this.scoreGlobal = scoreGlobal;
    }

	public List<TestStatDTO> getTests() {
		return tests;
	}

	public void setTests(List<TestStatDTO> tests) {
		this.tests = tests;
	}

    
}
