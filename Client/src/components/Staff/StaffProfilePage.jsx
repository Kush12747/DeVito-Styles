import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { FaStar, FaInstagram } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import "../Staff/Staff-profile.css";
const API_URL = import.meta.env.VITE_API_URL;

function StaffProfilePage() {
    const token = localStorage.getItem("token");
    const navigate = useNavigate();
    const { barberId } = useParams();

    const [barber, setBarber] = useState(null);

    const currentYear = new Date().getFullYear();

    useEffect(() => {
        fetch(`${API_URL}/api/barber/${barberId}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
            .then(r => r.json())
            .then(setBarber)
            .catch(console.error);

    }, [barberId, token]);

    if (!barber) {
        return <h2 className="barber-profile-loading">Loading...</h2>;
    }

    const yearsExperience = barber.startYear
        ? currentYear - barber.startYear
        : null;

    const specialties = barber.specialization
        ? barber.specialization.split(",")
        : [];

    return (
        <div className="barber-profile-page">

            {/* HERO */}
            <section className="barber-hero">

                <div className="barber-hero-image">
                    <img
                        src={barber.imageUrl}
                        alt={`${barber.firstName} ${barber.lastName}`}
                        className="barber-profile-image"
                    />
                </div>

                <div className="barber-hero-content">

                    <h2>{barber.title}</h2>

                    <h1>
                        {barber.firstName} {barber.lastName}
                    </h1>

                    <div className="barber-divider"></div>

                    <p className="trust-line">
                        <span className="trust-stars">
                            <FaStar />
                            <FaStar />
                            <FaStar />
                            <FaStar />
                            <FaStar />
                        </span>
                        {" "}Trusted Professional
                    </p>

                    {yearsExperience && (
                        <p className="experience">
                            {yearsExperience}+ Years Experience
                        </p>
                    )}

                    <div className="barber-hero-buttons">

                        <button className="barber-btn barber-btn--primary" onClick={() => navigate(`/services?barberId=${barber.barberId}`)}>
                            Schedule Appointment
                        </button>

                        {barber.instagramUrl && (
                            <a
                                href={barber.instagramUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="barber-btn barber-btn--secondary"
                            >
                                <FaInstagram /> Instagram
                            </a>
                        )}

                    </div>

                </div>

            </section>

            {/* ABOUT */}

            <section className="profile-section dark">

                <span className="profile-eyebrow">About</span>

                <h2>Craftsmanship. Precision. Attention to Detail.</h2>

                <p className="bio">
                    {barber.bio}
                </p>

            </section>

            {/* SPECIALTIES */}

            {specialties.length > 0 && (

                <section className="profile-section light">

                    <span className="profile-eyebrow">Expertise</span>

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

                <span className="profile-eyebrow">The Experience</span>

                <h2>
                    Why Clients Choose {barber.firstName}
                </h2>

                <ul className="trust-list">
                    <li>Personalized consultation</li>
                    <li>Premium grooming experience</li>
                    <li>Detail-focused craftsmanship</li>
                    <li>Modern &amp; classic techniques</li>
                    <li>Relaxed atmosphere</li>
                </ul>

            </section>

            {/* CTA */}

            <section className="barber-cta-section">

                <span className="profile-eyebrow">Reservations</span>

                <h2>Ready for your next visit?</h2>

                <div className="barber-divider"></div>

                <p>
                    Book an appointment with {barber.firstName} today and
                    experience the DeVito Styles difference.
                </p>

                <button className="barber-btn barber-btn--primary" onClick={() => navigate(`/services?barberId=${barber.barberId}`)}>
                    Schedule Appointment
                </button>

            </section>

        </div>
    );
}

export default StaffProfilePage;