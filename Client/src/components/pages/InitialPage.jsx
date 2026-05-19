import { useNavigate } from "react-router-dom";
import "../../styles/initial.css";

function InitialPage() {
    const navigate = useNavigate();

    return (
        <div className="background">
            <div className="hero-container">
                <p className="intro-text">
                    Welcome to
                </p>

                <h1 className="typewriter">
                    DeVito Styles
                </h1>

                <p className="sub-text">
                    Luxury barber experience crafted for your style.
                </p>

                <button className="btn" onClick={() => navigate("/services")}>Book Appointment</button>
            </div>
            
            
            
        </div>
    )
}

export default InitialPage;