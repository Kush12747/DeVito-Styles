import ProductCard from "./ProductCard";

function ProductGrid({ products }) {
    return (
        <div className="product-grid">
            {products.map(product => (
                <ProductCard key={product.productId} product={product} />
            ))}
        </div>
    );
}

export default ProductGrid;