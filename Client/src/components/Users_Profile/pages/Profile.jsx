import "../../styles/profile.css";
import { useEffect, useState } from "react";

import ProfileCard from "../ProfileCard";
import EditProfileForm from "../EditProfileForm";
import UpcomingAppointments from "../UpcomingAppointments";
import AppointmentHistory from "../AppointmentHistory";
import RescheduleAppointmentForm from "../RescheduleAppointmentForm";

import useUser from "../hooks/useUser";
import useAppointments from "../hooks/useAppointments";

function Profile() {

  const token = localStorage.getItem("token");

  const {
    user,
    formData,
    setFormData,
    loadUser,
    saveUser,
    uploadImage
  } = useUser(token);

  const {
    enriched,
    loadAppointments
  } = useAppointments(token);

  const [editMode, setEditMode] = useState(false);
  const [editingAppointment, setEditingAppointment] = useState(null);
  const [appointmentForm, setAppointmentForm] = useState({
    date: "",
    time: ""
  });

  useEffect(() => {
    const storedUser = JSON.parse(localStorage.getItem("loggedInUser"));

    if (storedUser) {
      loadUser(storedUser.userId);
      loadAppointments(storedUser.userId);
    }
  }, []);

  const now = new Date();

  const upcoming = enriched.filter(a => new Date(a.appointmentDatetime) > now);
  const history = enriched.filter(a => new Date(a.appointmentDatetime) <= now);

  return (
    <div className="profile-page">

      <ProfileCard
        user={user}
        setEditMode={setEditMode}
        handleImageUpload={uploadImage}
      />

      {editMode && (
        <EditProfileForm
          formData={formData}
          handleChange={(e) =>
            setFormData({
              ...formData,
              [e.target.name]: e.target.value
            })
          }
          handleSubmit={saveUser}
          setEditMode={setEditMode}
        />
      )}

      <div className="appointments-wrapper">

        <UpcomingAppointments appointments={upcoming} />

        <AppointmentHistory appointments={history} />

      </div>

    </div>
  );
}

export default Profile;