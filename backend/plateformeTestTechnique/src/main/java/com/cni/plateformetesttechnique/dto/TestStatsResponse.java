package com.cni.plateformetesttechnique.dto;

public class TestStatsResponse {
    private double moyenne;
    private long dureeMoyen;

    private ScoreInfo meilleurScore;
    private ScoreInfo pirreScore;

    private ResultStats echecs;
    private ResultStats reussit;

    public TestStatsResponse() {
    }

    public TestStatsResponse(double moyenne, long dureeMoyen,
                             ScoreInfo meilleurScore, ScoreInfo pirreScore,
                             ResultStats echecs, ResultStats reussit) {
        this.moyenne = moyenne;
        this.dureeMoyen = dureeMoyen;
        this.meilleurScore = meilleurScore;
        this.pirreScore = pirreScore;
        this.echecs = echecs;
        this.reussit = reussit;
    }

    public TestStatsResponse(double moyenne, long dureeMoyen, ScoreInfo scoreInfo, ScoreInfo scoreInfo1, int participants, int totalReussite, int totalEchec, double tauxReussite, double tauxEchec) {
    }

    // Getters and Setters

    public double getMoyenne() {
        return moyenne;
    }

    public void setMoyenne(double moyenne) {
        this.moyenne = moyenne;
    }

    public long getDureeMoyen() {
        return dureeMoyen;
    }

    public void setDureeMoyen(long dureeMoyen) {
        this.dureeMoyen = dureeMoyen;
    }

    public ScoreInfo getMeilleurScore() {
        return meilleurScore;
    }

    public void setMeilleurScore(ScoreInfo meilleurScore) {
        this.meilleurScore = meilleurScore;
    }

    public ScoreInfo getPirreScore() {
        return pirreScore;
    }

    public void setPirreScore(ScoreInfo pirreScore) {
        this.pirreScore = pirreScore;
    }

    public ResultStats getEchecs() {
        return echecs;
    }

    public void setEchecs(ResultStats echecs) {
        this.echecs = echecs;
    }

    public ResultStats getReussit() {
        return reussit;
    }

    public void setReussit(ResultStats reussit) {
        this.reussit = reussit;
    }

    // Inner static classes

    public static class ScoreInfo {
        private String nom;
        private double score;

        public ScoreInfo() {
        }

        public ScoreInfo(String nom, double score) {
            this.nom = nom;
            this.score = score;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }

    public static class ResultStats {
        private double pourcentage;
        private int total;
        private int participants;

        public ResultStats() {
        }

        public ResultStats(double pourcentage, int total, int participants) {
            this.pourcentage = pourcentage;
            this.total = total;
            this.participants = participants;
        }

        public double getPourcentage() {
            return pourcentage;
        }

        public void setPourcentage(double pourcentage) {
            this.pourcentage = pourcentage;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getParticipants() {
            return participants;
        }

        public void setParticipants(int participants) {
            this.participants = participants;
        }
    }
}
