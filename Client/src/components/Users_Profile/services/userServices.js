const BASE_URL = "http://localhost:8080/api/user";

async function handleResponse(res) {
  let data = null;

  try {
    data = await res.json();
  } catch {
    data = null;
  }

  if (!res.ok) {
    const message =
      data?.message ||
      `Request failed with status ${res.status}`;

    throw new Error(message);
  }

  return data;
}

export async function fetchUser(id, token) {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`
    }
  });

  return handleResponse(res);
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

  return handleResponse(res);
}

export async function uploadProfilePicture(userId, file, token) {
  console.log("File:", file);

  const formData = new FormData();
  formData.append("file", file);

  for (const pair of formData.entries()) {
    console.log(pair[0], pair[1]);
  }

  const res = await fetch(
    `${BASE_URL}/${userId}/profile-picture`,
    {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`
      },
      body: formData
    }
  );

  return handleResponse(res);
}