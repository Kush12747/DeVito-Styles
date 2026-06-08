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

    /*
     * Used to make HTTP requests from Spring Boot
     * to external services (Ollama in this case).
     */
    private final RestTemplate restTemplate = new RestTemplate();

    /*
     * Main chatbot method.
     *
     * Receives:
     *     User's message from the frontend
     *
     * Returns:
     *     AI-generated response from Ollama
     */
    public String chat(String message) {

        /*
         * Builds the full prompt sent to Ollama.
         *
         * This includes:
         * - Business information
         * - Rules
         * - Customer's question
         */
        String fullPrompt = getFullPrompt(message);

        /*
         * JSON body sent to Ollama.
         *
         * Equivalent JSON:
         *
         * {
         *   "model":"llama3",
         *   "prompt":"...",
         *   "stream":false
         * }
         */
        Map<String, Object> requestBody = Map.of(
                "model", "llama3",
                "prompt", fullPrompt,
                "stream", false
        );

        /*
         * Configure request headers.
         *
         * Tells Ollama we are sending JSON.
         */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        /*
         * Combines:
         * - Headers
         * - Request body
         *
         * into one HTTP request object.
         */
        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(requestBody, headers);

        /*
         * Sends POST request to Ollama.
         *
         * URL:
         * http://localhost:11434/api/generate
         *
         * Ollama processes the prompt and
         * returns a JSON response.
         */
        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        "http://localhost:11434/api/generate",
                        request,
                        Map.class
                );

        /*
         * Extract JSON body from response.
         */
        Map body = response.getBody();

        /*
         * Ollama response contains:
         *
         * {
         *   "response":"Hello! How can I help?"
         * }
         *
         * Return only the AI text.
         */
        return body.get("response").toString();
    }

    /*
     * Creates the prompt sent to Ollama.
     *
     * This is where the chatbot's behavior
     * is controlled.
     */
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
