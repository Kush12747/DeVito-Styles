import { useState } from "react";
import "../../styles/reviewPage.css";

function ReviewForm({ onSubmit }) {

    const [rating, setRating] = useState(5);

    const [reviewText, setReviewText] = useState("");

    const labels = {
        1:"We're Sorry",
        2:"Needs Improvement",
        3:"Good Experience",
        4:"Great Service",
        5:"Outstanding!"
    };

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

            <div className="star-rating">
                {[1,2,3,4,5].map(star => (
                    <span
                        key={star}

                        onClick={() => setRating(star)}

                        className={
                            star <= rating ? "star active" : "star"
                        }
                    >
                        ★
                    </span>
                ))}
            </div>

            <p className="rating-label">

                {labels[rating]}

            </p>

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