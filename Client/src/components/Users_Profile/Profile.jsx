import "../../styles/profile.css";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import ProfileCard from "./ProfileCard";
import EditProfileForm from "./EditProfileForm";
import UpcomingAppointments from "./UpcomingAppointments";
import AppointmentHistory from "./AppointmentHistory";
import RescheduleAppointmentForm from "./RescheduleAppointmentForm";

function Profile() {

  const [user, setUser] = useState(null);

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: ""
  });

  const [editingAppointment, setEditingAppointment] = useState(null);

  const [appointmentForm, setAppointmentForm] = useState({
    date: "",
    time: "",
  });

  const [editMode, setEditMode] = useState(false);

  const [appointments, setAppointments] = useState([]);

  const [enrichedAppointments, setEnrichedAppointments] = useState([]);

  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  const barberCache = {};
  const serviceCache = {};

  const handleViewConfirmation = (appointment) => {
        console.log("Sending appointment:", appointment);

    navigate("/confirmation", {
      state: {
        appointment
      }
    });
  };


  const handleEditAppointment = (appointment) => {

    setEditingAppointment(appointment);

    const existingDate = new Date(appointment.appointmentDatetime);

    setAppointmentForm({
      date: existingDate.toISOString().split("T")[0],
      time: existingDate.toTimeString().slice(0, 5)
    });
  };

/* COMPLETE APPOINTMENT BTN*/

  const handleCompleteAppointment = async (appointment) => {
    try {
      const response = await fetch(`http://localhost:8080/api/appointment/${appointment.appointmentId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify({
            ...appointment,
            status: "COMPLETED"
          })
        }
      );

      if (response.ok) {
        await fetchAppointments(user.userId);
      }

      } catch (err) {
        console.error(err);
      }
  };


/* CANCEL APPOOINTMENT BTN */
const handleCancelAppointment = async (appointment) => {
  try {
    const response = await fetch(`http://localhost:8080/api/appointment/${appointment.appointmentId}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          ...appointment,
          status: "CANCELLED"
        })
      }
    );

    if (response.ok) {
        await fetchAppointments(user.userId);
      }

  } catch (error) {
    console.log(error);
  }
}

  const handleAppointmentUpdate = async (e) => {
    e.preventDefault();

    const updatedDatetime = `${appointmentForm.date}T${appointmentForm.time}:00`;

    try {
      const response = await fetch(`http://localhost:8080/api/appointment/${editingAppointment.appointmentId}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          ...editingAppointment,
          appointmentDatetime: updatedDatetime,
          status: editingAppointment.status
        })
      }
      );

      if (response.ok) {
        await fetchAppointments(user.userId);
        setEditingAppointment(null);
      }

    } catch (error) {
      console.log(error);
    }
  }

const enrichAppointments = async (appointments) => {

  const enriched = await Promise.all(
    appointments.map(async (a) => {

      // BARBER (cached)
      if (!barberCache[a.barberId]) {
        const res = await fetch(
          `http://localhost:8080/api/barber/${a.barberId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`
            }
          }
        );
        barberCache[a.barberId] = await res.json();
      }

      // SERVICE (cached)
      if (!serviceCache[a.serviceId]) {
        const res = await fetch(
          `http://localhost:8080/api/service/${a.serviceId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`
            }
          }
        );
        serviceCache[a.serviceId] = await res.json();
      }

      const barber = barberCache[a.barberId];
      const service = serviceCache[a.serviceId];

      return {
        ...a,
        barberName: `${barber.firstName} ${barber.lastName}`,
        serviceName: service.name
      };
    })
  );

  setEnrichedAppointments(enriched);
};

  const handleImageUpload = async (e) => {
    const file = e.target.files[0];

    if (!file || !user) return;

    try {

      const formData = new FormData();

      formData.append("file", file);

      const response = await fetch(
        `http://localhost:8080/api/user/${user.userId}/profile-picture`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`
          },
          body: formData
        }
      );

      if (response.ok) {
        const updatedUser = await response.json();

        setUser(updatedUser);

        localStorage.setItem("loggedInUser", JSON.stringify(updatedUser));
      }
    } catch(error) {
      console.log(error);
    }
  }


  useEffect(() => {

    const storedUser =
      JSON.parse(localStorage.getItem("loggedInUser"));

    console.log(storedUser);

    if (storedUser) {
      fetchUser(storedUser.userId);
      fetchAppointments(storedUser.userId);
    }

  }, []);

  const fetchUser = async (id) => {

    try {

      const response =
        await fetch(`http://localhost:8080/api/user/${id}`,
          {
            headers: {
              Authorization: `Bearer ${token}`
            }
          }
        );

      const data = await response.json();

      setUser(data);

      setFormData({
        firstName: data.firstName || "",
        lastName: data.lastName || "",
        email: data.email || "",
        phone: data.phone || ""
      });

    } catch (err) {
      console.error(err);
    }

  };

  const fetchAppointments = async (userId) => {

    try {

      const response = await fetch(
        `http://localhost:8080/api/appointment/user/${userId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`
        }
      }
    );

      const data = await response.json();

      setAppointments(data);

      await enrichAppointments(data);

    } catch (err) {
      console.error(err);
    }

  };

  const handleChange = (e) => {

    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });

  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      const response = await fetch(
        `http://localhost:8080/api/user/${user.userId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify({
            ...user,
            ...formData
          })
        }
      );

      if (response.ok) {

        const updatedUser = await response.json();

        setUser(updatedUser);

        localStorage.setItem(
          "loggedInUser",
          JSON.stringify(updatedUser)
        );

        setEditMode(false);
      }

    } catch (err) {
      console.error(err);
    }

  };

  const now = new Date();

  const upcomingAppointments =
  enrichedAppointments.filter(a =>
    a.status === "BOOKED" || a.status === "CONFIRMED"
  );

const appointmentHistory =
  enrichedAppointments.filter(a =>
    a.status === "COMPLETED" || a.status === "CANCELLED"
  );


    return (
        <div className="profile-page">

            <ProfileCard user={user} setEditMode={setEditMode} handleImageUpload={handleImageUpload} />

            {editMode && (
                <EditProfileForm formData={formData} handleChange={handleChange} handleSubmit={handleSubmit} setEditMode={setEditMode} />
            )}

            <div className="appointments-wrapper">

                <UpcomingAppointments appointments={upcomingAppointments} onEdit={handleEditAppointment} onComplete={handleCompleteAppointment} onCancel={handleCancelAppointment} onViewConfirmation={handleViewConfirmation}/>
                
                {editingAppointment && (
                <RescheduleAppointmentForm
                  appointmentForm={appointmentForm}
                  setAppointmentForm={setAppointmentForm}
                  handleAppointmentUpdate={handleAppointmentUpdate}
                  setEditingAppointment={setEditingAppointment}
                />
              )}
                <AppointmentHistory appointments={appointmentHistory} />

            </div>   
        </div>
    );
}

export default Profile;