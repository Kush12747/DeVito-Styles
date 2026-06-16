const BASE_URL = "http://localhost:8080/api/user";

export async function fetchUser(id, token) {
  const res = await fetch(`${BASE_URL}/${id}`, {
    headers: { Authorization: `Bearer ${token}` }
  });

  return res.json();
}

export async function updateUser(id, user, token) {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    },
    body: JSON.stringify(user)
  });

  return res.json();
}

export async function uploadProfilePicture(userId, file, token) {
  const formData = new FormData();
  formData.append("file", file);

  const res = await fetch(`${BASE_URL}/${userId}/profile-picture`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`
    },
    body: formData
  });

  return res.json();
}