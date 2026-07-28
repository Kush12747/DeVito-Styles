import { Link } from "react-router-dom";

function ProductCard({ product }) {

    return (
        <div className="product-card">
            

            {product.featured && (
                <span className="featured-badge">
                    ★ Featured
                </span>
            )}


            <img 
                src={product.imageUrl}
                alt={product.name}
            />


            <div className="product-card-content">


                <span className="product-category">
                    {product.categoryName || "Barber Product"}
                </span>


                <h3>
                    {product.name}
                </h3>


                <p className="product-description">
                    {product.description}
                </p>


                <div className="product-price">
                    ${product.price}
                </div>


                <div 
                    className={
                        product.stockQuantity > 0
                        ? "stock-status in-stock"
                        : "stock-status out-stock"
                    }
                >
                    {
                        product.stockQuantity > 0
                        ? `${product.stockQuantity} Available`
                        : "Out of Stock"
                    }
                </div>


                <Link
                    to={`/products/${product.productId}`}
                    className="view-product-button"
                >
                    View Details
                </Link>


            </div>

        </div>
    );
}

export default ProductCard;