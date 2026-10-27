const API_URL = import.meta.env.VITE_API_URL;

/*
 * Admin dashboard stats.
 *
 * range: "day" | "week" | "month"
 * The backend only answers for ADMIN tokens (403 otherwise).
 */
export async function getDashboard(range) {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/api/admin/dashboard?range=${range}`, {
        headers: token ? { Authorization: `Bearer ${token}` } : {}
    });

    if (response.status === 401 || response.status === 403) {
        const error = new Error("Not authorized");
        error.status = response.status;
        throw error;
    }

    if (!response.ok) {
        throw new Error(`Dashboard request failed: ${response.status}`);
    }

    return response.json();
}
