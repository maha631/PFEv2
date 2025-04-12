package com.cni.plateformetesttechnique.dto;

public class PasswordResetRequest {

    private String email;

    // Constructeurs
    public PasswordResetRequest() {}

    public PasswordResetRequest(String email) {
        this.email = email;
    }

    // Getters et Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
