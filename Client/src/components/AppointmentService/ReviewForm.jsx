import { useState } from "react";
import "../../styles/reviewPage.css";

function ReviewForm({ onSubmit }) {

    const [rating, setRating] = useState(5);

    const [reviewText, setReviewText] = useState("");

    function handleSubmit(event) {

        event.preventDefault();

        onSubmit({

            rating,

            reviewText

        });

    }

    return (

        <form onSubmit={handleSubmit} className="review-form">

            <label>Rating</label>

            <select
                value={rating}
                onChange={(e) =>
                    setRating(Number(e.target.value))
                }
            >
                <option value={5}>⭐⭐⭐⭐⭐ Excellent</option>
                <option value={4}>⭐⭐⭐⭐ Very Good</option>
                <option value={3}>⭐⭐⭐ Good</option>
                <option value={2}>⭐⭐ Fair</option>
                <option value={1}>⭐ Poor</option>
            </select>

            <label>Tell us about your experience</label>

            <textarea
                rows="6"
                placeholder="Share your experience..."
                value={reviewText}
                onChange={(e) =>
                    setReviewText(e.target.value)
                }
            />

            <button type="submit">
                Submit Review
            </button>

        </form>
    );

}

export default ReviewForm;