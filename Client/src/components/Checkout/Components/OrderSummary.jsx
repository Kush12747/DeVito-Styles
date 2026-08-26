import "../Style/OrderSummaryStyle.css";

function OrderSummary({ subtotal, shipping, total, tax }) {

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
                <span>{tax.toFixed(2)}</span>
            </div>

            <div className="summaryRow">
                <span>Shiping</span>
                <span>
                    {shipping === 0
                        ? "FREE"
                        :  `$${shipping.toFixed(2)}`}
                </span>
            </div>

            <div className="summaryRow">
                <span>Total</span>
                <span>{total.toFixed(2)}</span>
            </div>
            
            <button>
                Proceed to Checkout
            </button>
            
            
        </div>
    );
}

export default OrderSummary;