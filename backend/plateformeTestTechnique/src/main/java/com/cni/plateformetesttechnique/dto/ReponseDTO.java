package com.cni.plateformetesttechnique.dto;

import java.util.List;

public class ReponseDTO {
    private Long testId;
    private Long questionId;
    private List<Long> selectedOptionIds;

    // Getters et Setters
    public Long getTestId() { return testId; }
    public void setTestId(Long testId) { this.testId = testId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public List<Long> getSelectedOptionIds() { return selectedOptionIds; }
    public void setSelectedOptionIds(List<Long> selectedOptionIds) { this.selectedOptionIds = selectedOptionIds; }

    }
