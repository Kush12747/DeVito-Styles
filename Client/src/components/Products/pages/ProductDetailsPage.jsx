import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { fetchProductById, fetchRelatedProducts } from "../../../Services/productService";
import { addToCart } from "../../../Services/cartService";

import "../Style/ProductDetailsPage.css";

function ProductDetailsPage() {

    const { productId } = useParams();
    const [product, setProduct] = useState(null);
    const [relatedProducts, setRelatedProducts] = useState([]);
    const [quantity, setQuantity] = useState(1);
    const navigate = useNavigate();

    const token = localStorage.getItem("token");

    const user = JSON.parse(localStorage.getItem("loggedInUser"));
    const userId = user?.userId;

    function increaseQuantity() {
        if (quantity >= product.stockQuantity) {
            return;
        }

        setQuantity(quantity + 1);
    }

    function decreaseQuantity() {
        if (quantity <= 1) {
            return;
        }

        setQuantity(quantity - 1);
    }

    // add products to cart function
    async function handleAddToCart() {
        try {
            await addToCart(userId, token, product.productId, quantity);

            alert("Added to cart");
        } catch (error) {
            console.error(error);
        }
    }

    useEffect(() => {
        async function loadProduct() {
            const product = await fetchProductById(productId, token);
            setProduct(product);

            const related = await fetchRelatedProducts(productId, token);
            setRelatedProducts(related);
        }
        
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

            <button className="back-button" onClick={() => navigate("/products")}>
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

                        <button onClick={decreaseQuantity}>
                            -
                        </button>

                        <span>{quantity}</span>

                        <button onClick={increaseQuantity}>
                            +
                        </button>

                    </div>

                    <button className="cart-button" onClick={handleAddToCart}>
                        Add to Cart
                    </button>
                </div>
            </section>

                <section className="product-section">
                        <h3>Why You'll Love It</h3>
                        
                        <ul className="benefits-lists">
                            {product.benefits.map(benefit => (
                                <li key={benefit.benefitId}>✓ {benefit.benefit}</li>
                            ))}
                        </ul>
                </section>
                
                <section className="product-section">

                    <h3>Specifications</h3>

                    <div className="spec-grid">

                        <div>
                            <span>Size</span>
                            <strong>{product.specification?.size}</strong>
                        </div>

                        <div>
                            <span>Scent</span>
                            <strong>{product.specification?.scent}</strong>
                        </div>

                        <div>
                            <span>Hair Type</span>
                            <strong>{product.specification?.hairType}</strong>
                        </div>

                        <div>
                            <span>Hold</span>
                            <strong>{product.specification?.holdStrength}</strong>
                        </div>

                        <div>
                            <span>Finish</span>
                            <strong>{product.specification?.finish}</strong>
                        </div>

                        <div>
                            <span>Weight</span>
                            <strong>{product.specification?.weight}</strong>
                        </div>

                    </div>

                </section>

                <section className="product-section">

                    <h3>Ingredients</h3>

                    <div className="ingredient-tags">

                        {product.ingredients.map(ingredient => (

                            <span
                                key={ingredient.ingredientId}
                                className="ingredient-tag"
                            >
                                {ingredient.ingredient}
                            </span>

                        ))}

                    </div>

                </section>

                <section className="product-section">

                    <h3>How to Use</h3>

                    <ol className="usage-steps">

                        {product.usageSteps.map(step => (

                            <li key={step.stepId}>
                                {step.instruction}
                            </li>

                        ))}

                    </ol>

                </section>
            

            <section className="related-products">

                <h2>You May Also Like</h2>

                <div className="related-grid">
                    {relatedProducts.map(product => (
                        <div
                            key={product.productId}
                            className="related-card"
                            onClick={() => navigate(`/products/${product.productId}`)}
                        >
                            <img 
                                src={product.imageUrl}
                                alt={product.name}
                                className="related-card"
                            />

                            <h3>{product.name}</h3>
                            <p>${product.price.toFixed(2)}</p>
                        </div>
                    ))}
                </div>
                

            </section>

            <section className="reviews">

                <h2>Customer Reviews</h2>

                <p>Reviews coming soon.</p>

            </section>
        </div>
);
}

export default ProductDetailsPage;


/*
ProductDetailsPage
│
├── Product Header
│     ├── Image
│     ├── Name
│     ├── Price
│     ├── Description
│     ├── Shipping
│     ├── Stock
│     ├── Quantity
│     └── Add To Cart
│
├── Benefits
│
├── Specifications
│
├── Ingredients
│
├── How To Use
│
├── Related Products
│
└── Reviews
*/