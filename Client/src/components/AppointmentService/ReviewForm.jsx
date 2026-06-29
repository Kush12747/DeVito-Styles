import { useState } from "react";

/**
 * 
 * ReviewForm is a reuseable form component.
 * 
 * Props:
 * onSubmit(reviewData)
 */
function ReviewForm({ onSubmit }) {
    
    // Default rating
    const [rating, setRating] = useState(5);
    
    // User's written review
    const [reviewText, setReviewText] = useState("");
    
    function handleSubmit(event) {
        event.preventDefaault();

        onSubmit({rating, reviewText});
    }
    
    return (
        <form onSubmit={handleSubmit}>
            <h2>Leave a Review</h2>

            {/* Rating */}

            <label>Rating</label>

            <select 
                value={rating}
                onChange={(e) => setRating(Number(e.target.value))}
            >
                <option value={5}> 5 Stars</option>
                <option value={4}> 4 Stars</option>
                <option value={3}> 3 Stars</option>
                <option value={2}> 2 Stars</option>
                <option value={1}> 1 Stars</option>   
            </select>

            <br />
            <br />

            <label>Review</label>

            <textarea 
                rows="5"
                value={reviewText}
                onChange={(e) => setReviewText(e.target.value)}
            />

            <br />
            <br />

            <button type="submit">Submit Review</button>

        </form>
    );
}

export default ReviewForm;