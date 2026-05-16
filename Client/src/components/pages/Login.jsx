import { useState } from "react"
import { useNavigate, useOutletContext } from "react-router-dom"
import "../../styles/login.css";

function Login() {
    const navigate = useNavigate()
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
            const [userJson] = payload.user.split("|")
            const parsedUser = JSON.parse(userJson)

            parsedUser.diyJwt = payload.user

            setLoggedInUser(parsedUser)
            localStorage.setItem("loggedInUser", JSON.stringify(parsedUser))

            navigate("/initial", {
                state: { message: "Login successful" }
            })
        } else {
            setErrors(payload)
        }
    }

    return (
        <div className="bg">
            <div className="login-page">
                <div className="login-card">
                    <h2 className="login-title">Login</h2>
                

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
                                <label>Username:</label>
                                <input
                                    name="username"
                                    value={credentials.username}
                                    onChange={handleChange}
                                    type="text"
                                />
                            </div>

                            <div className="form-control">
                                <label>Password:</label>
                                <input
                                    name="password"
                                    value={credentials.password}
                                    onChange={handleChange}
                                    type="password"
                                />
                            </div>

                            <button className="login-btn" type="submit">Submit</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Login