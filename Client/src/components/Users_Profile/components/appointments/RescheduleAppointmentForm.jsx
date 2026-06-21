import "../../styles/profile.css";

function RescheduleAppointmentForm({
  appointmentForm,
  setAppointmentForm,
  handleAppointmentUpdate,
  setEditingAppointment
}) {

  const handleDateChange = (e) => {
    setAppointmentForm({
      ...appointmentForm,
      date: e.target.value
    });
  };

  const handleTimeChange = (e) => {
    setAppointmentForm({
      ...appointmentForm,
      time: e.target.value
    });
  };

  return (
    <div className="edit-form-card">

      <h3>Reschedule Appointment</h3>

      <form onSubmit={handleAppointmentUpdate}>

        <input
          type="date"
          value={appointmentForm.date}
          onChange={handleDateChange}
          required
        />

        <input
          type="time"
          value={appointmentForm.time}
          onChange={handleTimeChange}
          required
        />

        <div className="form-buttons">

          <button type="submit">
            Save Changes
          </button>

          <button
            type="button"
            onClick={() => setEditingAppointment(null)}
          >
            Cancel
          </button>

        </div>

      </form>

    </div>
  );
}

export default RescheduleAppointmentForm;