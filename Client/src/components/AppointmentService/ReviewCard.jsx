function ReviewCard({ review }) {

    return (

        <div className="review-card">

            <h4>

                {"★".repeat(review.rating)}

            </h4>

            <p>

                {review.reviewText}

            </p>

            <small>

                User #{review.userId}

            </small>

        </div>

    );

}

export default ReviewCard;