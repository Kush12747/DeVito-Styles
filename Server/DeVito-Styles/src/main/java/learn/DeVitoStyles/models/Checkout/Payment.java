package learn.DeVitoStyles.models.Checkout;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private int paymentId;

    private int orderId;

    private String paymentProvider;

    private String paymentIntentId;

    private PaymentStatus status;

    private BigDecimal amount;

    private String currency;

    private LocalDateTime paidAt;

    private LocalDateTime createdAt;

    public Payment() {
    }

    public Payment(int paymentId, int orderId, String paymentProvider, String paymentIntentId, PaymentStatus status,
                   BigDecimal amount, String currency, LocalDateTime paidAt, LocalDateTime createdAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentProvider = paymentProvider;
        this.paymentIntentId = paymentIntentId;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
