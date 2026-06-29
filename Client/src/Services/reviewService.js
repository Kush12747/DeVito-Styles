// Base URL
const BASE_URL = "http://localhost:8080/api/review";

export async function addReview(review, token) {

    const response = await fetch(BASE_URL , {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(review)
    });

    // Convert any validation erros from spring into json
    if (!response.ok) {
        throw await response.json();
    }
    return await response.json();
}

/*
* Get a review by appointment.
* Used later to determine whether the user has already reviwed
* a completed appointment.
*/
export async function fetchReviewByAppointment(appointmentId, token) {

    const response = await fetch(`${BASE_URL}/appointment/${appointmentId}`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if (response.status === 404) {
        return null;
    }

    return await response.json();
}
