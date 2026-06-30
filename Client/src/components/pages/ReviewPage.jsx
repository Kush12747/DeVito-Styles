/*
*   This page should be responsible for
*   - Loading appointment details
*   - Displaying barber/service info
*   - Rendering the review form
*/

import { useNavigate, useParams, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";

import { addReview } from "../../Services/reviewService";
import { fetchAppointmentById } from "../../Services/appointmentService";
import { FaCut } from "react-icons/fa";
import { GiRazor } from "react-icons/gi";
import { FaCalendarAlt } from "react-icons/fa";

import ReviewForm from "../AppointmentService/ReviewForm";
import "../../styles/ReviewPage.css";

function ReviewPage() {

    const navigate = useNavigate();
    const location = useLocation();
    const passedAppointment = location.state?.appointment;

    const { appointmentId } = useParams();

    const [appointment, setAppointment] = useState(null);
    const [errors, setErrors] = useState([]);

    const loggedInUser = JSON.parse(localStorage.getItem("loggedInUser"));
    const token = localStorage.getItem("token");

    useEffect(() => {

        async function loadAppointment() {

            try {

                if (passedAppointment) {

                    setAppointment(passedAppointment);

                    return;
                }

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

        <div className="review-page">

            <div className="review-container">
                
                <div className="review-header">
                    <h2>Leave a Review</h2>

                    <p>Tell us about your expereince with your appointment.</p>
                </div>

                    <div className="appointment-summary">
                        <h3>Appointment Details</h3>

                        <div className="summary-row">
                            <span>
                                <FaCut className="summary-icon" />
                                Service
                            </span>
                            
                            <span>{appointment.serviceName}</span>
                        </div>

                        <div className="summary-row">
                            <span>
                                <GiRazor className="summary-icon" />
                                Barber
                            </span>
                            
                            <span>{appointment.barberName}</span>
                        </div>

                        <div className="summary-row">
                            <span>
                                <FaCalendarAlt className="summary-icon" />
                                Date
                            </span>

                            <span>
                                {new Date(appointment.appointmentDatetime).toLocaleString()}
                            </span>
                        </div>

                    </div>
                    
                    {errors.length > 0 && (
                        <div className="review-errors">
                            {errors.map(error => (
                                <p key={error}>{error}</p>
                            ))}
                        </div>
                    )}

                    <ReviewForm onSubmit={handleSubmit} />

                </div>
            </div>
        );

    }

export default ReviewPage;