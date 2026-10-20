import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "../../styles/service.css";
const API_URL = import.meta.env.VITE_API_URL;


const SERVICE_URL = `${API_URL}/api/service`;
const APPOINTMENT_URL = `${API_URL}/api/appointment`;
const BARBER_URL = `${API_URL}/api/barber`;

function BookingPage() {
    const { serviceId } = useParams();
    const navigate = useNavigate();
    const [appointment, setAppointment] = useState(null);

    const [service, setService] = useState({});
    const [barbers, setBarbers] = useState([]);

    const [barberId, setBarberId] = useState("");
    const [time, setTime] = useState("");
    const [date, setDate] = useState("");

    const [bookedAppointments, setBookedAppointments] = useState([]);

    const user = JSON.parse(localStorage.getItem("loggedInUser"));
    const userId = user?.userId;
    const token = localStorage.getItem("token");

    // Load Service
    useEffect(() => {
        fetch(`${SERVICE_URL}/${serviceId}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
        .then(response => response.json())
        .then(data => setService(data))
        .catch(error => console.error("Error fetching service:", error));
    }, [serviceId]);

    // Load Barbers
    useEffect(() => {
        fetch(BARBER_URL, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
        .then(response => response.json())
        .then(data => setBarbers(data))
        .catch(error => console.error("Error fetching barbers:", error));
    }, []);

    // ---------------- AVAILABILITY ----------------
    useEffect(() => {
        if (!barberId || !date) return;

        fetch(`${APPOINTMENT_URL}/availability?barberId=${barberId}&date=${date}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
        .then(res => res.json())
        .then(data => setBookedAppointments(data))
        .catch(err => console.error(err));
    }, [barberId, date]);

    // ---------------- TIME LOGIC ----------------
    const ALL_SLOTS = ["09:00", "10:00", "11:00", "13:00", "15:00"];

    const bookedTimes = bookedAppointments.map(a =>
        a.appointmentDatetime.split("T")[1].slice(0, 5)
    );

    const availableSlots = ALL_SLOTS.filter(
        slot => !bookedTimes.includes(slot)
    );

    // ---------------- SUBMIT ----------------
    const submit = async (e) => {
        e.preventDefault();

        const appointmentDatetime = `${date}T${time}:00`;

        const res = await fetch(APPOINTMENT_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({
                userId,
                barberId: Number(barberId),
                serviceId: Number(serviceId),
                appointmentDatetime
            })
        });

        const data = await res.json();

        if (!res.ok) {
            alert("Failed to create appointment");
            return;
        }

        navigate("/confirmation", {
            state: {
                appointment: data
            }
        });
    };


    return (
        <div className="booking-wrapper">

        <form className="booking-form" onSubmit={submit}>

            <div className="booking-intro">
                <p className="booking-subtitle">DeVito Styles</p>

                <h2>Book: {service.name}</h2>

                <div className="booking-divider"></div>

                <p className="booking-description">
                    Reserve your appointment with one of our professional barbers.<br />
                    Precision cuts. Premium experience.
                </p>
            </div>

            {/* BARBER SELECTION*/}
            <div className="booking-step">
                <span className="booking-step-number">01</span>
                <label>Choose Barber</label>
                <select value={barberId} onChange={(e) => setBarberId(e.target.value)} required>
                    <option value="">Select Barber</option>
                    {barbers.map(b => {
                        return (
                            <option key={b.barberId} value={b.barberId}>
                                {b.firstName} {b.lastName}
                            </option>
                        );
                    })}

                </select>
            </div>

            {/* DATE */}
            <div className="booking-step">
                <span className="booking-step-number">02</span>
                <label>Date</label>
                <input type="date" onChange={(e) => setDate(e.target.value)} required />
            </div>

            {/* TIME (NOW DYNAMIC) */}
            <div className="booking-step">
                <span className="booking-step-number">03</span>
                <label>Time</label>
                <select
                    value={time}
                    onChange={(e) => setTime(e.target.value)}
                    required
                >
                    <option value="">Select time</option>

                    {availableSlots.map(slot => (
                        <option key={slot} value={slot}>
                            {slot}
                        </option>
                    ))}
                </select>
            </div>

            <button type="submit">Confirm Appointment</button>
        </form>
    </div>
    );
}

export default BookingPage;