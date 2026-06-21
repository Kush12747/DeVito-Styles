function AppointmentActions( {appointment, onComplete, onCancel, onReschedule} ) {
  return (
    <div className="appointment-actions">

      <button
        type="button"
        className="complete-btn"
        onClick={() => onComplete(appointment)}
      >
        Complete
      </button>

      <button
        type="button"
        className="cancel-btn"
        onClick={() => onCancel(appointment)}
      >
        Cancel
      </button>

      <button
        type="button"
        className="reschedule-btn"
        onClick={() => onReschedule(appointment)}
      >
        Reschedule
      </button>

    </div>
  );
}

export default AppointmentActions;