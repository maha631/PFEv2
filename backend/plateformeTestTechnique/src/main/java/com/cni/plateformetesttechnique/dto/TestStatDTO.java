package com.cni.plateformetesttechnique.dto;


public record TestStatDTO(
        Long testId,
        String titreTest,
        long totalQuestions,
        long correctAnswers,
        long incorrectAnswers,
        String niveauDifficulte,
        double score
) {}