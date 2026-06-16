const BASE_URL = "http://localhost:8080/api/appointment";

export async function fetchAppointments(userId, token) {
  const res = await fetch(`${BASE_URL}/user/${userId}`, {
    headers: { Authorization: `Bearer ${token}` }
  });

  return res.json();
}

export async function updateAppointment(id, appointment, token) {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    },
    body: JSON.stringify(appointment)
  });

  return res.json();
}