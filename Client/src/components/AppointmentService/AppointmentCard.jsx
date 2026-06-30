import { Link } from "react-router-dom";

function AppointmentCard({ appointment }) {

    return (

        <div className="appointment-card">

            <h3>

                Appointment

            </h3>

            <p>

                {new Date(
                    appointment.appointmentDatetime
                ).toLocaleString()}

            </p>

            <p>

                Barber ID: {appointment.barberId}

            </p>

            <Link
                to={`/appointments/${appointment.appointmentId}/review`}
            >

                Leave Review

            </Link>

        </div>

    );

}

export default AppointmentCard;