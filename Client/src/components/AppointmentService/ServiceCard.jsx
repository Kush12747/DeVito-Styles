import { useNavigate } from "react-router-dom";
import "../../styles/service.css";
import classic from "../../images/haircut.png";
import devito from "../../images/devito.jpg";
import skinfade from "../../images/skinfade.png";
import beardTrim from "../../images/beardtrim.png";
import combo from "../../images/combo.jpg";

function ServiceCard({ service }) {
    const navigate = useNavigate();

    const serviceImages = {
        "Classic Haircut": classic,
        "Skin Fade": skinfade,
        "Beard Trim": beardTrim,
        "Haircut and Beard Combo": combo,
        "DeVito Style": devito
    };

    const image = serviceImages[service.name] || classic;

    return (
        <div
    className="service-card"
    onClick={() => navigate(`/book/${service.serviceId}`)}
>
    <div className="service-image-wrapper">
        {service.name?.toLowerCase().replace(/\s/g, "") === "devitostyle" ? (
            <a
                href="https://www.youtube.com/watch?v=QDia3e12czc"
                target="_blank"
                rel="noopener noreferrer"
                onClick={(e) => e.stopPropagation()}
            >
                <img
                    src={image}
                    alt={service.name}
                    className="service-image"
                />
            </a>
        ) : (
            <img
                src={image}
                alt={service.name}
                className="service-image"
            />
        )}
    </div>

    <div className="service-content">
        <h3>{service.name}</h3>

        <p className="description">
            {service.description}
        </p>

        <div className="service-meta">
            <span>{service.durationMinutes} min</span>
            <span className="price">
                ${service.price}
            </span>
        </div>
    </div>
</div>
    );
}

export default ServiceCard;
