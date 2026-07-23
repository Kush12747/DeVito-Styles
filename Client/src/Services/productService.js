const PRODUCT_BASE_URL = "http://localhost:8080/api/products";

export async function fetchProducts(token) {
    const response = await fetch(PRODUCT_BASE_URL, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error("Failed to load products");
            }

            return response.json();
}