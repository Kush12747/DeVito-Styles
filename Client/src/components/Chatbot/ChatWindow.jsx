import { useState } from "react";
import "../../styles/ChatBot.css";
import ChatMessage from "./ChatMessage";

function ChatWindow({ onClose }) {

    const [messages, setMessages] = useState([]);

    const [input, setInput] = useState("");

    async function sendMessage() {
        
        if (!input.trim()) {
            return;
        }

        const userMessage = input;

        setInput("");

        setMessages(prev => [...prev,
            {
                sender: "user",
                text: userMessage
            },
            {
                sender:"typing",
                text: "Typing..."
            }
        ]);

        try {
            const response = await fetch("http://localhost:8080/api/chat",
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

            const data = await response.json();

            setMessages(prev => {

                const filtered = prev.filter(message => message.sender !== "typing");

                return [
                    ...filtered,
                    {
                        sender: "bot",
                        text: data.response
                    }
                ];
            });

        } catch (error) {
            setMessages(prev => {

            const filtered =
                prev.filter(
                    message => message.sender !== "typing"
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
            <div className="chat-header">
                
                <span>
                     ✂️ DeVito Styles Assistant
                </span>

                <button className="close-chat-btn" onClick={onClose}>X</button>
            </div>
        
            <div className="chat-body">
                {messages.map((message, index) => (
                    <ChatMessage
                        key={index}
                        message={message}
                    />
                ))}
            </div>

            <div className="chat-footer">
                <input
                    type="text"
                    value={input} 
                    onChange={(e) => setInput(e.target.value)} 
                    placeholder="Ask a question..." 
                    onKeyDown={(e) => {
                        if (e.key === "Enter") {
                            sendMessage();
                        }
                    }}    
                />

                <button onClick={sendMessage}>Send</button>
            </div>
        </div>

    );
}

export default ChatWindow;