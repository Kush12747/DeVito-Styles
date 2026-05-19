import "../../styles/profile.css";

function RescheduleAppointmentForm({ appointmentForm, setAppointmentForm, handleAppointmentUpdate, setEditingAppointment }) {
    return (
        <div className="edit-form-card">

            <h3>Reschedule Appointment</h3>

            <form onSubmit={handleAppointmentUpdate}>
                <input type="date" value={appointmentForm.date} onChange={(e) => 
                    setAppointmentForm({...appointmentForm, date: e.target.value})}
                />

                <input type="time" value={appointmentForm.time} onChange={(e) => setAppointmentForm({...appointmentForm, time: e.target.value})} />

                <div className="form-buttons">
                    <button type="submit">Save Changes</button>
                    <button type="button" onClick={() => setEditingAppointment(null)}>Cancel</button>
                </div>

            </form>

        </div>
    );
}

export default RescheduleAppointmentForm;