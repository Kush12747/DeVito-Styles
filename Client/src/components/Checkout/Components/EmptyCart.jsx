import { useNavigate } from "react-router-dom";
import "../Style/EmptyCartStyle.css";

function EmptyCart() {

    const navigate = useNavigate();

    return (
        <div className="emptyCart">
            <div className="emptyCartIcon">
                🛒
            </div>

            <h2>Your Cart is Empty</h2>

            <p>You haven't added any products yet.
                Browse our grooming collection and find something you like.
            </p>

            <button onClick={() => navigate("/products")}>
                Browse Products
            </button>
        </div>
    );
}

export default EmptyCart;