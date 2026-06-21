import { useState, useRef } from "react";
import {
  fetchAppointments,
  updateAppointment,
  completeAppointment,
  cancelAppointment,
  rescheduleAppointment
} from "../services/appointmentService";

export default function useAppointments(token) {

  const [enriched, setEnriched] = useState([]);

  const barberCache = useRef({});
  const serviceCache = useRef({});

  const handleComplete = async (appointment) => {
  await completeAppointment(appointment, token);
  await loadAppointments(appointment.userId);
};

const handleCancel = async (appointment) => {
  await cancelAppointment(appointment, token);
  await loadAppointments(appointment.userId);
};

const handleReschedule = async (appointment, newDateTime) => {
  await rescheduleAppointment(appointment, newDateTime, token);
  await loadAppointments(appointment.userId);
};

  const enrich = async (appointments) => {

    const result = await Promise.all(
      appointments.map(async (appointment) => {

        if (!barberCache.current[appointment.barberId]) {

          const response = await fetch(
            `http://localhost:8080/api/barber/${appointment.barberId}`,
            {
              headers: {
                Authorization: `Bearer ${token}`
              }
            }
          );

          barberCache.current[appointment.barberId] =
            await response.json();
        }

        if (!serviceCache.current[appointment.serviceId]) {

          const response = await fetch(
            `http://localhost:8080/api/service/${appointment.serviceId}`,
            {
              headers: {
                Authorization: `Bearer ${token}`
              }
            }
          );

          serviceCache.current[appointment.serviceId] =
            await response.json();
        }

        const barber =
          barberCache.current[appointment.barberId];

        const service =
          serviceCache.current[appointment.serviceId];

        return {
          ...appointment,
          barberName: `${barber.firstName} ${barber.lastName}`,
          serviceName: service.name
        };
      })
    );

    setEnriched(result);
  };

  const loadAppointments = async (userId) => {

    const appointments =
      await fetchAppointments(userId, token);

    await enrich(appointments);
  };

  return {
    enriched,
    loadAppointments,
    handleComplete,
    handleCancel,
    handleReschedule
  };
}