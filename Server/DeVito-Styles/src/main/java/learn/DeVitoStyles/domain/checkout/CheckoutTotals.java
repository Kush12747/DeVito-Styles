package learn.DeVitoStyles.domain.checkout;

import java.math.BigDecimal;


/*
 * Holds all monetary values calculated during checkout.
 *
 * Instead of passing subtotal, tax, shipping, discount and total
 * around as separate variables, we keep them together in one object.
 */
public class CheckoutTotals {

    private final BigDecimal subtotal;
    private final BigDecimal tax;
    private final BigDecimal shipping;
    private final BigDecimal discount;
    private final BigDecimal total;


    public CheckoutTotals(
            BigDecimal subtotal,
            BigDecimal tax,
            BigDecimal shipping,
            BigDecimal discount,
            BigDecimal total
    ) {
        this.subtotal = subtotal;
        this.tax = tax;
        this.shipping = shipping;
        this.discount = discount;
        this.total = total;
    }


    public BigDecimal getSubtotal() {
        return subtotal;
    }


    public BigDecimal getTax() {
        return tax;
    }


    public BigDecimal getShipping() {
        return shipping;
    }


    public BigDecimal getDiscount() {
        return discount;
    }


    public BigDecimal getTotal() {
        return total;
    }
}