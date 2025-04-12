package com.cni.plateformetesttechnique.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ChefDeProjet extends User {  

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
    private String specialite;
    private Double score;
  
    @JsonIgnore 
    @OneToMany(mappedBy = "chefDeProjet", cascade = CascadeType.ALL)
    @JsonManagedReference

    private List<Developpeur> developpeurs; 
   

    public ChefDeProjet() {
        super();
    }

    public ChefDeProjet(@NotBlank @Size(max = 20) String username, 
            @NotBlank @Size(max = 50) @Email String email,
            @NotBlank @Size(max = 120) String password, 
            String specialite, 
            Double score) {
    super(username, email, password);  // Initialisation du parent avec les valeurs de username, email, password
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
    
}
