import { useNavigate } from "react-router-dom";
import "../Style/CheckoutResultStyle.css";

function CheckoutCancelPage() {

    const navigate = useNavigate();

    return (
        <div className="checkoutResultPage">
            <div className="checkoutResult">

                <div className="checkoutResultIcon">🛒</div>

                <h1>Checkout Cancelled</h1>

                <p>
                    No payment was taken. Your items are still in your cart
                    whenever you're ready.
                </p>

                <div className="checkoutResultActions">
                    <button onClick={() => navigate("/cart")}>
                        Return to Cart
                    </button>

                    <button className="secondary" onClick={() => navigate("/products")}>
                        Keep Browsing
                    </button>
                </div>

            </div>
        </div>
    );
}

export default CheckoutCancelPage;
