const BASE_URL = "http://localhost:8080/api/appointment";

export async function fetchAppointmentById(appointmentId, token) {
    const response = await fetch(`${BASE_URL}/${appointmentId}`,
        {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }
    );

    if (!response.ok) {
        throw new Error("Unable to load appointment.");
    }

    return response.json();
}