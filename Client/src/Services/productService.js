const PRODUCT_BASE_URL = "http://localhost:8080/api/products";

export async function fetchProducts({token, keyword="", categoryId = "", sort="name"}) {
    const params = new URLSearchParams();

    if (keyword) {
        params.append("keyword", keyword);
    }

    if (categoryId) {
        params.append("categoryId", categoryId);
    }

    if (sort) {
        params.append("sort", sort);
    }

    const response = await fetch(
        `${PRODUCT_BASE_URL}?${params.toString()}`, 
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        if (!response.ok) {
            throw new Error("Failed to load products");
        }

        return response.json();
}