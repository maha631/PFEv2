package com.cni.plateformetesttechnique.dto;

import java.util.List;

public class PublishTestRequest {
    private Boolean accesRestreint;
    private List<Long> developerIds;

    // Getters et Setters
    public Boolean getAccesRestreint() {
        return accesRestreint;
    }

    public void setAccesRestreint(Boolean accesRestreint) {
        this.accesRestreint = accesRestreint;
    }

    public List<Long> getDeveloperIds() {
        return developerIds;
    }

    public void setDeveloperIds(List<Long> developerIds) {
        this.developerIds = developerIds;
    }
}
