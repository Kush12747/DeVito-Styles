import { useNavigate, useSearchParams } from "react-router-dom";
import "../Style/CheckoutResultStyle.css";

function CheckoutSuccessPage() {

    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const orderNumber = searchParams.get("order");

    return (
        <div className="checkoutResultPage">
            <div className="checkoutResult">

                <div className="checkoutResultIcon" aria-hidden="true">✓</div>

                <h1>Thank You for Your Order!</h1>

                <p>
                    Your payment was received. We'll get your products ready
                    and send you a confirmation shortly.
                </p>

                {orderNumber && (
                    <p>
                        Order number: <span className="orderNumber">{orderNumber}</span>
                    </p>
                )}

                <div className="checkoutResultActions">
                    <button onClick={() => navigate("/products")}>
                        Continue Shopping
                    </button>
                </div>

            </div>
        </div>
    );
}

export default CheckoutSuccessPage;
