import AppointmentCard from "./AppointmentCard";

function UpcomingAppointments({
  appointments,
  onComplete,
  onCancel,
  onEdit
}) {

  return (
    <div className="upcoming-appointments">

      <h3>Upcoming Appointments</h3>

      {appointments.length === 0 ? (
        <p>No Upcoming Appointments</p>
      ) : (
        appointments.map((appointment) => (
          <AppointmentCard
            key={appointment.appointmentId}
            appointment={appointment}
            onComplete={onComplete}
            onCancel={onCancel}
            onEdit={onEdit}
          />
        ))
      )}

    </div>
  );
}

export default UpcomingAppointments;