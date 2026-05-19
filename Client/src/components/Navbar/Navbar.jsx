import { Link, useNavigate } from "react-router-dom";
import "../../styles/navbar.css";
import logo from "../../images/logo.png";

function Navbar({ loggedInUser }) {
    const navigate = useNavigate();

    function handleLogout() {
        localStorage.removeItem("loggedInUser");
        navigate("/");
        window.location.reload();
    }

    return (
        <nav className="navbar">
        
            {/* Left */}
            <div className="nav-left">
                {loggedInUser && (
                    <>
                        <Link to="/initial">Home</Link>
                        <Link to="/contact">Contact</Link>
                        <Link to="/services">Services</Link>
                        <Link to="/about">About</Link>
                    </>
                )}
            </div>

            {/* Center */}
            <div className="nav-center">
                <img src={logo} alt="DeVito Styles Logo" className="logo-img" />
            </div>

            {/* Right */}
            <div className="nav-right">
                {loggedInUser && (
                    <>
                        {loggedInUser?.role === "ADMIN" && (
                            <Link to="/admin" className="admin-link">
                                Admin
                            </Link>
                        )}

                        <Link to="/profile">Profile</Link>
                        <button className="logout-btn" onClick={handleLogout}>Logout</button>
                    </>
                )}
            </div>
        </nav>
    );
}

export default Navbar;