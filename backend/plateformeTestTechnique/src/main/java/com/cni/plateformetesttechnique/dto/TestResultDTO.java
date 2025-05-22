package com.cni.plateformetesttechnique.dto;


public class TestResultDTO {
    private String testTitle;
    private Double score;
    private String datePassed;
	public String getTestTitle() {
		return testTitle;
	}
	public void setTestTitle(String testTitle) {
		this.testTitle = testTitle;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	public String getDatePassed() {
		return datePassed;
	}
	public void setDatePassed(String datePassed) {
		this.datePassed = datePassed;
	}

   
}
