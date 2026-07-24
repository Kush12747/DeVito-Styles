import { Link } from "react-router-dom";

function ProductCard({ product }) {
    return (
        <div className="product-card">

            <img src={product.imageUrl} alt={product.name} />

            <h3>{product.name}</h3>

            <p>${product.price}</p>

            <Link
                to={`/products/${product.productId}`}
                key={product.productId}
            >
                <button>View Details</button>
            </Link>
            

        </div>
    );    
}

export default ProductCard;