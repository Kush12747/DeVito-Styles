const CATEGORY_BASE_URL = "http://localhost:8080/api/categories";

export async function fetchCategories(token) {
    const response = await fetch(CATEGORY_BASE_URL, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        throw new Error("Failed to load categories");
    }

    return response.json();
}