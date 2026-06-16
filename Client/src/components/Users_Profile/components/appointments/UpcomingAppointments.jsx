function UpcommingAppointments({ appointments, onEdit, onComplete, onCancel }) {
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
                        <p className={`status ${a.status?.toLowerCase()}`}>Status: {a.status}</p>

                        {a.status === "BOOKED" && (
                            <div className="appointment-actions">
                                <button className="reschedule-btn" onClick={() => onEdit(a)}>Reschedule</button>
                                <button className="complete-btn" onClick={() => onComplete(a)}>Complete</button>
                                <button className="cancel-btn" onClick={() => onCancel(a)}>Cancel</button>
                            </div>
                        )}
                    </div>
                ))
            )}

        </div>
    );
}

export default UpcommingAppointments;