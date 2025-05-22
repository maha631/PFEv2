package com.cni.plateformetesttechnique.model;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
public class GeniniResponse {
    private Double note;
    private String correction;
    private String explication;
    private String ReponseCorrecte;
    private String feedback;
    // Getters et Setters
    public Double getNote() {
        return note;
    }

    public void setNote(Double note) {
        this.note = note;
    }

    public String getCorrection() {
        return correction;
    }

    public void setCorrection(String correction) {
        this.correction = correction;
    }



    public String getExplication() {
        return explication;
    }

    public void setExplication(String explication) {
        this.explication = explication;
    }
    public String getReponseCorrecte() {
        return ReponseCorrecte;
    }
    public void setReponseCorrecte(String reponseCorrecte) {
        this.ReponseCorrecte=ReponseCorrecte;
    }
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    public GeniniResponse parseGeniniResponse(String responseJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseNode = objectMapper.readTree(responseJson);

            // Récupérer le texte généré par Gemini
            String generatedText = responseNode.path("generatedText").asText();

            // Initialiser l'objet GeniniResponse
            GeniniResponse geniniResponse = new GeniniResponse();

            // Séparer la réponse par lignes
            String[] lines = generatedText.split("\n");

            // Analyser chaque ligne pour remplir l'objet GeniniResponse
            for (String line : lines) {
                if (line.startsWith("Note :")) {
                    // Extraire la note et la convertir en Double
                    String noteText = line.split(":")[1].trim();
                    geniniResponse.setNote(Double.parseDouble(noteText.replace("/10", "").trim()));
                } else if (line.startsWith("Correction :")) {
                    // Extraire la correction
                    geniniResponse.setCorrection(line.split(":")[1].trim());
                } else if (line.startsWith("Explication :")) {
                    // Extraire l'explication
                    geniniResponse.setExplication(line.split(":")[1].trim());
                } else if (line.startsWith("Réponse correcte :")) {
                    // Extraire la réponse correcte si elle est fournie
                    geniniResponse.setReponseCorrecte(line.split(":")[1].trim());
                } else if (line.startsWith("Feedback :")) {
                    // Extraire le feedback
                    geniniResponse.setFeedback(line.split(":")[1].trim());
                }
            }

            return geniniResponse;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du parsing de la réponse de Gemini", e);
        }
}
}

