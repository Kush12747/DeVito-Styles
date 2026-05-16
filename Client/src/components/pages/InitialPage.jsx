import { useNavigate } from "react-router-dom";
import "../../styles/initial.css";

function InitialPage() {
    const navigate = useNavigate();

    return (
        <div className="background">
            <div className="hero-container">
                <h1>Welcome to DeVito Styles</h1>

                <p>Your one-stop shop for all your fashion needs!</p>

                <button className="btn" onClick={() => navigate("/services")}>Book Appointment</button>
            </div>
            
            
            
        </div>
    )
}

export default InitialPage;