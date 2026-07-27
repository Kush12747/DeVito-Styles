package learn.DeVitoStyles.models.Products;

import java.util.Objects;

public class ProductSpecification {

    private int productId;

    private String size;
    private String scent;
    private String hairType;
    private String holdStrength;
    private String finish;
    private String countryOfOrigin;
    private String weight;
    private String sku;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getScent() {
        return scent;
    }

    public void setScent(String scent) {
        this.scent = scent;
    }

    public String getHairType() {
        return hairType;
    }

    public void setHairType(String hairType) {
        this.hairType = hairType;
    }

    public String getHoldStrength() {
        return holdStrength;
    }

    public void setHoldStrength(String holdStrength) {
        this.holdStrength = holdStrength;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductSpecification that)) return false;
        return productId == that.productId && Objects.equals(size, that.size) && Objects.equals(scent, that.scent) && Objects.equals(hairType, that.hairType) && Objects.equals(holdStrength, that.holdStrength) && Objects.equals(finish, that.finish) && Objects.equals(countryOfOrigin, that.countryOfOrigin) && Objects.equals(weight, that.weight) && Objects.equals(sku, that.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, size, scent, hairType, holdStrength, finish, countryOfOrigin, weight, sku);
    }

    @Override
    public String toString() {
        return "ProductSpecification{" +
                "productId=" + productId +
                ", size='" + size + '\'' +
                ", scent='" + scent + '\'' +
                ", hairType='" + hairType + '\'' +
                ", holdStrength='" + holdStrength + '\'' +
                ", finish='" + finish + '\'' +
                ", countryOfOrigin='" + countryOfOrigin + '\'' +
                ", weight='" + weight + '\'' +
                ", sku='" + sku + '\'' +
                '}';
    }
}