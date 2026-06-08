/*
 * ChatMessage Component
 *
 * Responsible for displaying
 * a single message in the chat.
 *
 * Examples:
 *
 * User message:
 * {
 *   sender: "user",
 *   text: "What are your hours?"
 * }
 *
 * Bot message:
 * {
 *   sender: "bot",
 *   text: "We are open 9AM-7PM."
 * }
 */

function ChatMessage({ message }) {

    return (

        /*
         * Dynamic CSS classes.
         *
         * If sender is "user":
         * className becomes:
         * "message user"
         *
         * If sender is "bot":
         * className becomes:
         * "message bot"
         *
         * If sender is "typing":
         * className becomes:
         * "message typing"
         */
        <div
            className={`message ${message.sender}`}
        >

            {/* Actual text displayed */}
            {message.text}

        </div>
    );
}

export default ChatMessage;