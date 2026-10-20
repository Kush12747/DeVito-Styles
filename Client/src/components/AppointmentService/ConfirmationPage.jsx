import { Link, useLocation } from "react-router-dom";
import {
    FaCheckCircle,
    FaCut,
    FaUser,
    FaCalendarAlt,
    FaClock,
    FaHourglassHalf,
    FaUserTie
} from "react-icons/fa";

import "../../styles/Confirmation.css";

function ConfirmationPage() {
    const location = useLocation();

    const appointment = location.state?.appointment;

    if (!appointment) {
        return (
            <div className="confirmation-page">
                <div className="confirmation-empty">
                    <span className="confirm-eyebrow">DeVito Styles</span>

                    <h2>No appointment information found.</h2>

                    <div className="confirm-divider"></div>

                    <Link className="confirm-btn confirm-btn--primary" to="/services">
                        Book an appointment
                    </Link>
                </div>
            </div>
        );
    }

    const appointmentDate = new Date(appointment.appointmentDatetime);

    return (
        <div className="confirm-page">

            <div className="confirmation-card">
                
                {/* Header */}
                
                <section className="confirmation-header">
                    
                    <div className="success-icon">
                        <FaCheckCircle />
                    </div>

                    <span className="confirm-eyebrow">DeVito Styles</span>

                    <h1>Appointment Confirmed</h1>

                    <div className="confirm-divider"></div>

                    <p>Thank you for booking with DeVito Styles. Your appointment has been successfully booked.</p>

                    <p className="confirmation-email">A confirmation email has been sent to your email address.</p>
                </section>

                {/* Appointment Details */}

                <section className="appointment-details">

                    <h2>Appointment Details</h2>

                    <div className="detail-row">
                        <span className="confirm-label">
                            <FaCut className="confirm-icon" />
                                Service
                        </span>
                        <span className="confirm-value">{appointment.serviceName}</span>
                    </div>

                    <div className="detail-row">
                        <span className="confirm-label">
                            <FaUserTie className="confirm-icon" />
                                Barber
                        </span>
                        <span className="confirm-value">{appointment.barberName}</span>
                    </div>

                    <div className="detail-row">
                        <span className="confirm-label">
                            <FaUser className="confirm-icon" />
                                Customer
                        </span>
                        <span className="confirm-value">{appointment.customerName}</span>
                    </div>

                    <div className="detail-row">
                        <span className="confirm-label">
                            <FaCalendarAlt className="confirm-icon" />
                                Date
                        </span>
                        
                        <span className="confirm-value">
                            {new Date(appointment.appointmentDatetime)
                                .toLocaleDateString()}
                        </span>
                    </div>

                    <div className="detail-row">
                        <span className="confirm-label">
                            <FaClock className="confirm-icon" />
                                Time
                        </span>
                        
                        <span className="confirm-value">{new Date(appointment.appointmentDatetime).toLocaleTimeString([], {
                            hour: "numeric",
                            minute: "2-digit"
                        })}
                        </span>
                    </div>

                    <div className="detail-row">
                        <span className="confirm-label">
                            <FaHourglassHalf className="confirm-icon" />
                            Duration
                        </span>
                        
                        <span className="confirm-value">
                            {appointment.durationMinutes} minutes
                        </span>
                    </div>

                    <div className="detail-row">
                        <span className="confirm-label">Status</span>

                        <span className="confirm-status booked">
                            {appointment.status}
                        </span>
                    </div>

                    {
                        appointment.googleCalendarUrl && (
                            <a
                                className="calendar-button confirm-btn confirm-btn--secondary"
                                href={appointment.googleCalendarUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                >
                                    Add to Google Calendar
                                </a>
                        )
                    }
                </section>

                <div className="confirmation-actions">
                    <Link className="confirm-btn confirm-btn--secondary" to="/services">Book Another</Link>
                    <Link className="confirm-btn confirm-btn--primary" to="/">Return Home</Link>
                </div>

            </div> 
        </div>
    );
}

export default ConfirmationPage;