package com.cni.plateformetesttechnique.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.cni.plateformetesttechnique.model.EvaluationResult;
import java.util.regex.*;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;

    public GeminiService(@Value("${gemini.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Map.of("key", apiKey))
                .build();
    }
public Mono<EvaluationResult> evaluateDeveloperResponse(String question, String reponse,Integer point) {

    String prompt = """
    Vous êtes un évaluateur technique.

    Voici une question posée à un développeur, ainsi que sa réponse. Veuillez fournir une évaluation concise, professionnelle et structurée de la réponse.

    Question : %s
    Réponse : %s

    Merci de fournir l’analyse dans le format suivant :

    - Note : / %d
    - Correction : [Correcte, Incorrecte]
    - Explication : phrase claire et concise
    - Réponse correcte : (à fournir uniquement si la réponse du candidat est Incorrecte)
    - Feedback : conseil(s) pour s’améliorer

    Respectez exactement ce format sans ajouter d'autres éléments. N'utilisez que [Correcte, Incorrecte] sans variantes.
""".formatted(question, reponse,point);
    Map<String, Object> body = Map.of(
            "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
    );

    // Appeler l'API et retourner un Mono<EvaluationResult> après avoir transformé la réponse en EvaluationResult
    return webClient.post()
            .uri("?key={key}")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String.class) // Récupérer la réponse sous forme de chaîne
            .doOnNext(responseText -> System.out.println("Réponse brute de Gemini: " + responseText)) // Log de la réponse brute
            .map(this::parseGeminiResponse); // Utiliser parseGeminiResponse pour transformer la chaîne en EvaluationResult
}
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EvaluationResult parseGeminiResponse(String text) {
        try {
            JsonNode root = objectMapper.readTree(text);
            text = root.path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText();

            EvaluationResult result = new EvaluationResult();

            Pattern notePattern = Pattern.compile("- Note ?: ?(\\d{1,2})\\s*/\\s*10", Pattern.CASE_INSENSITIVE);
            Pattern correctionPattern = Pattern.compile("- Correction ?: ?\\[?(Correcte|Incorrecte)\\]?", Pattern.CASE_INSENSITIVE);
            Pattern explicationPattern = Pattern.compile("- Explication ?: ?(.*?)(?:\\n- Réponse correcte|\\n- Feedback|$)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Pattern reponsePattern = Pattern.compile("- Réponse correcte ?: ?(.*?)(?:\\n- Feedback|$)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Pattern feedbackPattern = Pattern.compile("- Feedback ?: ?(.+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

            Matcher m;

            m = notePattern.matcher(text);
            if (m.find()) result.setNote(Integer.parseInt(m.group(1)));

            m = correctionPattern.matcher(text);
            if (m.find()) {
            result.setIsCorrecte(m.group(1).equalsIgnoreCase("Correcte"));
            }
            else result.setIsCorrecte(false); // Default si pas trouvé

            m = explicationPattern.matcher(text);
            if (m.find()) result.setExplication(m.group(1).trim());

            m = reponsePattern.matcher(text);
            if (m.find()) result.setReponseCorrecte(m.group(1).trim());

            m = feedbackPattern.matcher(text);
            if (m.find()) result.setFeedback(m.group(1).trim());

            return result;

        } catch (Exception e) {
            System.err.println("Erreur lors du parsing de la réponse Gemini : " + e.getMessage());
            return null;
        }
    }

//    public EvaluationResult parseGeminiResponse(String responseText) {
//        EvaluationResult result = new EvaluationResult();
//
//        Pattern notePattern = Pattern.compile("- Note ?: ?(\\d{1,2})/10", Pattern.CASE_INSENSITIVE);
//        Pattern correctionPattern = Pattern.compile("- Correction ?: ?\\[(Correcte|Incorrecte)]", Pattern.CASE_INSENSITIVE);
//        Pattern explicationPattern = Pattern.compile("- Explication ?: ?(.+)", Pattern.CASE_INSENSITIVE);
//        Pattern reponsePattern = Pattern.compile("- Réponse correcte ?: ?(.+)", Pattern.CASE_INSENSITIVE);
//        Pattern feedbackPattern = Pattern.compile("- Feedback ?: ?(.+)", Pattern.CASE_INSENSITIVE);
//
//        Matcher m;
//
//        m = notePattern.matcher(responseText);
//        if (m.find()) result.setNote(Integer.parseInt(m.group(1)));
//
//        m = correctionPattern.matcher(responseText);
//        if (m.find()) {
////            result.setIsCorrecte(m.group(1).equalsIgnoreCase("Correcte") ? 1 : 0);
//            result.setIsCorrecte(m.group(1).equalsIgnoreCase("Correcte"));
//
//        }
//
//        m = explicationPattern.matcher(responseText);
//        if (m.find()) result.setExplication(m.group(1).trim());
//
//        m = reponsePattern.matcher(responseText);
//        if (m.find()) result.setReponseCorrecte(m.group(1).trim());
//
//        m = feedbackPattern.matcher(responseText);
//        if (m.find()) result.setFeedback(m.group(1).trim());
//
//        return result;
//    }

}
