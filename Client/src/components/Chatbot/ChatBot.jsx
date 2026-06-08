import { useState } from 'react';
import ChatWindow from './ChatWindow';
import "../../styles/ChatBot.css";

/*
 * ChatBot Component
 *
 * Responsibilities:
 * 1. Display the floating chatbot button.
 * 2. Track whether the chat window is open or closed.
 * 3. Render the ChatWindow component when opened.
 * 4. Provide ChatWindow a way to close itself.
 */

function ChatBot() {

    /*
     * Stores whether the chatbot window is visible.
     *
     * false = hidden
     * true  = displayed
     */
    const [isOpen, setIsOpen] = useState(false);

    return (
        <div>
            {/*
             * Conditional rendering.
             *
             * If isOpen is true:
             *      Render ChatWindow
             *
             * If isOpen is false:
             *      Render nothing
             */}
            {isOpen && (
                /*
                 * Pass a callback function to ChatWindow.
                 *
                 * When the user clicks the X button,
                 * ChatWindow will call onClose(),
                 * which sets isOpen back to false.
                 */
                <ChatWindow onClose={() => setIsOpen(false)} />
            )}

            <button className="chat-button" onClick={() => setIsOpen(!isOpen)}></button>
        </div>
    );   
}

export default ChatBot;