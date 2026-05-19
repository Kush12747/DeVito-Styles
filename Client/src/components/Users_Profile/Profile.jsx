import "../../styles/profile.css";
import { useEffect, useState } from "react";

import ProfileCard from "./ProfileCard";
import EditProfileForm from "./EditProfileForm";
import UpcomingAppointments from "./UpcomingAppointments";
import AppointmentHistory from "./AppointmentHistory";

function Profile() {

  const [user, setUser] = useState(null);

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: ""
  });

  const [editMode, setEditMode] = useState(false);

  const [appointments, setAppointments] = useState([]);

  const [profileImage, setProfileImage] = useState("");

  const [enrichedAppointments, setEnrichedAppointments] = useState([]);

  const barberCache = {};
  const serviceCache = {};

const enrichAppointments = async (appointments) => {

  const enriched = await Promise.all(
    appointments.map(async (a) => {

      // BARBER (cached)
      if (!barberCache[a.barberId]) {
        const res = await fetch(
          `http://localhost:8080/api/barber/${a.barberId}`
        );
        barberCache[a.barberId] = await res.json();
      }

      // SERVICE (cached)
      if (!serviceCache[a.serviceId]) {
        const res = await fetch(
          `http://localhost:8080/api/service/${a.serviceId}`
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

  const handleImageUpload = (e) => {
    const file = e.target.files[0];

    if (!file || !user) return;

    const reader = new FileReader();

    reader.onloadend = () => {

    // update the UI instantly
    setProfileImage(reader.result);

    // save to localStorage
    localStorage.setItem(
      `profileImage-${user.userId}`,
      reader.result
    );
  };
    reader.readAsDataURL(file);

    e.target.value = "";
  };


  useEffect(() => {

    const storedUser =
      JSON.parse(localStorage.getItem("loggedInUser"));

    console.log(storedUser);

    if (storedUser) {
      fetchUser(storedUser.userId);
      fetchAppointments(storedUser.userId);
    
      const savedImage = localStorage.getItem(`profileImage-${storedUser.userId}`);

      if (savedImage) {
        setProfileImage(savedImage);
      }
    }

  }, []);

  const fetchUser = async (id) => {

    try {

      const response =
        await fetch(`http://localhost:8080/api/user/${id}`);

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
        `http://localhost:8080/api/appointment/user/${userId}`
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
            "Content-Type": "application/json"
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
    new Date(a.appointmentDatetime) > now
  );

const appointmentHistory =
  enrichedAppointments.filter(a =>
    new Date(a.appointmentDatetime) <= now
  );


    return (
        <div className="profile-page">

            <ProfileCard user={user} setEditMode={setEditMode} profileImage={profileImage} handleImageUpload={handleImageUpload} />

            {editMode && (
                <EditProfileForm formData={formData} handleChange={handleChange} handleSubmit={handleSubmit} setEditMode={setEditMode} />
            )}

            <div className="appointments-wrapper">

                <UpcomingAppointments appointments={upcomingAppointments} />
                <AppointmentHistory appointments={appointmentHistory} />

            </div>   
        </div>
    );
}

export default Profile;