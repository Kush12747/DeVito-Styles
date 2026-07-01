import { FaStar } from "react-icons/fa";

function StarRating({
    rating,
    setRating,
    hover,
    setHover
}) {

    return (

        <div className="star-rating">

            {[1,2,3,4,5].map((value) => (

                <FaStar

                    key={value}

                    className={
                        value <= (hover || rating)
                            ? "star active"
                            : "star"
                    }

                    onMouseEnter={() => setHover(value)}

                    onMouseLeave={() => setHover(0)}

                    onClick={() => setRating(value)}

                />

            ))}

        </div>

    );

}

export default StarRating;