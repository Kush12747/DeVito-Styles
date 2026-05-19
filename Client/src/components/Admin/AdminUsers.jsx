import { useEffect, useState } from "react";
import "../../styles/admin.css";

function AdminUsers() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadUsers();
    }, []);

    async function loadUsers() {
        try {
            const res = await fetch("http://localhost:8080/api/user");

            if (res.ok) {
                const data = await res.json();

                //Add UI-only status
                const enriched = data.map(u => ({
                    ...u,
                    status: u.status || "ACTIVE"
                }));

                setUsers(enriched);
            }

        } catch (error) {
            console.log(error);
        } finally {
            setLoading(false);
        }
    }

    function toggleBan(userId) {
        setUsers(prev => prev.map(u=> {
            if (u.userId === userId) {
                return {
                    ...u,
                    status: u.status === "BANNED" ? "ACTIVE" : "BANNED"
                };
            }
            return u;
        })
    );
    }

    return (
        <div className="admin-users-page">
            <div className="admin-main">

                <h1>User Management</h1>

                {loading ? (
                    <p>Loading users...</p>
                ) : (
                    <div className="table-card">
                        <table className="admin-table">
                            <thead>
                                <tr>
                                    <th>Username</th>
                                    <th>Email</th>
                                    <th>Role</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>

                            <tbody>
                                {users.map(user => (
                                    <tr key={user.userId}>
                                        <td>{user.username}</td>
                                        <td>{user.email}</td>
                                        <td>{user.role}</td>

                                        <td>
                                            <span className={`status ${user.status.toLowerCase()}`}>{user.status}</span>
                                        </td>

                                        <td>
                                            <button className={user.status === "BANNED" ? "confirm" : "cancel"} onClick={() => toggleBan(user.userId)}>
                                                {user.status == "BANNED" ? "Unban" : "Ban"}
                                            </button>
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

export default AdminUsers;