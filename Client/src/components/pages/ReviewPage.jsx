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
import { FaStar } from "react-icons/fa";
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

            navigate("/profile");

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

            <div className="luxury-bg-overlay">

                <div className="review-container">

                    {/* ================= HERO ================= */}

                    <div className="review-header">

                        <span className="review-tag">
                            ★ PREMIUM EXPERIENCE
                        </span>

                        <h1>Leave Your Review</h1>

                        <p>
                            Your feedback helps us continue delivering
                            exceptional grooming experiences.
                        </p>

                    </div>

                    {/* ================= APPOINTMENT INFO ================= */}

                    <div className="appointment-section">

                        <h2>Appointment Summary</h2>

                        <div className="summary-grid">

                            <div className="summary-card">

                                <div className="summary-card-icon">
                                    <FaCut />
                                </div>

                                <div>

                                    <small>Service</small>

                                    <h3>{appointment.serviceName}</h3>

                                </div>

                            </div>

                            <div className="summary-card">

                                <div className="summary-card-icon">

                                    <GiRazor />

                                </div>

                                <div>

                                    <small>Your Barber</small>

                                    <h3>{appointment.barberName}</h3>

                                </div>

                            </div>

                            <div className="summary-card">

                                <div className="summary-card-icon">

                                    <FaCalendarAlt />

                                </div>

                                <div>

                                    <small>Date & Time</small>

                                    <h3>

                                        {new Date(
                                            appointment.appointmentDatetime
                                        ).toLocaleString()}

                                    </h3>

                                </div>

                            </div>

                        </div>

                    </div>

                    {/* ================= REVIEW SECTION ================= */}

                    <div className="review-form-section">

                        <div className="section-title">

                            <span></span>

                            <h2>Share Your Experience</h2>

                            <span></span>

                        </div>

                        <p className="section-description">

                            Tell future clients what made your visit memorable.

                        </p>

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

            </div>

        </div>

    );
}

export default ReviewPage;