import { Link } from "react-router-dom";
import "../../styles/landing.css";
import logo from "../../images/logo.png";

function Home() {
    return (
        <div className="home-page">
            <div className="home-card">

                <div className="logo-wrapper">
                    <img src={logo} alt="DeVito Styles" className="home-logo" />
                </div>

                <div className="slogan-box">
                    <p className="home-eyebrow">The Gentleman's Barbershop</p>
                    <h1>Save your Style with DeVito Styles</h1>
                    <span className="home-divider" aria-hidden="true"></span>
                    <p className="home-tagline">Precision cuts, fades and beard grooming, by appointment.</p>
                </div>

                <div className="button-group">
                    <Link to="/login" className="btn">Login</Link>
                    <Link to="/register" className="btn btn-outline">Register</Link>
                </div>

            </div>
        </div>
    )
}

export default Home;
