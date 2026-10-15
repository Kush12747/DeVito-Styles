package learn.DeVitoStyles.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/*
 * ChatBotService creates its own RestTemplate internally (no injection point),
 * so instead of mocking it we bind Spring's MockRestServiceServer to that
 * instance. No real HTTP call to Ollama is made.
 */
class ChatBotServiceTest {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    private ChatBotService chatBotService;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        chatBotService = new ChatBotService();
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(chatBotService, "restTemplate");
        server = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void chat_postsPromptToOllamaAndReturnsResponseText() {
        server.expect(requestTo(OLLAMA_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.model").value("llama3"))
                .andExpect(jsonPath("$.stream").value(false))
                .andExpect(jsonPath("$.prompt").value(allOf(
                        containsString("customer service assistant for DeVito Styles"),
                        containsString("Hours: Mon-Sat: 9am - 4pm"),
                        endsWith("\nCustomer: What are your hours?"))))
                .andRespond(withSuccess(
                        "{\"model\":\"llama3\",\"response\":\"We are open Mon-Sat 9am-4pm.\",\"done\":true}",
                        MediaType.APPLICATION_JSON));

        String reply = chatBotService.chat("What are your hours?");

        assertEquals("We are open Mon-Sat 9am-4pm.", reply);
        server.verify();
    }

    @Test
    void chat_whenOllamaReturnsServerError_throws() {
        server.expect(requestTo(OLLAMA_URL)).andRespond(withServerError());

        assertThrows(HttpServerErrorException.class, () -> chatBotService.chat("hello"));
        server.verify();
    }
}
