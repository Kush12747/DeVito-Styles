import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { FaStar, FaInstagram } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import "../Staff/Staff-profile.css";

function StaffProfilePage() {
    const token = localStorage.getItem("token");
    const navigate = useNavigate();
    const { barberId } = useParams();

    const [barber, setBarber] = useState(null);

    const currentYear = new Date().getFullYear();

    useEffect(() => {
        fetch(`http://localhost:8080/api/barber/${barberId}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
            .then(r => r.json())
            .then(setBarber)
            .catch(console.error);

    }, [barberId, token]);

    if (!barber) {
        return <h2 className="loading">Loading...</h2>;
    }

    const yearsExperience = barber.startYear
        ? currentYear - barber.startYear
        : null;

    const specialties = barber.specialization
        ? barber.specialization.split(",")
        : [];

    return (
        <div className="profile-page">

            {/* HERO */}
            <section className="hero">

                <div className="hero-image">
                    <img
                        src={barber.imageUrl}
                        alt={`${barber.firstName} ${barber.lastName}`}
                        className="profile-image"
                    />
                </div>

                <div className="hero-content">

                    <h1>
                        {barber.firstName} {barber.lastName}
                    </h1>

                    <h2>{barber.title}</h2>

                    <p className="trust-line">
                        <FaStar />
                        <FaStar />
                        <FaStar />
                        <FaStar />
                        <FaStar />
                        {" "}Trusted Professional
                    </p>

                    {yearsExperience && (
                        <p className="experience">
                            {yearsExperience}+ Years Experience
                        </p>
                    )}

                    <div className="hero-buttons">

                        <button className="primary-btn" onClick={() => navigate(`/services?barberId=${barber.barberId}`)}>
                            Schedule Appointment
                        </button>

                        {barber.instagramUrl && (
                            <a
                                href={barber.instagramUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="secondary-btn"
                            >
                                <FaInstagram /> Instagram
                            </a>
                        )}

                    </div>

                </div>

            </section>

            {/* ABOUT */}

            <section className="profile-section dark">

                <h2>Craftsmanship. Precision. Attention to Detail.</h2>

                <p className="bio">
                    {barber.bio}
                </p>

            </section>

            {/* SPECIALTIES */}

            {specialties.length > 0 && (

                <section className="profile-section light">

                    <h2>Specialties</h2>

                    <div className="specialties">

                        {specialties.map((specialty, index) => (

                            <span
                                key={index}
                                className="specialty-tag"
                            >
                                {specialty.trim()}
                            </span>

                        ))}

                    </div>

                </section>

            )}

            {/* WHY CLIENTS CHOOSE */}

            <section className="profile-section dark">

                <h2>
                    Why Clients Choose {barber.firstName}
                </h2>

                <ul className="trust-list">
                    <li>✓ Personalized consultation</li>
                    <li>✓ Premium grooming experience</li>
                    <li>✓ Detail-focused craftsmanship</li>
                    <li>✓ Modern & classic techniques</li>
                    <li>✓ Relaxed atmosphere</li>
                </ul>

            </section>

            {/* CTA */}

            <section className="cta-section">

                <h2>Ready for your next visit?</h2>

                <p>
                    Book an appointment with {barber.firstName} today and
                    experience the DeVito Styles difference.
                </p>

                <button className="primary-btn glow" onClick={() => navigate(`/services?barberId=${barber.barberId}`)}>
                    Schedule Appointment
                </button>

            </section>

        </div>
    );
}

export default StaffProfilePage;