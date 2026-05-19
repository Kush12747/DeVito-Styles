import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../styles/admin.css";

function AdminDashboard() {

    const navigate = useNavigate();
    const [appointments, setAppointments] = useState([]);
    const [activeTab, setActiveTab] = useState("appointments");

    useEffect(() => {
        const user = JSON.parse(localStorage.getItem("loggedInUser"));

        if (!user || user.role !== "ADMIN") {
            navigate("/");
            return;
        }

        loadAppointments();
    }, []);

    async function loadAppointments() {
        try {
            const res = await fetch("http://localhost:8080/api/appointment");

            if (!res.ok) return;

            const data = await res.json();

            const enriched = await Promise.all(
                data.map(async (app) => {

                const userRes = await fetch(`http://localhost:8080/api/user/${app.userId}`);
                const user = await userRes.json();

                const serviceRes = await fetch(`http://localhost:8080/api/service/${app.serviceId}`);
                const service = await serviceRes.json();

                const barberRes = await fetch(`http://localhost:8080/api/barber/${app.barberId}`);
                const barber = await barberRes.json();

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
            const response = await fetch(`http://localhost:8080/api/appointment/${appointment.appointmentId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
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
        navigate("/login");
    }

    return (
        <div className="admin-container">

            {/* SIDEBAR */}
            <div className="admin-sidebar">
                <h2 className="admin-logo">DeVito Admin</h2>

                <button onClick={() => setActiveTab("appointments")}>
                    Appointments
                </button>

                <button onClick={() => setActiveTab("stats")}>
                    Dashboard
                </button>

                <button onClick={logout} className="logout-btn">
                    Logout
                </button>
            </div>

            {/* MAIN CONTENT */}
            <div className="admin-main">

                {/* DASHBOARD VIEW */}
                {activeTab === "stats" && (
                    <div>
                        <h1>Dashboard</h1>

                        <div className="admin-cards">
                            <div className="card">
                                Total: {appointments.length}
                            </div>

                            <div className="card">
                                Pending: {
                                    appointments.filter(a => a.status === "PENDING").length
                                }
                            </div>

                            <div className="card">
                                Completed: {
                                    appointments.filter(a => a.status === "COMPLETED").length
                                }
                            </div>
                        </div>
                    </div>
                )}

                {/* APPOINTMENTS VIEW */}
                {activeTab === "appointments" && (
                    <div>
                        <h1>Appointments</h1>

                        <table className="admin-table">
                            <thead>
                                <tr>
                                    <th>User</th>
                                    <th>Service</th>
                                    <th>Date</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>

                            <tbody>
                                {appointments.map(app => (
                                    <tr key={app.id}>
                                        <td>{app.userName}</td>
                                        <td>{app.serviceName}</td>
                                        <td>{app.barberName}</td>
                                        <td>
                                            {new Date(app.appointmentDatetime).toLocaleString()}
                                        </td>

                                        <td>{app.status}</td>

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
                )}

            </div>
        </div>
    );
}

export default AdminDashboard;