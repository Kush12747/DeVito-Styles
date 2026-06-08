import { useState } from 'react';
import ChatWindow from './ChatWindow';
import "../../styles/ChatBot.css";

/*
*   Responsible for:
*       Floating button
*       Opening/Closing the chat
*       Passing data to ChatWindow
*/

function ChatBot() {
    
    const [isOpen, setIsOpen] = useState(false);

    return (
        <div>
            {isOpen && (
                <ChatWindow onClose={() => setIsOpen(false)} />
            )}

            <button className="chat-button" onClick={() => setIsOpen(!isOpen)}></button>
        </div>
    );   
}

export default ChatBot;