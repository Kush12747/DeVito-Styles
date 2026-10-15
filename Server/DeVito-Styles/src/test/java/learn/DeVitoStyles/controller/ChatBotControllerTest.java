package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.ChatBotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ChatBotControllerTest {

    @Mock
    private ChatBotService chatService;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ChatBotController(chatService)).build();
    }

    @Test
    void chat_passesMessageToService_andWrapsReplyInResponse() throws Exception {
        when(chatService.chat("What are your hours?")).thenReturn("We are open Monday-Saturday 9-7.");

        mvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"What are your hours?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("We are open Monday-Saturday 9-7."));

        verify(chatService).chat("What are your hours?");
    }

    @Test
    void chat_whenMessageMissing_passesNullToService() throws Exception {
        when(chatService.chat(null)).thenReturn("How can I help?");

        mvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("How can I help?"));

        verify(chatService).chat(null);
    }

    @Test
    void chat_whenBodyMissing_returns400WithoutCallingService() throws Exception {
        mvc.perform(post("/api/chat").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(chatService);
    }
}
