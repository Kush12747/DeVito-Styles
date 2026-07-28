package learn.DeVitoStyles.models.Products;

import java.util.Objects;

public class ProductBenefit {

    private int benefitId;

    private int productId;

    private String benefit;

    private int displayOrder;

    public int getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(int benefitId) {
        this.benefitId = benefitId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductBenefit that)) return false;
        return benefitId == that.benefitId && productId == that.productId && displayOrder == that.displayOrder && Objects.equals(benefit, that.benefit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(benefitId, productId, benefit, displayOrder);
    }

    @Override
    public String toString() {
        return "ProductBenefit{" +
                "benefitId=" + benefitId +
                ", productId=" + productId +
                ", benefit='" + benefit + '\'' +
                ", displayOrder=" + displayOrder +
                '}';
    }
}

