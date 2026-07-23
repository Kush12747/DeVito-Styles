import { useEffect, useState } from 'react';
import "../Style/ProductsPage.css";
import { fetchProducts } from '../../../Services/productService';
import ProductGrid from '../ProductGrid';
import ProductCard from '../ProductCard';
import ProductSearch from '../ProductSearch';
import CategoryFilter from '../CategoryFilter';
import SortDropDown from '../SortDropDown';

function ProductsPage() {

    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);

    const [search, setSearch] = useState("");
    const [categoryId, setCategoryId] = useState("");
    const [sort, setSort] = useState("name");

    const token = localStorage.getItem("token");

    useEffect(() => {
        loadProducts();
    }, []);

    const loadProducts = async () => {
        try {
            const data = await fetchProducts(token);
            setProducts(data);
        } catch (err) {
            console.error(err);
        }
    }

    // if (loading) {
    // return (
    //     <div className="product-grid">
    //         <ProductSkeleton />
    //         <ProductSkeleton />
    //         <ProductSkeleton />
    //         <ProductSkeleton />
    //     </div>
    // );
    // }

    return (
        <div className="products-page">

            <header className="products-header">
                <h1>Products</h1>
                <p>Professional barber products for every style.</p>
            </header>

            <section className="products-toolbar">
                <ProductSearch
                    search={search}
                    onSearchChange={setSearch}
                />

                <CategoryFilter  categories={categories} categoryId={categoryId} onCategoryChange={setCategoryId}/>

                <SortDropDown sort={sort} onSortChange={setSort} />

            </section>

            <section className="products-content">
                <ProductGrid products={products} />
            </section>
            
            <ProductSearch search={search} setSearch={setSearch} />

        
        </div>
    );
}

export default ProductsPage;