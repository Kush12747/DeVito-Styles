import { useState } from "react";
import StarRating from "./StarRating";

function ReviewForm({ onSubmit }) {

    const [rating, setRating] = useState(5);

    const [hover, setHover] = useState(0);

    const [reviewText, setReviewText] = useState("");

    function handleSubmit(e){

        e.preventDefault();

        onSubmit({

            rating,

            reviewText

        });

    }

    const labels = {

        1:"Needs Improvement",

        2:"Fair Expereince",

        3:"Good Service",

        4:"Excellent",

        5:"Outstanding"

    };

    return (

        <form
            className="review-form"
            onSubmit={handleSubmit}
        >

            <label>

                Rate Your Experience

            </label>

            <StarRating

                rating={rating}

                setRating={setRating}

                hover={hover}

                setHover={setHover}

            />

            <div className="rating-label">

                {labels[hover || rating]}

            </div>

            <label>

                Tell us about your visit

            </label>

            <textarea

                value={reviewText}

                maxLength={500}

                onChange={(e)=>

                    setReviewText(e.target.value)

                }

                placeholder="How was your haircut? How was the service? Would you recommend your barber?"

            />

            <div className="character-count">

                {reviewText.length} / 500

            </div>

            <button>

                Submit Review

            </button>

        </form>

    );

}

export default ReviewForm;