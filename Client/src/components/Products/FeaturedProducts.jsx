import { useEffect, useState } from "react";
import ProductGrid from "./ProductGrid";
import { fetchProducts } from "../../Services/productService";

function FeaturedProducts() {

    const [products, setProducts] = useState([]);

    useEffect(() => {
        loadFeaturedProducts();
    }, []);

    async function loadFeaturedProducts() {
        // later call fetchProducts with featured=true
    }

    return (
        <>
            <h2>Featured Products</h2>
            <ProductGrid products={products} />
        </>
    );
}

export default FeaturedProducts;