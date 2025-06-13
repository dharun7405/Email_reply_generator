package com.email.writer.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorService {

    private final WebClient webClient;
    public EmailGeneratorService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Value("${gemini.url}")
    private String geminiApiUrl;
    @Value("${gemini.api}")
    private String geminiApiKey;


    public String generateEmailReply(EmailRequest emailRequest){
        //BUILD THE PROMPT
        String prompt = buildPrompt(emailRequest);

        //CRAFT A REQUEST
        Map<String,Object> requestBody = Map.of(
                "contents",new Object[]{
                        Map.of("parts",new Object[]{
                                Map.of("text",prompt)
                        })
                }
        );

        //DO REQUEST AND GET RESPONSE
        String response = webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .header("Content-Type","application/json")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        
        //RETURN RESPONSE
        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content. Please don't generate a subject line");
        if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()){
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone");
        }
        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}
