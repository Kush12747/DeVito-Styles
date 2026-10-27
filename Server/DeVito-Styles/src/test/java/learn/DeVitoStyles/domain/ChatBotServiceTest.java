package learn.DeVitoStyles.domain;

import com.anthropic.client.AnthropicClient;
import com.anthropic.core.JsonValue;
import com.anthropic.errors.AnthropicIoException;
import com.anthropic.errors.InternalServerException;
import com.anthropic.errors.RateLimitException;
import com.anthropic.errors.UnauthorizedException;
import com.anthropic.models.beta.AnthropicBeta;
import com.anthropic.models.beta.messages.BetaContentBlock;
import com.anthropic.models.beta.messages.BetaMessage;
import com.anthropic.models.beta.messages.BetaOutputConfig;
import com.anthropic.models.beta.messages.BetaStopReason;
import com.anthropic.models.beta.messages.BetaTextBlock;
import com.anthropic.models.beta.messages.MessageCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatBotServiceTest {

    private AnthropicClient client;
    private ChatBotService service;

    @BeforeEach
    void setUp() {
        client = mock(AnthropicClient.class, RETURNS_DEEP_STUBS);
        service = new ChatBotService(client, "claude-opus-5-5");
    }

    @Test
    void chat_returnsClaudeReplyText() {
        BetaMessage response = message(BetaStopReason.END_TURN, "We're open ", "Mon-Sat, 9am - 4pm.");
        when(client.beta().messages().create(any(MessageCreateParams.class))).thenReturn(response);

        assertEquals("We're open Mon-Sat, 9am - 4pm.", service.chat("What are your hours?"));
    }

    @Test
    void chat_sendsSystemPromptUserMessageModelAndLowEffort() {
        BetaMessage response = message(BetaStopReason.END_TURN, "Hi!");
        when(client.beta().messages().create(any(MessageCreateParams.class))).thenReturn(response);

        service.chat("Do you do fades?");

        ArgumentCaptor<MessageCreateParams> captor = ArgumentCaptor.forClass(MessageCreateParams.class);
        verify(client.beta().messages()).create(captor.capture());
        MessageCreateParams params = captor.getValue();

        assertEquals("claude-opus-5-5", params.model().asString());
        assertEquals(ChatBotService.MAX_TOKENS, params.maxTokens());
        assertTrue(params.system().orElseThrow().string().orElseThrow().contains("DeVito Styles"));
        assertEquals(1, params.messages().size());
        assertEquals("Do you do fades?", params.messages().get(0).content().string().orElseThrow());
        assertEquals(BetaOutputConfig.Effort.LOW, params.outputConfig().orElseThrow().effort().orElseThrow());
    }

    @Test
    void chat_enablesRefusalFallbackOnSupportedModels() {
        MessageCreateParams params = service.buildRequest("Hello");

        assertTrue(params.betas().orElseThrow().contains(AnthropicBeta.of(ChatBotService.FALLBACK_BETA)));
        assertEquals(JsonValue.from("default"), params._additionalBodyProperties().get("fallbacks"));
    }

    @Test
    void chat_omitsRefusalFallbackOnModelsWithoutIt() {
        MessageCreateParams params = new ChatBotService(client, "claude-haiku-5-5").buildRequest("Hello");

        assertTrue(params.betas().map(List::isEmpty).orElse(true));
        assertFalse(params._additionalBodyProperties().containsKey("fallbacks"));
    }

    @Test
    void chat_whenRefused_returnsFriendlyRedirect() {
        BetaMessage response = message(BetaStopReason.REFUSAL);
        when(client.beta().messages().create(any(MessageCreateParams.class))).thenReturn(response);

        assertEquals(ChatBotService.REFUSAL_REPLY, service.chat("Something off-topic"));
    }

    @Test
    void chat_whenReplyIsEmpty_returnsUnavailable() {
        BetaMessage response = message(BetaStopReason.END_TURN, "   ");
        when(client.beta().messages().create(any(MessageCreateParams.class))).thenReturn(response);

        assertEquals(ChatBotService.UNAVAILABLE_REPLY, service.chat("Hello"));
    }

    @Test
    void chat_whenMessageBlank_doesNotCallApi() {
        assertEquals(ChatBotService.EMPTY_MESSAGE_REPLY, service.chat("  "));
        assertEquals(ChatBotService.EMPTY_MESSAGE_REPLY, service.chat(null));

        verify(client.beta().messages(), never()).create(any(MessageCreateParams.class));
    }

    @Test
    void chat_whenMessageTooLong_doesNotCallApi() {
        String longMessage = "a".repeat(ChatBotService.MAX_MESSAGE_LENGTH + 1);

        assertEquals(ChatBotService.TOO_LONG_REPLY, service.chat(longMessage));
        verify(client.beta().messages(), never()).create(any(MessageCreateParams.class));
    }

    @Test
    void chat_whenNoApiKey_returnsNotConfigured() {
        ChatBotService unconfigured = new ChatBotService("", "claude-opus-5-5");

        assertEquals(ChatBotService.NOT_CONFIGURED_REPLY, unconfigured.chat("What are your hours?"));
    }

    @Test
    void chat_whenApiKeyRejected_returnsNotConfigured() {
        when(client.beta().messages().create(any(MessageCreateParams.class)))
                .thenThrow(mock(UnauthorizedException.class));

        assertEquals(ChatBotService.NOT_CONFIGURED_REPLY, service.chat("Hello"));
    }

    @Test
    void chat_whenRateLimited_returnsUnavailable() {
        when(client.beta().messages().create(any(MessageCreateParams.class)))
                .thenThrow(mock(RateLimitException.class));

        assertEquals(ChatBotService.UNAVAILABLE_REPLY, service.chat("Hello"));
    }

    @Test
    void chat_whenServerError_returnsUnavailable() {
        when(client.beta().messages().create(any(MessageCreateParams.class)))
                .thenThrow(mock(InternalServerException.class));

        assertEquals(ChatBotService.UNAVAILABLE_REPLY, service.chat("Hello"));
    }

    @Test
    void chat_whenNetworkFails_returnsUnavailable() {
        when(client.beta().messages().create(any(MessageCreateParams.class)))
                .thenThrow(mock(AnthropicIoException.class));

        assertEquals(ChatBotService.UNAVAILABLE_REPLY, service.chat("Hello"));
    }

    private BetaMessage message(BetaStopReason stopReason, String... texts) {
        List<BetaContentBlock> blocks = java.util.Arrays.stream(texts)
                .map(text -> {
                    BetaTextBlock textBlock = mock(BetaTextBlock.class);
                    when(textBlock.text()).thenReturn(text);
                    BetaContentBlock block = mock(BetaContentBlock.class);
                    when(block.text()).thenReturn(Optional.of(textBlock));
                    return block;
                })
                .toList();

        BetaMessage message = mock(BetaMessage.class);
        when(message.stopReason()).thenReturn(Optional.of(stopReason));
        lenient().when(message.content()).thenReturn(blocks);
        return message;
    }
}
