import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import "../../styles/register.css";

function Register() {
    const navigate = useNavigate();

    const [user, setUser] = useState({
        username: "",
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        address: "",
        phone: "",
        role: "CUSTOMER"
    });

    const [errors, setErrors] = useState([]);

    function handleChange(event) {
        setUser({...user, [event.target.name]: event.target.value});
    }

    async function handleSubmit(event) {
        event.preventDefault();

        const response = await fetch("http://localhost:8080/api/user/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(user)
        });

        if (response.ok) {
            navigate("/login", {
                state: { message: "Registration successful. Please log in." }
            });
        } else {
            const payload = await response.json();
            setErrors(payload);

        }
    }
    return (
        <div className="bg">
            <div className="register-page">

                <div className="register-card">

                    <h2 className="register-title">
                        Create Account
                    </h2>

                    <div className="register-form-box">

                        {errors.length > 0 && (
                            <ul className="error-list">
                                {errors.map((error) => (
                                    <li key={error}>{error}</li>
                                ))}
                            </ul>
                        )}

                        <form onSubmit={handleSubmit}>

                            <div className="form-control">
                                <label>Username</label>
                                <input
                                    type="text"
                                    name="username"
                                    value={user.username}
                                    onChange={handleChange}
                                />
                            </div>

                            <div className="form-control">
                                <label>Password</label>
                                <input
                                    type="password"
                                    name="password"
                                    value={user.password}
                                    onChange={handleChange}
                                />
                            </div>

                            <div className="form-control">
                                <label>First Name</label>
                                <input
                                    type="text"
                                    name="firstName"
                                    value={user.firstName}
                                    onChange={handleChange}
                                />
                            </div>

                            <div className="form-control">
                                <label>Last Name</label>
                                <input
                                    type="text"
                                    name="lastName"
                                    value={user.lastName}
                                    onChange={handleChange}
                                />
                            </div>

                            <div className="form-control">
                                <label>Email</label>
                                <input
                                    type="email"
                                    name="email"
                                    value={user.email}
                                    onChange={handleChange}
                                />
                            </div>

                            <div className="form-control">
                                <label>Address</label>
                                <input
                                    type="text"
                                    name="address"
                                    value={user.address}
                                    onChange={handleChange}
                                />
                            </div>

                            <div className="form-control">
                                <label>Phone</label>
                                <input
                                    type="text"
                                    name="phone"
                                    value={user.phone}
                                    onChange={handleChange}
                                />
                            </div>

                            <button
                                className="register-btn"
                                type="submit"
                            >
                                Register
                            </button>

                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Register;