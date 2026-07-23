function SortDropDown({ sort, onSortChange }) {
    return (
        <select
            value={sort}
            onChange={(e) => onSortChange(e.target.value)}
        >

            <option value="name">Name</option>
            <option value="priceAsc">Price: Low to High</option>
            <option value="priceDesc">Price: High to Low</option>
            
        </select>
    );
}

export default SortDropDown;