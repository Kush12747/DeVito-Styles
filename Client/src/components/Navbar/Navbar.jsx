import { useNavigate, NavLink } from "react-router-dom";
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
                        <NavLink to="/initial" className={({ isActive }) => isActive ? "active-link" : ""}>Home</NavLink>
                        <NavLink to="/contact" className={({ isActive }) => isActive ? "active-link" : ""}>Contact</NavLink>
                        <NavLink to="/services" className={({ isActive }) => isActive ? "active-link" : ""}>Services</NavLink>
                        <NavLink to="/about" className={({ isActive }) => isActive ? "active-link" : ""}>About</NavLink>

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
                            <NavLink to="/admin" className="admin-link">
                                Admin
                            </NavLink>
                        )}

                        <NavLink to="/profile">Profile</NavLink>
                        <button className="logout-btn" onClick={handleLogout}>Logout</button>
                    </>
                )}
            </div>
        </nav>
    );
}

export default Navbar;