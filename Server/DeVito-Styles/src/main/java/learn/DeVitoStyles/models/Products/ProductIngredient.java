package learn.DeVitoStyles.models.Products;

import java.util.Objects;

public class ProductIngredient {

    private int ingredientId;

    private int productId;

    private String ingredient;

    private int displayOrder;

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductIngredient that)) return false;
        return ingredientId == that.ingredientId && productId == that.productId && displayOrder == that.displayOrder && Objects.equals(ingredient, that.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredientId, productId, ingredient, displayOrder);
    }

    @Override
    public String toString() {
        return "ProductIngredient{" +
                "ingredientId=" + ingredientId +
                ", productId=" + productId +
                ", ingredient='" + ingredient + '\'' +
                ", displayOrder=" + displayOrder +
                '}';
    }
}