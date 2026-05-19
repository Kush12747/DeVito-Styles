function UpcommingAppointments({ appointments }) {
    return (
        <div className="appointment-card">

            <h3>Upcomming Appointments</h3>

            {appointments.length == 0 ? (
                <p>No Upcomming Appointments.</p>
            ) : (
                appointments.map(a => (
                    <div className="appointment-item" key={a.appointmentId} >

                    <h4>{a.serviceName}</h4>

                    <p>Barber: {a.barberName}</p>

                    <p>{new Date(a.appointmentDatetime).toLocaleString()}</p>
                    </div>
                ))
            )}

        </div>
    );
}

export default UpcommingAppointments;