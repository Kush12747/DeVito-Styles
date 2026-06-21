import "../../../styles/profile.css";
import { useEffect, useState } from "react";

import ProfileCard from "../components/profile/ProfileCard";
import UpcomingAppointments from "../components/appointments/UpcomingAppointments";
import AppointmentHistory from "../components/appointments/AppointmentHistory";
import EditProfileForm from "../components/profile/EditProfileForm";

import useUser from "../hooks/useUser";
import useAppointments from "../hooks/useAppointments";

function Profile() {

  const token = localStorage.getItem("token");

  const user = useUser(token);
  const appointments = useAppointments(token);
  const [editMode, setEditMode] = useState(false);

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: ""
  });

  useEffect(() => {
    const storedUser = JSON.parse(
      localStorage.getItem("loggedInUser")
    );

    if (storedUser) {
      user.loadUser(storedUser.userId);
      appointments.loadAppointments(storedUser.userId);
    }

  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    await user.saveUser();

    setEditMode(false);
  }

  const now = new Date();

  const upcoming = appointments.enriched.filter(
    a => new Date(a.appointmentDatetime) > now
  );

  const history = appointments.enriched.filter(
    a => new Date(a.appointmentDatetime) <= now
  );

  const handleComplete = (appointment) => {
    console.log("Complete:", appointment);
  };

  const handleCancel = (appointment) => {
    console.log("Cancel:", appointment);
  };

  const handleReschedule = (appointment) => {
    console.log("Reschedule:", appointment);
  };

  return (
    <div className="profile-page">

      {editMode ? (
      <EditProfileForm
        formData={user.formData}
        handleChange={user.handleChange}
        handleSubmit={handleSubmit}
        setEditMode={setEditMode}
      />
    ) : (
      <ProfileCard
        user={user.user}
        handleImageUpload={user.uploadImage}
        setEditMode={setEditMode}
      />
    )}

      <div className="appointments-wrapper">

        <div className="appointments-card">
          
          <UpcomingAppointments
            appointments={upcoming}
            onComplete={appointments.handleComplete}
            onCancel={appointments.handleCancel}
            onEdit={appointments.handleReschedule}
          />
        </div>
        
          
          <div className="appointments-card">
            
            <AppointmentHistory appointments={history} />
        </div>

      </div>
    </div>
  );
}

export default Profile;