import { useState } from "react";

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

        <form onSubmit={handleSubmit}>

            <h2>Leave a Review</h2>

            <label>Rating</label>

            <br />

            <select
                value={rating}
                onChange={(e) =>
                    setRating(Number(e.target.value))
                }
            >

                <option value={5}>5 Stars</option>

                <option value={4}>4 Stars</option>

                <option value={3}>3 Stars</option>

                <option value={2}>2 Stars</option>

                <option value={1}>1 Star</option>

            </select>

            <br />
            <br />

            <label>Review</label>

            <br />

            <textarea
                rows="5"
                value={reviewText}
                onChange={(e) =>
                    setReviewText(e.target.value)
                }
            />

            <br />
            <br />

            <button type="submit">

                Submit Review

            </button>

        </form>

    );

}

export default ReviewForm;