import "../../styles/about.css";
import logo from "../../images/logo.png";
import { useNavigate } from "react-router-dom";

function About() {
    const navigate = useNavigate();

    return (
        <section className="about-page">
            <div className="about-inner">
                <div className="about-content">
                    <p className="about-eyebrow">DeVito Styles</p>

                    <h1 className="about-title">
                        <span className="about-title-line">Precision Cuts.</span>
                        <span className="about-title-line about-title-accent">Timeless Style.</span>
                    </h1>

                    <div className="about-divider"></div>

                    <p className="about-text">
                        At DeVito Styles, we combine classic barbering
                        with modern grooming to deliver sharp cuts,
                        clean fades, and a premium experience every time.
                    </p>

                    {/* Buttons */}
                    <div className="about-actions">
                        <button className="about-btn about-btn--primary" onClick={() => navigate("/services")}>Book Now</button>

                        <button className="about-btn about-btn--secondary" onClick={() => navigate("/contact")}>Contact Us</button>
                    </div>

                    {/* Stats */}
                    <div className="about-stats">
                        <div className="about-stat">
                            <h2>10+</h2>
                            <p>Years Experience</p>
                        </div>

                        <div className="about-stat">
                            <h2>9000+</h2>
                            <p>Clients</p>
                        </div>

                        <div className="about-stat">
                            <h2>5.0★</h2>
                            <p>Rating</p>
                        </div>
                    </div>
                </div>

                <div className="about-visual">
                    <div className="about-image" aria-hidden="true"></div>
                    <img src={logo} alt="DeVito Styles Logo" className="about-logo"
                         onClick={() => navigate("/initial")}/>
                </div>
            </div>
        </section>
    )
}

export default About;