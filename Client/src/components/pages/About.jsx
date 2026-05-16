import "../../styles/about.css";
import logo from "../../images/logo.png";
import { useNavigate } from "react-router-dom";

function About() {
    const navigate = useNavigate();

    return (
        <section className="backdrop">
            <div className="overlay">
                <div className="about-content">
                    <p className="section-tag">DeVito Styles</p>

                    <h1>
                        Precision Cuts.
                        <br />
                        Timeless Style.
                    </h1>

                    <div className="gold-line"></div>

                    <p className="about-text">
                        At DeVito Styles, we combine classic barbering
                        with modern grooming to deliver sharp cuts,
                        clean fades, and a premium experience every time.
                    </p>

                    {/* Buttons */}
                    <div className="hero-buttons">
                        <button className="primary-btn" onClick={() => navigate("/services")}>Book Now</button>

                        <button className="secondary-btn" onClick={() => navigate("/contact")}>Contact Us</button>
                    </div>

                    {/* Stats */}
                    <div className="stats-container">
                        <div className="stats-card">
                            <h2>10+</h2>
                            <p>Years Experience</p>
                        </div>

                        <div className="stat-card">
                            <h2>9000+</h2>
                            <p>Clients</p>
                        </div>

                        <div className="stat-card">
                            <h2>5.0★</h2>
                            <p>Rating</p>
                        </div>
                    </div>
                </div>
                <div className="logo-side">
                    <img src={logo} alt="DeVito Styles Logo" className="hero-logo"
                         onClick={() => navigate("/initial")}/>
                </div>
            </div>
        </section>
    )
}

export default About;