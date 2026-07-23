function ProductCard({ product }) {
    return (
        <div className="product-card">

            <img src={product.imageUrl} alt={product.name} />

            <h3>{product.name}</h3>

            <p>${product.price}</p>

            <button>View Details</button>

        </div>
    );    
}

export default ProductCard;