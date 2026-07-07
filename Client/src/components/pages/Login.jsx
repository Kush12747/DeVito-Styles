import { useState } from "react"
import { useNavigate, useOutletContext, Link } from "react-router-dom"
import "../../styles/login.css";
import logo from "../../images/logo.png";

function Login() {
    const navigate = useNavigate()
    const [showPassword, setShowPassword] = useState(false);
    const { setLoggedInUser } = useOutletContext()

    const [credentials, setCredentials] = useState({
        username: "",
        password: ""
    })

    const [errors, setErrors] = useState([])

    function handleChange(event) {
        setCredentials({
            ...credentials,
            [event.target.name]: event.target.value
        })
    }

    async function handleSubmit(event) {
        event.preventDefault()

        const response = await fetch("http://localhost:8080/api/user/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(credentials)
        })

        const payload = await response.json()

        if (response.ok) {
            
            const loggedInUser = {
                userId: payload.userId,
                username: payload.username,
                role: payload.role
            };

            setLoggedInUser(loggedInUser);

            localStorage.setItem("loggedInUser", JSON.stringify(loggedInUser));
            localStorage.setItem("token", payload.token);

            if (payload.role === "ADMIN") {
                navigate("/admin")
            } else {
                navigate("/initial")
            }
        } else {
            setErrors(Array.isArray(payload) ? payload : [payload.message || "Login failed"]);
        }
    }

    return (
        <div className="bg">
            <div className="login-page">
                <div className="login-card">

                    <div className="login-header">
                        <img
                            src={logo}
                            alt="Devito Styles Logo"
                            className="login-logo"
                        />

                        <h1 className="login-title">
                            Devito Styles
                        </h1>

                        <p className="login-subtitle">
                            Sign in to your account
                        </p>
                    </div>

                    <div className="login-form-box">
                        <form onSubmit={handleSubmit}>

                            {errors.length > 0 && (
                                <ul className="error-list">
                                    {errors.map((e) => (
                                        <li key={e}>{e}</li>
                                    ))}
                                </ul>
                            )}

                            <div className="form-control">
                                <label htmlFor="username">Username</label>

                                <input
                                    id="username"
                                    name="username"
                                    type="text"
                                    placeholder="Enter your username"
                                    value={credentials.username}
                                    onChange={handleChange}
                                />
                            </div>

                            <div className="form-control">
                                <label htmlFor="password">Password</label>

                                <div className="password-wrapper">

                                    <input
                                        id="password"
                                        name="password"
                                        type={showPassword ? "text" : "password"}
                                        placeholder="Enter your password"
                                        value={credentials.password}
                                        onChange={handleChange}
                                    />

                                    <button
                                        type="button"
                                        className="show-password-btn"
                                        onClick={() => setShowPassedword(!showPassword)}
                                    >
                                        {showPassword ? "Hide" : "Show"}
                                    </button>
                                </div>
                            </div>

                            <button className="login-btn" type="submit">
                                Sign In
                            </button>

                        </form>
                        <p className='register-footer'>
                            Don't have an account, Click here to make one.{" "}
                            <Link to="/register">Create an account</Link>
                        </p>
                    </div>

                </div>
            </div>
        </div>
    );
}

export default Login