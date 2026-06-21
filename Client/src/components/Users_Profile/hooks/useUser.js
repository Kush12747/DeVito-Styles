import { useState } from "react";
import { fetchUser, updateUser, uploadProfilePicture } from "../services/userServices";

export default function useUser(token) {

  const [user, setUser] = useState(null);

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: ""
  });

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const loadUser = async (id) => {
    try {

      const data = await fetchUser(id, token);

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

  const saveUser = async () => {
    try {

      const updated = await updateUser(
        user.userId,
        {
          ...user,
          ...formData
        },
        token
      );

      setUser(updated);

      localStorage.setItem(
        "loggedInUser",
        JSON.stringify(updated)
      );

      return updated;

    } catch (err) {
      console.error(err);
    }
  };

  const uploadImage = async (file) => {
    
    if (!file) {
        return;
    }
    
    try {

      const updated = await uploadProfilePicture(
        user.userId,
        file,
        token
      );

      setUser(updated);

      localStorage.setItem(
        "loggedInUser",
        JSON.stringify(updated)
      );

      return updated;
        console.log(file);

    } catch (err) {
      console.error(err);
    }
  };

  return {
    user,
    formData,
    setFormData,
    loadUser,
    saveUser,
    uploadImage,
    handleChange
  };
}