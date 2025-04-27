package com.cni.plateformetesttechnique.dto;


public class CodeAnalysisRequest {
    private String question;
    private String userCode;

    // Getters et setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
