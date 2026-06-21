function AppointmentHistory({ appointments }) {

  return (
    <div className="appointment-history">

      <h3>Appointment History</h3>

      {appointments.length === 0 ? (
        <p>No Appointment History</p>
      ) : (
        appointments.map(appointment => (
          <div
            className="appointment-item"
            key={appointment.appointmentId}
          >

            <h4>{appointment.serviceName}</h4>

            <p>
              Barber: {appointment.barberName}
            </p>

            <p>
              {new Date(
                appointment.appointmentDatetime
              ).toLocaleString()}
            </p>

            <p>
              Status: {appointment.status}
            </p>

          </div>
        ))
      )}

    </div>
  );
}

export default AppointmentHistory;