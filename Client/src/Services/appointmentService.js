const BASE_URL = `${import.meta.env.VITE_API_URL}/api/appointment`;

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