package com.cni.plateformetesttechnique.model;


public enum Niveau {
    DEBUTANT,
    DEVELOPPEUR,
    SENIOR;

@Override
public String toString() {
    return switch (this) {
        case DEBUTANT -> "Expert Junior";
        case DEVELOPPEUR -> "Développeur Confirmé";
        case SENIOR -> "Expert Senior";
    };
}}