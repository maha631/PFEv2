package com.cni.plateformetesttechnique.dto;

import java.util.List;

public class DeveloppeurDTO {
    private Long id;
    private String username;
    private String email;
    private String image;
    private String specialite;
    private Double score;
    private List<String> technologies;
    private int experience;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
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

    
}
