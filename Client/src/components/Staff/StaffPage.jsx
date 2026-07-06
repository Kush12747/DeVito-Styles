import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "../../styles/StaffPage.css";

function StaffPage() {

    const [barbers, setBarbers] = useState([]);
    const token = localStorage.getItem("token");

    useEffect(() => {
        fetch("http://localhost:8080/api/barber", {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
        .then(r => r.json())
        .then(setBarbers)
        .catch(console.error);
    }, []);

    return (
        <div className="staff-page">

            <section className="staff-header">
                <h1>Meet Our Team</h1>

                <p>
                    Passion. Precesion. Personility.
                    Meet the professionals behind DeVito Styles.
                </p>
            </section>

            <div className="staff-grid">

    {barbers.map(barber => (

        <Link
            to={`/staff/${barber.barberId}`}
            className="staff-card"
            key={barber.barberId}
        >

            <img
                src={barber.imageUrl}
                alt={`${barber.firstName} ${barber.lastName}`}
                className={`staff-image barber-${barber.barberId}`}
            />

            <div className="staff-info">

                <h2>
                    {barber.firstName} {barber.lastName}
                </h2>

                <h4>{barber.title}</h4>

                <div className="staff-divider"></div>

                <div className="staff-detail">

                    <span>★</span>

                    <div>
                        <strong>
                            {new Date().getFullYear() - barber.startYear}+ Years
                        </strong>

                        <small>Experience</small>
                    </div>

                </div>

                <div className="staff-detail">

                    <span>✂</span>

                    <div>
                        <strong>{barber.specialization}</strong>

                        <small>Specialty</small>
                    </div>

                </div>

                <button>
                    View Profile →
                </button>

            </div>

        </Link>

    ))}

</div>
            
        </div>
    );
}

export default StaffPage;