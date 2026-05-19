import { Link } from "react-router-dom";
import "../../styles/landing.css";
import bg from "../../images/HomeBackground.png";
import logo from "../../images/logo.png";

function Home() {
    return (
        <div className="home-page">
            
            {/* floating particles */}
            <div className="particles">
                <span></span>
                <span></span>
                <span></span>
                <span></span>
                <span></span>
                <span></span>
                <span></span>
                <span></span>
                <span></span>
                <span></span>
                <span></span>
                <span></span>
            </div>

            <div className="home-card">
                <div className="logo-wrapper">
                    <img src={logo} alt="DeVito Styles" className="home-logo" />
                </div>

            <div className="yellow-wrapper">
                <div className="slogan-box">
                    <h1>Save your Style with DeVito Styles</h1>
                </div>
                    <div className="white-card">
                        <div className="button-group">
                            <Link to="/login">
                                <button className="btn">Login</button>
                            </Link>
                            
                            <Link to="/register">
                                <button className="btn">Register</button>
                            </Link>
                        </div>
                    </div>
            </div>
            </div>
        </div>
    )
}

export default Home;