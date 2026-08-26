const BASE_URL = "http://localhost:8080/api/cart";

export async function getCart(userId, token) {
    const response = await fetch(`${BASE_URL}/${userId}`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error("Failed to fetch cart");
    }

    return response.json();

}

export async function updateCartItem(userId, token, productId, quantity) {
    const response = await fetch(`${BASE_URL}/${userId}/items/${productId}`, {
        method: "PUT",
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ quantity })
    });

    if (!response.ok) {
        throw new Error("Failed to update cart item");
    }

    return response.json();
}

export async function removeCartItem(userId, token, productId) {
    const response = await fetch(`${BASE_URL}/${userId}/items/${productId}`, {
        method: "DELETE",
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error("Failed to remove cart item");
    }

    return;
}

export async function addToCart(userId, token, productId, quantity) {
    const response = await fetch(`${BASE_URL}/${userId}/items`, {
        method: "POST",
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ productId, quantity })
    });

    if (!response.ok) {
        throw new Error("Failed to add item to cart");
    }

    return response.json();
}