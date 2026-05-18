import { useEffect, useState } from "react";
import ServiceCard from "./ServiceCard";
import { data } from "react-router-dom";
import "../../styles/service.css";

const BASE_URL = "http://localhost:8080/api/service";

function Services() {
    const [services, setServices] = useState([]);

    useEffect(() => {
        fetch(BASE_URL)
        .then(response => response.json())
        .then(data => setServices(data))
        .catch(err => console.error("Error loading services:", err));
    }, []);

    return (
        <div className="services-grid">
            {services.map(s => (
                <ServiceCard key={s.serviceId} service={s} />
            ))}
        </div>
    );
}

export default Services;
