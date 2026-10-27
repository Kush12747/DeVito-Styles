package learn.DeVitoStyles.domain;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.core.JsonValue;
import com.anthropic.errors.AnthropicIoException;
import com.anthropic.errors.AnthropicServiceException;
import com.anthropic.errors.PermissionDeniedException;
import com.anthropic.errors.RateLimitException;
import com.anthropic.errors.UnauthorizedException;
import com.anthropic.models.beta.messages.BetaMessage;
import com.anthropic.models.beta.messages.BetaOutputConfig;
import com.anthropic.models.beta.messages.BetaStopReason;
import com.anthropic.models.beta.messages.BetaTextBlock;
import com.anthropic.models.beta.messages.MessageCreateParams;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * Customer service chat assistant backed by the Claude API.
 *
 * Every failure (no API key, rate limits, outages, refusals) becomes a
 * friendly reply instead of an error, so the chat window never breaks.
 */
@Service
public class ChatBotService {

    private static final Logger log = LoggerFactory.getLogger(ChatBotService.class);

    static final String EMPTY_MESSAGE_REPLY =
            "How can I help? Ask me about our services, hours or booking.";
    static final String TOO_LONG_REPLY =
            "Please keep your question under 1,000 characters.";
    static final String NOT_CONFIGURED_REPLY =
            "Our chat assistant isn't available right now. Please call us at (847) 555-1234 or book online.";
    static final String UNAVAILABLE_REPLY =
            "Sorry, I'm having trouble answering right now. Please try again in a moment, or call us at (847) 555-1234.";
    static final String REFUSAL_REPLY =
            "I can only help with questions about DeVito Styles, like our services, hours and booking.";

    static final int MAX_MESSAGE_LENGTH = 1000;

    // Short support answers; also caps the cost of any single reply
    static final long MAX_TOKENS = 4096;

    // Models that support server-side refusal fallbacks ("fallbacks": "default")
    private static final Set<String> FALLBACK_MODELS =
            Set.of("claude-opus-5-5", "claude-opus-5", "claude-fable-5-1", "claude-sonnet-5-5");
    static final String FALLBACK_BETA = "server-side-fallback-2026-07-01";

    static final String SYSTEM_PROMPT = """
            You are the customer service assistant for DeVito Styles barbershop.

            Rules:
            - Only answer questions about DeVito Styles.
            - Be concise and professional.
            - Hours: Mon-Sat: 9am - 4pm
            - Location: Miami, Florida
            - Phone: (847) 555-1234
            - If unrelated question, redirect to services or booking.
            """;

    // Null when no API key is configured; chat() then returns NOT_CONFIGURED_REPLY
    private final AnthropicClient client;
    private final String model;

    @Autowired
    public ChatBotService(
            @Value("${anthropic.api-key:}") String apiKey,
            @Value("${anthropic.model:claude-opus-5-5}") String model
    ) {
        this(createClient(apiKey), model);
    }

    // Used directly by tests to inject a mock client
    ChatBotService(AnthropicClient client, String model) {
        this.client = client;
        this.model = model;

        if (client == null) {
            log.warn("ANTHROPIC_API_KEY is not set; the chat assistant will reply that it's unavailable.");
        }
    }

    public String chat(String message) {
        if (message == null || message.isBlank()) {
            return EMPTY_MESSAGE_REPLY;
        }

        if (message.length() > MAX_MESSAGE_LENGTH) {
            return TOO_LONG_REPLY;
        }

        if (client == null) {
            return NOT_CONFIGURED_REPLY;
        }

        try {
            BetaMessage response = client.beta().messages().create(buildRequest(message));

            boolean refused = response.stopReason()
                    .map(BetaStopReason.REFUSAL::equals)
                    .orElse(false);
            if (refused) {
                return REFUSAL_REPLY;
            }

            String reply = response.content().stream()
                    .flatMap(block -> block.text().stream())
                    .map(BetaTextBlock::text)
                    .collect(Collectors.joining())
                    .trim();

            return reply.isEmpty() ? UNAVAILABLE_REPLY : reply;

        } catch (UnauthorizedException | PermissionDeniedException e) {
            log.error("Claude API rejected the API key; check ANTHROPIC_API_KEY", e);
            return NOT_CONFIGURED_REPLY;
        } catch (RateLimitException e) {
            log.warn("Claude API rate limit reached", e);
            return UNAVAILABLE_REPLY;
        } catch (AnthropicServiceException e) {
            log.error("Claude API request failed (status {})", e.statusCode(), e);
            return UNAVAILABLE_REPLY;
        } catch (AnthropicIoException e) {
            log.error("Could not reach the Claude API", e);
            return UNAVAILABLE_REPLY;
        }
    }

    MessageCreateParams buildRequest(String message) {
        MessageCreateParams.Builder builder = MessageCreateParams.builder()
                .model(model)
                .maxTokens(MAX_TOKENS)
                .system(SYSTEM_PROMPT)
                // Low effort suits short customer service answers (faster, cheaper)
                .outputConfig(BetaOutputConfig.builder()
                        .effort(BetaOutputConfig.Effort.LOW)
                        .build())
                .addUserMessage(message);

        // If a safety classifier declines a request, let the API retry it on a
        // suitable model instead of returning nothing.
        if (FALLBACK_MODELS.contains(model)) {
            builder.addBeta(FALLBACK_BETA)
                    .putAdditionalBodyProperty("fallbacks", JsonValue.from("default"));
        }

        return builder.build();
    }

    @PreDestroy
    void close() {
        if (client != null) {
            client.close();
        }
    }

    private static AnthropicClient createClient(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }

        return AnthropicOkHttpClient.builder()
                .apiKey(apiKey)
                .timeout(Duration.ofSeconds(60))
                .build();
    }
}
