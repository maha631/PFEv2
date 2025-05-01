package com.cni.plateformetesttechnique.model;


public class AdministrateurDTO {
    private Long id;
    private String username;
    private String email;
    private String grade;
    private String image;
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
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}


}
