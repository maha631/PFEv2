package com.cni.plateformetesttechnique.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class UpdateDeveloppeurRequest {

    private String oldPassword;
    private String newPassword;
    private String username;
    private String email;
    private MultipartFile image;
    private String specialite;
    private Double score;
    private List<String> technologies;
    private Integer experience;
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
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
	public MultipartFile getImage() {
		return image;
	}
	public void setImage(MultipartFile image) {
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
	public Integer getExperience() {
		return experience;
	}
	public void setExperience(Integer experience) {
		this.experience = experience;
	}
    


   
}
