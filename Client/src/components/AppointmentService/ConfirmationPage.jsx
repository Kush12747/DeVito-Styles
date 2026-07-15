import { Link, useLocation } from "react-router-dom";
import "../../styles/Confirmation.css";

function ConfirmationPage() {
    const location = useLocation();

    const appointment = location.state?.appointment;

    if (!appointment) {
        return (
            <div className="confirmation-page">
                <h2>No appointment information found.</h2>
                
                <Link to="/services">
                    Book an appointment
                </Link>
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
                        ✓
                    </div>

                    <h1>Appointment Confirmed</h1>

                    <p>Thank you for booking with DeVito Styles. Your appointment has been successfully booked.</p>

                    <p className="confirmation-email">A confirmation email has been sent to your email address.</p>
                </section>

                {/* Appointment Details */}

                <section className="appointment-details">

                    <h2>Appointment Detailes</h2>

                    <div className="detail-row">
                        <span className="label">💈Service</span>
                        <span className="value">{appointment.serviceName}</span>
                    </div>

                    <div className="detail-row">
                        <span className="label">👤 Barber</span>
                        <span className="value">{appointment.barberName}</span>
                    </div>

                    <div className="detail-row">
                        <span className="label">🙍 Customer</span>
                        <span className="value">{appointment.customerName}</span>
                    </div>

                    <div className="detail-row">
                        <span className="label">📅 Date</span>
                        <span className="value">
                            {new Date(appointment.appointmentDatetime)
                                .toLocaleDateString()}
                        </span>
                    </div>

                    <div className="detail-row">
                        <span className="label">🕙 Time</span>
                        <span className="value">{new Date(appointment.appointmentDatetime).toLocaleTimeString([], {
                            hour: "numeric",
                            minute: "2-digit"
                        })}
                        </span>
                    </div>

                    <div className="detail-row">
                        <span className="label">⏱ Duration</span>
                        <span className="value">
                            {appointment.durationMinutes} minutes
                        </span>
                    </div>

                    <div className="detail-row">
                        <span className="label">Status</span>

                        <span className="status booked">
                            {appointment.status}
                        </span>
                    </div>

                    {
                        appointment.googleCalendarUrl && (
                            <a
                                className="calendar-button"
                                href={appointment.googleCalendarUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                >
                                    Add to Google Calendar
                                </a>
                        )
                    }
                </section>

            </div> 
        </div>
    );
}

export default ConfirmationPage;