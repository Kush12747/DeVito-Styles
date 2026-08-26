import "../Style/CartHeaderStyle.css";

function CartHeader({ itemCount }) {
    return (
        <div className="cartHeader">
            <h2>
                {itemCount} {itemCount === 1 ? "Item" : "Items"}
            </h2>
        </div>
    );
}

export default CartHeader;