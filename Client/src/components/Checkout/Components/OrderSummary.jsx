import "../Style/OrderSummaryStyle.css";

function OrderSummary({ subtotal, shipping, total, tax, onCheckout, checkingOut, error }) {

    subtotal = Number(subtotal);
    tax = Number(tax);
    shipping = Number(shipping);
    total = Number(total);
    return (

        <div className="orderSummary">

            <h2>Order Summary</h2>

            <div className="summaryRow">
                <span>Subtotal</span>
                <span>${subtotal.toFixed(2)}</span>
            </div>

            <div className="summaryRow">
                <span>Tax</span>
                <span>${tax.toFixed(2)}</span>
            </div>

            <div className="summaryRow">
                <span>Shipping</span>
                <span>
                    {shipping === 0
                        ? "FREE"
                        :  `$${shipping.toFixed(2)}`}
                </span>
            </div>

            <div className="summaryRow">
                <span>Total</span>
                <span>${total.toFixed(2)}</span>
            </div>

            {error && (
                <p className="checkoutError">{error}</p>
            )}

            <button onClick={onCheckout} disabled={checkingOut}>
                {checkingOut ? "Redirecting to payment..." : "Proceed to Checkout"}
            </button>


        </div>
    );
}

export default OrderSummary;
