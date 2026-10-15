const BASE_URL = `${import.meta.env.VITE_API_URL}/api/checkout`;

// Creates a pending order on the backend and returns the Stripe Checkout session info:
// { checkoutUrl, orderId, orderNumber, amount }
export async function startCheckout(cartId, token) {
    const response = await fetch(BASE_URL, {
        method: "POST",
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ cartId })
    });

    if (!response.ok) {
        const messages = await response.json().catch(() => null);

        throw new Error(
            Array.isArray(messages) && messages.length > 0
                ? messages.join(" ")
                : "Unable to start checkout"
        );
    }

    return response.json();
}
