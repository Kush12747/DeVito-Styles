package learn.DeVitoStyles.models.Products;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private int productId;
    private int categoryId;

    private String name;
    private String description;

    private BigDecimal price;
    private int stockQuantity;

    private String imageUrl;

    private boolean isFeatured;
    private boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private ProductSpecification specification;

    private List<ProductBenefit> benefits = new ArrayList<>();

    private List<ProductIngredient> ingredients = new ArrayList<>();

    private List<ProductUsageStep> usageSteps = new ArrayList<>();

    public Product(int productId, int categoryId, String name, String description, BigDecimal price, int stockQuantity, String imageUrl, boolean isFeatured, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.isFeatured = isFeatured;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Product() {
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ProductSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(ProductSpecification specification) {
        this.specification = specification;
    }

    public List<ProductBenefit> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<ProductBenefit> benefits) {
        this.benefits = benefits;
    }

    public List<ProductIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<ProductIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<ProductUsageStep> getUsageSteps() {
        return usageSteps;
    }

    public void setUsageSteps(List<ProductUsageStep> usageSteps) {
        this.usageSteps = usageSteps;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", imageUrl='" + imageUrl + '\'' +
                ", isFeatured=" + isFeatured +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", specification=" + specification +
                ", benefits=" + benefits +
                ", ingredients=" + ingredients +
                ", usageSteps=" + usageSteps +
                '}';
    }
}
