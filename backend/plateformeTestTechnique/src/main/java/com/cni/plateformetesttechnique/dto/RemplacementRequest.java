package com.cni.plateformetesttechnique.dto;

import com.cni.plateformetesttechnique.model.NiveauQuestion;
import com.cni.plateformetesttechnique.model.TypeQuestion;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class RemplacementRequest {
    private Long id;

    private TypeQuestion type;
    @Enumerated(EnumType.STRING)
    private NiveauQuestion niveau;
    private String technologie;
    public Long getId() {
        return id;
    }

    public TypeQuestion getType() {
        return type;
    }

    public NiveauQuestion getNiveau() {
        return niveau;
    }

    public void setNiveau(NiveauQuestion niveau) {
        this.niveau = niveau;
    }
    public String getTechnologie() {
        return technologie;
    }

    public void setTechnologie(String technologie) {
        this.technologie = technologie;
    }

}
