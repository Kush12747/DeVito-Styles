import { useState } from "react";
import { fetchAppointments } from "../services/appointmentServices";

export default function useAppointments(token) {
  const [appointments, setAppointments] = useState([]);
  const [enriched, setEnriched] = useState([]);

  const barberCache = {};
  const serviceCache = {};

  const enrich = async (data) => {
    const result = await Promise.all(
      data.map(async (a) => {

        if (!barberCache[a.barberId]) {
          const res = await fetch(
            `http://localhost:8080/api/barber/${a.barberId}`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          barberCache[a.barberId] = await res.json();
        }

        if (!serviceCache[a.serviceId]) {
          const res = await fetch(
            `http://localhost:8080/api/service/${a.serviceId}`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          serviceCache[a.serviceId] = await res.json();
        }

        return {
          ...a,
          barberName: `${barberCache[a.barberId].firstName} ${barberCache[a.barberId].lastName}`,
          serviceName: serviceCache[a.serviceId].name
        };
      })
    );

    setEnriched(result);
  };

  const loadAppointments = async (userId) => {
    const data = await fetchAppointments(userId, token);
    setAppointments(data);
    await enrich(data);
  };

  return {
    appointments,
    enriched,
    loadAppointments,
    setEnriched
  };
}