import { Link } from "react-router-dom";
import "../../styles/navbar.css";
import logo from "../../images/logo.png";

function Navbar() {
    return (
    <nav className="navbar">
      
      {/* Left */}
      <div className="nav-left">
        <Link to="/">Home</Link>
        <Link to="/contact">Contact</Link>
        <Link to="/services">Services</Link>
        <Link to="/about">About</Link>
      </div>

      {/* Center */}
      <div className="nav-center">
        <img src={logo} alt="DeVito Styles Logo" className="logo-img" />
      </div>

      {/* Right */}
      <div className="nav-right">
        <Link to="/profile">Profile</Link>
        <button className="logout-btn">Login</button>
      </div>

    </nav>
  );
}

export default Navbar;