import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import "../../styles/register.css";
import logo from "../../images/logo.png";
const API_URL = import.meta.env.VITE_API_URL;

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

        const response = await fetch(`${API_URL}/api/user/register`, {
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
        <div className="register-page">

            <div className="register-card">

                <div className="register-header">
                    <img
                        src={logo}
                        alt="Devito Styles Logo"
                        className="register-logo"
                    />

                    <p className="register-eyebrow">Membership</p>

                    <h1 className="register-title">Join DeVito Styles</h1>

                    <p className="register-subtitle">
                        Create your customer account
                    </p>
                </div>

                <div className="register-body">

                    {errors.length > 0 && (
                        <ul className="register-errors">
                            {errors.map((error) => (
                                <li key={error}>{error}</li>
                            ))}
                        </ul>
                    )}

                    <form className="register-form" onSubmit={handleSubmit}>

                        <div className="register-field">
                            <label htmlFor='username'>Username</label>
                            <input
                                id="username"
                                type="text"
                                name="username"
                                placeholder='Enter Your Username'
                                autoComplete='username'
                                autoFocus
                                value={user.username}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="register-field">
                            <label htmlFor='password'>Password</label>
                            <input
                                id="password"
                                type="password"
                                name="password"
                                autoComplete='password'
                                placeholder='Create a Password'
                                value={user.password}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="register-field">
                            <label htmlFor='firstName'>First Name</label>
                            <input
                                id="firstName"
                                type="text"
                                name="firstName"
                                placeholder='John'
                                autoComplete='firstname'
                                value={user.firstName}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="register-field">
                            <label htmlFor='lastName'>Last Name</label>
                            <input
                                id="lastName"
                                type="text"
                                name="lastName"
                                autoComplete='lastname'
                                placeholder='Doe'
                                value={user.lastName}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="register-field">
                            <label htmlFor='email'>Email</label>
                            <input
                                id="email"
                                type="email"
                                name="email"
                                placeholder='john@gmail.com'
                                autoComplete='email'
                                value={user.email}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="register-field">
                            <label htmlFor='phone'>Phone</label>
                            <input
                                id="phone"
                                type="text"
                                name="phone"
                                autoComplete='phone'
                                placeholder='555-555-5555'
                                value={user.phone}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="register-field register-field--full">
                            <label htmlFor='address'>Address</label>
                            <input
                                id="address"
                                type="text"
                                name="address"
                                placeholder='Enter your street address'
                                autoComplete='address'
                                value={user.address}
                                onChange={handleChange}
                            />
                        </div>

                        <button className="register-btn" type="submit">
                            Create Account
                        </button>

                    </form>

                    <p className="register-footer">
                        Already have an account?{" "}
                        <Link to="/login">Sign in</Link>
                    </p>
                </div>
            </div>
        </div>
    );
}

export default Register;