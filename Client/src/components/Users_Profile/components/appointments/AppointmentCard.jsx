import AppointmentActions from "./AppointmentActions";

function AppointmentCard({
  appointment,
  onComplete,
  onCancel,
  onEdit
}) {

  const appointmentDate =
    new Date(appointment.appointmentDatetime);

  return (
    <div className="appointment-card">

      <div className="appointment-details">

  <div className="detail-row">
    <span className="label">Barber:</span>
    <span className="value">{appointment.barberName}</span>
  </div>

  <div className="detail-row">
    <span className="label">Service:</span>
    <span className="value">{appointment.serviceName}</span>
  </div>

  <div className="detail-row">
    <span className="label">Date:</span>
    <span className="value">
      {appointmentDate.toLocaleDateString()}
    </span>
  </div>

  <div className="detail-row">
    <span className="label">Time:</span>
    <span className="value">
      {appointmentDate.toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit"
      })}
    </span>
  </div>

  <div className="detail-row">
    <span className="label">Status:</span>
    <span className={`status ${appointment.status.toLowerCase()}`}>
      {appointment.status}
    </span>
  </div>

</div>

      <AppointmentActions
        appointment={appointment}
        onComplete={onComplete}
        onCancel={onCancel}
        onReschedule={onEdit}
      />

    </div>
  );
}

export default AppointmentCard;