package learn.DeVitoStyles.domain;

import jakarta.annotation.Nonnull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ChatBotService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String message) {

        // 1. System prompt (controls behavior)
        String fullPrompt = getFullPrompt(message);

        //2. Ollama request body
        Map<String, Object> requestBody = Map.of("model", "llama3", "prompt", fullPrompt, "stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // 3. Call Ollama
        ResponseEntity<Map> response = restTemplate.postForEntity("http://localhost:11434/api/generate", request, Map.class);

        // 4. Extract response text
        Map body = response.getBody();

        return body.get("response").toString();
    }

    @Nonnull
    private static String getFullPrompt(String message) {
        String systemPrompt = """
        You are the customer service assistant for DeVito Styles barbershop.
        
        Rules:
        - Only answer questions about DeVito Styles.
        - Be concise and professional.
        - Services:
          * Classic Haircut: Standard haircut service.
          * Skin Fade: Fade haircut with detailed blending.
          * Beard Trim: Professional beard shaping and trim.
          * Haircut and Beard Combo: Haircut combined with beard service.
        - Hours: Mon-Sat: 9am - 4pm
        - Location: Miami, Florida
        - Phone: (847) 555-1234
        - If unrelated question, redirect to services or booking.
        """;

        String fullPrompt = systemPrompt + "\nCustomer: " + message;
        return fullPrompt;
    }
}
