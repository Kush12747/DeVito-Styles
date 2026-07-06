import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import "../Staff/Staff-profile.css";

function StaffProfilePage() {
    
    const token = localStorage.getItem("token");
    const { barberId } = useParams();
    const [barber, setBarber] = useState(null);

    useEffect(() => {
        fetch(`http://localhost:8080/api/barber/${barberId}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
        .then(r => r.json())
        .then(setBarber)
        .catch(console.error)
    }, [barberId, token]);

    if (!barber) {
        return <h2>Loading...</h2>
    }
    
    return (
        <div className="staff-profile">

            <img src={barber.imageUrl} alt={`${barber.firstName} ${barber.lastName}`} className="profile-image" />
            
            <div className="profile-info">
                
                <h1>{barber.firstName} {barber.lastName}</h1>

                <h2>{barber.title}</h2>

                <p>{barber.bio}</p>
            </div>
            
        </div>
    );
}

export default StaffProfilePage;