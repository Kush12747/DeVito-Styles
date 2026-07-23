function ProductSearch({ search, onSearchChange }) {
    return (
        <input 
            type="text" 
            placeholder="Search Products..." 
            value={search} 
            onChange={(e) => onSearchChange(e.target.value)} 
        />
    );
}

export default ProductSearch;