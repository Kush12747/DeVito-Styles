import { useState } from "react";
import { fetchUser, updateUser, uploadProfilePicture } from "../services/UserServices";

export default function useUser(token) {
  const [user, setUser] = useState(null);
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: ""
  });

  const loadUser = async (id) => {
    const data = await fetchUser(id, token);
    setUser(data);

    setFormData({
      firstName: data.firstName || "",
      lastName: data.lastName || "",
      email: data.email || "",
      phone: data.phone || ""
    });
  };

  const saveUser = async () => {
    const updated = await updateUser(user.userId, { ...user, ...formData }, token);
    setUser(updated);
    return updated;
  };

  const uploadImage = async (file) => {
    const updated = await uploadProfilePicture(user.userId, file, token);
    setUser(updated);
    return updated;
  };

  return {
    user,
    setUser,
    formData,
    setFormData,
    loadUser,
    saveUser,
    uploadImage
  };
}