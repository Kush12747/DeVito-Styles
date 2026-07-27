package learn.DeVitoStyles.models.Products;

import java.util.Objects;

public class ProductUsageStep {

    private int stepId;

    private int productId;

    private int stepNumber;

    private String instruction;

    public int getStepId() {
        return stepId;
    }

    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductUsageStep that)) return false;
        return stepId == that.stepId && productId == that.productId && stepNumber == that.stepNumber && Objects.equals(instruction, that.instruction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stepId, productId, stepNumber, instruction);
    }

    @Override
    public String toString() {
        return "ProductUsageStep{" +
                "stepId=" + stepId +
                ", productId=" + productId +
                ", stepNumber=" + stepNumber +
                ", instruction='" + instruction + '\'' +
                '}';
    }
}
