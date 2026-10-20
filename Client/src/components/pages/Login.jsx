import { useState } from "react"
import { useNavigate, useOutletContext, Link } from "react-router-dom"
import "../../styles/login.css";
import logo from "../../images/logo.png";
const API_URL = import.meta.env.VITE_API_URL;

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
        setErrors([])

        let response
        let payload

        try {
            response = await fetch(`${API_URL}/api/user/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(credentials)
            })

            payload = await response.json().catch(() => null)
        } catch {
            setErrors(["Unable to reach the server. Please try again."])
            return
        }

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
            setErrors(Array.isArray(payload) ? payload : [payload?.message || "Login failed"]);
        }
    }

    return (
        <div className="login-page">
            <div className="login-card">

                <div className="login-header">
                    <img
                        src={logo}
                        alt="Devito Styles Logo"
                        className="login-logo"
                    />

                    <p className="login-eyebrow">Members Entrance</p>

                    <h1 className="login-title">Welcome Back</h1>

                    <p className="login-subtitle">
                        Sign in to your DeVito Styles account
                    </p>
                </div>

                <form className="login-form" onSubmit={handleSubmit}>

                    {errors.length > 0 && (
                        <ul className="login-errors">
                            {errors.map((e) => (
                                <li key={e}>{e}</li>
                            ))}
                        </ul>
                    )}

                    <div className="login-field">
                        <label htmlFor="username">Username</label>

                        <input
                            id="username"
                            name="username"
                            type="text"
                            placeholder="Your username"
                            value={credentials.username}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="login-field">
                        <label htmlFor="password">Password</label>

                        <div className="login-password">
                            <input
                                id="password"
                                name="password"
                                type={showPassword ? "text" : "password"}
                                placeholder="Your password"
                                value={credentials.password}
                                onChange={handleChange}
                            />

                            <button
                                type="button"
                                className="login-toggle"
                                onClick={() => setShowPassword(!showPassword)}
                                aria-label={showPassword ? "Hide password" : "Show password"}
                            >
                                {showPassword ? "Hide" : "Show"}
                            </button>
                        </div>
                    </div>

                    <button className="login-btn" type="submit">
                        Sign In
                    </button>
                </form>

                <p className="login-footer">
                    New to DeVito Styles?{" "}
                    <Link to="/register">Create an account</Link>
                </p>

            </div>
        </div>
    );
}

export default Login