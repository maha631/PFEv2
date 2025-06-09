package com.cni.plateformetesttechnique.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Niveau niveau;

    private LocalDate dateObtention;

    @ManyToOne
    private Developpeur developpeur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Niveau getNiveau() {
        return niveau;
    }

    public void setNiveau(Niveau libelleNiveau) {
        this.niveau = libelleNiveau;
    }

    public LocalDate getDateObtention() {
        return dateObtention;
    }

    public void setDateObtention(LocalDate dateObtention) {
        this.dateObtention = dateObtention;
    }

    public Developpeur getDeveloppeur() {
        return developpeur;
    }

    public void setDeveloppeur(Developpeur developpeur) {
        this.developpeur = developpeur;
    }

    public Certification(Long id, Niveau niveau, LocalDate dateObtention, Developpeur developpeur) {
        super();
        this.id = id;
        this.niveau = niveau;
        this.dateObtention = dateObtention;
        this.developpeur = developpeur;
    }

    public Certification() {
        super();
    }

}