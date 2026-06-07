package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.ChatBotService;
import learn.DeVitoStyles.models.ChatRequest;
import learn.DeVitoStyles.models.ChatResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/chat")
@CrossOrigin
public class ChatBotController {

    private final ChatBotService chatService;

    public ChatBotController(ChatBotService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {

        String reply = chatService.chat(request.getMessage());

        return new ChatResponse(reply);
    }
}