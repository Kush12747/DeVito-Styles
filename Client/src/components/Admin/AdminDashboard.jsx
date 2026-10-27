import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../styles/admin.css";
import DashboardOverview from "./dashboard/DashboardOverview";
const API_URL = import.meta.env.VITE_API_URL;

// Every /api call needs the login token
function authHeaders() {
    const token = localStorage.getItem("token");
    return token ? { Authorization: `Bearer ${token}` } : {};
}
    
function AdminDashboard() {
    

    const navigate = useNavigate();
    const [appointments, setAppointments] = useState([]);
    const [activeTab, setActiveTab] = useState("overview");

    useEffect(() => {
        const user = JSON.parse(localStorage.getItem("loggedInUser"));

        if (!user || user.role !== "ADMIN") {
            navigate("/");
            return;
        }

    }, []);

    // Only fetch the full appointment list when that tab is opened
    useEffect(() => {
        if (activeTab === "appointments") {
            loadAppointments();
        }
    }, [activeTab]);

    async function loadAppointments() {
        try {
            const res = await fetch(`${API_URL}/api/appointment`, { headers: authHeaders() });

            if (!res.ok) return;

            const data = await res.json();

            // Many appointments share a user/service/barber: fetch each one once
            const cache = new Map();
            const lookup = (path) => {
                if (!cache.has(path)) {
                    cache.set(path, fetch(`${API_URL}${path}`, { headers: authHeaders() }).then(r => r.json()));
                }
                return cache.get(path);
            };

            const enriched = await Promise.all(
                data.map(async (app) => {

                const [user, service, barber] = await Promise.all([
                    lookup(`/api/user/${app.userId}`),
                    lookup(`/api/service/${app.serviceId}`),
                    lookup(`/api/barber/${app.barberId}`)
                ]);

                return {
                    ...app,
                    userName: user.username,
                    serviceName: service.name,
                    barberName: barber.firstName + " " + barber.lastName
                };
            })
        );

        setAppointments(enriched);

        } catch (error) {
            console.log(error);
        }
    }

    async function updateStatus(appointment, newStatus) {
        try {
            const response = await fetch(`${API_URL}/api/appointment/${appointment.appointmentId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    ...authHeaders()
                },
                body: JSON.stringify({ 
                    ...appointment,
                    status: newStatus
                })
            }
        );

            if (response.ok) {
                loadAppointments();
            }
        } catch (err) {
            console.error("Error updating status", err);
        }
    }

    function logout() {
        localStorage.removeItem("loggedInUser");
        localStorage.removeItem("token");
        navigate("/login");
    }

    return (
        <div className="admin-container">

            {/* SIDEBAR */}
            <nav className="admin-sidebar" aria-label="Admin">
                <h2 className="admin-logo">
                    DEVITO//OPS
                    <span>admin console</span>
                </h2>

                <p className="admin-nav-label">Monitor</p>

                <button
                    className={activeTab === "overview" ? "active" : ""}
                    onClick={() => setActiveTab("overview")}
                >
                    Overview
                </button>

                <button
                    className={activeTab === "appointments" ? "active" : ""}
                    onClick={() => setActiveTab("appointments")}
                >
                    Appointments
                </button>

                <p className="admin-nav-label">Manage</p>

                <button onClick={() => navigate("/admin/users")}>
                    Users
                </button>

                <button onClick={logout} className="logout-btn">
                    Logout
                </button>
            </nav>

            {/* MAIN CONTENT */}
            <div className="admin-main">

                {/* OVERVIEW (metrics dashboard) */}
                {activeTab === "overview" && <DashboardOverview />}

                {/* APPOINTMENTS VIEW */}
                {activeTab === "appointments" && (
                    <div>
                        <h1>Appointments</h1>

                        <div className="table-card">
                            <table className="admin-table">
                                <thead>
                                    <tr>
                                        <th>User</th>
                                        <th>Service</th>
                                        <th>Barber</th>
                                        <th>Date</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {appointments.map(app => (
                                        <tr key={app.appointmentId}>
                                            <td>{app.userName}</td>
                                            <td>{app.serviceName}</td>
                                            <td>{app.barberName}</td>
                                            <td>
                                                {new Date(app.appointmentDatetime).toLocaleString()}
                                            </td>

                                            <td>
                                                <span className={`status ${app.status?.toLowerCase()}`}>
                                                    {app.status}
                                                </span>
                                            </td>

                                            <td className="actions">
                                                {app.status !== "COMPLETED" && (
                                                    <button
                                                        onClick={() => updateStatus(app, "COMPLETED")}
                                                        className="complete"
                                                    >
                                                        Complete
                                                    </button>
                                                )}

                                                {app.status === "PENDING" && (
                                                    <button
                                                        onClick={() => updateStatus(app, "CONFIRMED")}
                                                        className="confirm"
                                                    >
                                                        Confirm
                                                    </button>
                                                )}

                                                {app.status !== "CANCELLED" && (
                                                    <button
                                                        onClick={() => updateStatus(app, "CANCELLED")}
                                                        className="cancel"
                                                    >
                                                        Cancel
                                                    </button>
                                                )}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

            </div>
        </div>
    );
}

export default AdminDashboard;