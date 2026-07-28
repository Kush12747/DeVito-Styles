function CategoryFilter({ categories, categoryId, onCategoryChange }) {
    return (
        <select 
            value={categoryId}
            onChange={(e) => onCategoryChange(e.target.value)}
        >
            <option value="">All Categories</option>

            {categories.map(category => (
                <option
                    key={category.categoryId}
                    value={category.categoryId}
                >
                    {category.name}
                </option>
            ))}
        </select>
    );
}

export default CategoryFilter;