package com.cni.plateformetesttechnique.dto;

public class PasswordResetRequest {
	 private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public PasswordResetRequest(String email) {
		super();
		this.email = email;
	}
	public PasswordResetRequest() {}
	 
}
