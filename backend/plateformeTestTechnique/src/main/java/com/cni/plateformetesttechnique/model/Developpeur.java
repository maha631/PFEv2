package com.cni.plateformetesttechnique.model;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Developpeur extends User {

    private String specialite;
	private Double score;  // Score total du développeur


	@ElementCollection
    @CollectionTable(name = "developpeur_technologies", joinColumns = @JoinColumn(name = "developpeur_id"))
    @Column(name = "technologie")
    private List<String> technologies;

    private int experience;

	@OneToMany(mappedBy = "developpeur", cascade = CascadeType.ALL)
	@JsonIgnore

	private List<InvitationTest> invitations;
	
	
	@ManyToOne
    @JoinColumn(name = "chefDeProjet_id")
	@JsonBackReference

	private ChefDeProjet chefDeProjet;
	
	private boolean isAssigned = false;  

	
	public Developpeur() {
		super(); // ✅ Appelle le constructeur de `User`
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
	public List<String> getTechnologies() {
		return technologies;
	}

	public void setTechnologies(List<String> technologies) {
		this.technologies = technologies;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public Developpeur(String specialite, Double score, List<String> technologies, int experience,
			 List<InvitationTest> invitations) {
		super();
		this.specialite = specialite;
		this.score = score;
		this.technologies = technologies;
		this.experience = experience;
//		this.developpeurResponses = developpeurResponses;
		this.invitations = invitations;
	}

//	public List<DeveloppeurResponse> getDeveloppeurResponses() {
//		return developpeurResponses;
//	}
//
//	public void setDeveloppeurResponses(List<DeveloppeurResponse> developpeurResponses) {
//		this.developpeurResponses = developpeurResponses;
//	}

	public List<InvitationTest> getInvitations() {
		return invitations;
	}

	public void setInvitations(List<InvitationTest> invitations) {
		this.invitations = invitations;
	}
	@JsonIgnore
	public ChefDeProjet getChefDeProjet() {
		return chefDeProjet;
	}

	public void setChefDeProjet(ChefDeProjet chefDeProjet) {
		this.chefDeProjet = chefDeProjet;
	}

	public boolean isAssigned() {
		return isAssigned;
	}

	public void setAssigned(boolean isAssigned) {
		this.isAssigned = isAssigned;
	}
	

	

    
}