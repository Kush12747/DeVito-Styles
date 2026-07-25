import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchProductById } from "../../../Services/productService";

import "../Style/ProductDetailsPage.css";

function ProductDetailsPage() {

    const { productId } = useParams();
    const [product, setProduct] = useState(null);

    const token = localStorage.getItem("token");

    useEffect(() => {
        loadProduct();
    }, [productId]);

    async function loadProduct() {
        try {
            const data = await fetchProductById(productId, token);
            setProduct(data);
        } catch (err) {
            console.error(err);
        }
    }

    if (!product) {
        return <p>Loading...</p>;
    }

    return (
    <div className="product-details-page">

        <button className="back-button">
            ← Back to Products
        </button>

        <section className="product-details">

            <div className="product-image-section">

                <img
                    src={product.imageUrl}
                    alt={product.name}
                    className="product-image"
                />

            </div>

            <div className="product-info">

                {product.featured && (
                    <span className="featured-badge">
                        ★ Featured
                    </span>
                )}

                <h1>{product.name}</h1>

                <h2 className="price">
                    ${product.price}
                </h2>

                <p className="description">
                    {product.description}
                </p>

                <div className="product-meta">

                    <div className="meta-card">
                        <span className="meta-label">🚚 Shipping</span>
                        <span className="meta-value">2 - 3 business days</span>
                    </div>

                    <div className="meta-card">
                        <span className="meta-label">Status</span>
                        <span
                            className={
                                product.stockQuantity > 0
                                    ? "meta-value in-stock"
                                    : "meta-value out-of-stock"
                            }
                        >
                            {product.stockQuantity > 0 ? "In Stock" : "Out of Stock"}
                        </span>
                    </div>

                    <div className="meta-card">
                        <span className="meta-label">📦 Stock</span>
                        <span className="meta-value">
                            {product.stockQuantity} Left
                        </span>
                    </div>

                </div>

                <div className="quantity-container">

                    <button>-</button>

                    <span>1</span>

                    <button>+</button>

                </div>

                <button className="cart-button">
                    Add to Cart
                </button>

            </div>

        </section>

        <section className="related-products">

            <h2>You May Also Like</h2>

            {/* ProductGrid goes here later */}

        </section>

        <section className="reviews">

            <h2>Customer Reviews</h2>

        </section>

    </div>
);
}

export default ProductDetailsPage;