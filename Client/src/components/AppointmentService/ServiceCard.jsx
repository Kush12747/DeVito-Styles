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

    const image =
        serviceImages[service.name] || classic;

    return (
        <div className="service-card" onClick={() => navigate(`/book/${service.serviceId}`)}>
            <h3>{service.name}</h3>
            <p>{service.description}</p>
            <p className="duration">{service.durationMinutes} min</p>
            <p className="price">${service.price}</p>

            <div className="service-image-wrapper">
                <img
                    src={image}
                    alt={service.name}
                    className="service-bottom-image"
                />
            </div>
        </div>
    );
}

export default ServiceCard;
