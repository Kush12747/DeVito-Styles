/*
*   This page should be responsible for
*   - Loading appointment details
*   - Displaying barber/service info
*   - Rendering the review form
*/

import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";

import { addReview } from "../../Services/reviewService";
import { fetchAppointmentById } from "../../Services/appointmentService";

import ReviewForm from "../AppointmentService/ReviewForm";

function ReviewPage() {

    const navigate = useNavigate();

    const { appointmentId } = useParams();

    const [appointment, setAppointment] = useState(null);
    const [errors, setErrors] = useState([]);

    const loggedInUser = JSON.parse(localStorage.getItem("loggedInUser"));
    const token = localStorage.getItem("token");

    useEffect(() => {

        async function loadAppointment() {

            try {

                const data = await fetchAppointmentById(
                    appointmentId,
                    token
                );

                setAppointment(data);

            } catch {

                setErrors(["Unable to load appointment."]);
            }
        }

        loadAppointment();

    }, [appointmentId, token]);

    async function handleSubmit(formData) {

        const review = {

            userId: loggedInUser.userId,

            appointmentId: appointment.appointmentId,

            barberId: appointment.barberId,

            rating: formData.rating,

            reviewText: formData.reviewText

        };

        try {

            await addReview(review, token);

            navigate("/appointments");

        } catch (err) {

            setErrors(
                Array.isArray(err)
                    ? err
                    : ["Unable to submit review."]
            );
        }
    }

    if (!appointment) {

        return <p>Loading appointment...</p>;

    }

    return (

        <div>

            <h2>Leave a Review</h2>

            <p>

                <strong>Date:</strong>{" "}

                {new Date(
                    appointment.appointmentDatetime
                ).toLocaleString()}

            </p>

            <p>

                <strong>Barber ID:</strong>{" "}

                {appointment.barberId}

            </p>

            {errors.length > 0 && (

                <ul>

                    {errors.map(error => (

                        <li key={error}>{error}</li>

                    ))}

                </ul>

            )}

            <ReviewForm onSubmit={handleSubmit} />

        </div>

    );

}

export default ReviewPage;