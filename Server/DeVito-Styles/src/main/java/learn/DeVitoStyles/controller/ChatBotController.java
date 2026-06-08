package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.ChatBotService;
import learn.DeVitoStyles.models.ChatRequest;
import learn.DeVitoStyles.models.ChatResponse;
import org.springframework.web.bind.annotation.*;

/*
 * REST Controller
 *
 * Responsibilities:
 * - Receive HTTP requests from the frontend
 * - Pass data to the service layer
 * - Return data back to the frontend
 *
 * It should NOT contain AI logic or business logic.
 */
@RestController
@RequestMapping("api/chat")
@CrossOrigin
public class ChatBotController {

    private final ChatBotService chatService;

    public ChatBotController(ChatBotService chatService) {
        this.chatService = chatService;
    }

    /*
     * Handles POST requests to:
     *
     * /api/chat
     *
     * Example request:
     *
     * {
     *   "message": "What are your hours?"
     * }
     */
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        /*
         * Extract user's message from request.
         *
         * request.getMessage()
         * -> "What are your hours?"
         */
        String reply =
                chatService.chat(
                        request.getMessage()
                );

        /*
         * Wrap AI response in a response object.
         *
         * Example:
         *
         * {
         *   "response":
         *   "We are open Monday-Saturday..."
         * }
         */
        return new ChatResponse(reply);
    }
}