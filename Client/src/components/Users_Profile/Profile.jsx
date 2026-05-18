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

  const handleImageUpload = (e) => {
    const file = e.target.files[0];

    if (!file || !user) return;

    const render = new FileReader();

     render.onloadend = () => {

    setProfileImage(render.result);

    localStorage.setItem(
      `profileImage-${user.userId}`,
      reader.result
    );
  };
    render.readAsDataURL(file);

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

  const upcomingAppointments =
    appointments.filter(a =>
      new Date(a.appointmentDate) > new Date()
    );

  const appointmentHistory =
    appointments.filter(a =>
      new Date(a.appointmentDate) <= new Date()
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