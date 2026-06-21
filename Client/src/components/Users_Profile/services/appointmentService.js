const BASE_URL = "http://localhost:8080/api/appointment";

export async function fetchAppointments(userId, token) {

  const response = await fetch(
    `${BASE_URL}/user/${userId}`,
    {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  );

  return response.json();
}

export async function updateAppointment(
  appointmentId,
  appointment,
  token
) {

  const response = await fetch(
    `${BASE_URL}/${appointmentId}`,
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(appointment)
    }
  );

  return response.json();
}

export async function completeAppointment(
  appointment,
  token
) {

  return updateAppointment(
    appointment.appointmentId,
    {
      ...appointment,
      status: "COMPLETED"
    },
    token
  );
}

export async function cancelAppointment(
  appointment,
  token
) {

  return updateAppointment(
    appointment.appointmentId,
    {
      ...appointment,
      status: "CANCELLED"
    },
    token
  );
}

export async function rescheduleAppointment(
  appointment,
  newDateTime,
  token
) {

  return updateAppointment(
    appointment.appointmentId,
    {
      ...appointment,
      appointmentDatetime: newDateTime
    },
    token
  );
}