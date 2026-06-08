import { useState } from "react";
import "../../styles/ChatBot.css";
import ChatMessage from "./ChatMessage";

function ChatWindow({ onClose }) {

    /*
     * Stores the entire conversation.
     *
     * Example:
     * [
     *   { sender: "user", text: "Hello" },
     *   { sender: "bot", text: "Hi there!" }
     * ]
     */
    const [messages, setMessages] = useState([]);

    /*
     * Stores the current text inside
     * the input box.
     */
    const [input, setInput] = useState("");

    /*
     * Called when:
     * - Send button is clicked
     * - Enter key is pressed
     */
    async function sendMessage() {

        /*
         * Prevent sending empty messages.
         *
         * trim() removes whitespace.
         */
        if (!input.trim()) {
            return;
        }

        /*
         * Save the current input before
         * clearing the text box.
         */
        const userMessage = input;

        /*
         * Clear input field immediately
         * for a smoother user experience.
         */
        setInput("");

        /*
         * Add:
         * 1. User message
         * 2. Temporary "Typing..." message
         */
        setMessages(prev => [
            ...prev,

            {
                sender: "user",
                text: userMessage
            },

            {
                sender: "typing",
                text: "Typing..."
            }
        ]);

        try {

            /*
             * Send user message to backend.
             *
             * React
             *   ->
             * Spring Boot
             *   ->
             * Ollama
             */
            const response = await fetch(
                "http://localhost:8080/api/chat",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        message: userMessage
                    })
                }
            );

            /*
             * Convert JSON response
             * into a JavaScript object.
             */
            const data = await response.json();

            /*
             * Remove "Typing..."
             * and replace it with
             * the bot response.
             */
            setMessages(prev => {

                const filtered =
                    prev.filter(
                        message =>
                            message.sender !== "typing"
                    );

                return [
                    ...filtered,

                    {
                        sender: "bot",
                        text: data.response
                    }
                ];
            });

        } catch (error) {

            /*
             * If backend fails,
             * remove typing message
             * and show an error.
             */
            setMessages(prev => {

                const filtered =
                    prev.filter(
                        message =>
                            message.sender !== "typing"
                    );

                return [
                    ...filtered,

                    {
                        sender: "bot",
                        text: "Unable to connect."
                    }
                ];
            });
        }
    }

    return (
        <div className="chat-window">

            {/* Header Section */}
            <div className="chat-header">

                <span>
                    ✂️ DeVito Styles Assistant
                </span>

                {/* Close button */}
                <button
                    className="close-chat-btn"
                    onClick={onClose}
                >
                    X
                </button>

            </div>

            {/* Chat messages */}
            <div className="chat-body">

                {messages.map((message, index) => (

                    <ChatMessage
                        key={index}
                        message={message}
                    />

                ))}

            </div>

            {/* Input area */}
            <div className="chat-footer">

                <input
                    type="text"

                    value={input}

                    onChange={(e) =>
                        setInput(e.target.value)
                    }

                    placeholder="Ask a question..."

                    onKeyDown={(e) => {

                        if (e.key === "Enter") {
                            sendMessage();
                        }

                    }}
                />

                <button onClick={sendMessage}>
                    Send
                </button>

            </div>

        </div>
    );
}

export default ChatWindow;