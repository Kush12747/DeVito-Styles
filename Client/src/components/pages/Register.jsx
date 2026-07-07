import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import "../../styles/register.css";
import logo from "../../images/logo.png";

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

                    <div className="register-header">
                        <img
                            src={logo}
                            alt="Devito Styles Logo"
                            className="register-logo"
                        />

                        <h1 className="register-title">
                            Devito Styles
                        </h1>

                        <p className="register-subtitle">
                            Create your customer account
                        </p>
                    </div>

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

                            <div className="form-control">
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

                            <div className="form-control">
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

                            <div className="form-control">
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

                            <div className="form-control">
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

                            <div className="form-control full width">
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

                            <div className="form-control">
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

                            <button className="register-btn" type="submit">
                                Create Account
                            </button>

                        </form>

                        <p className='register-footer'>
                            Already have an account?{" "}
                            <Link to="/login">Sign in</Link>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Register;