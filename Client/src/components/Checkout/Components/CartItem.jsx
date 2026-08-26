import "../Style/CartItemStyle.css";

function CartItem({ item, onQuantityUpdate, onRemoveItem }) {

    const product = item.product;

    const itemSubtotal = product.price * item.quantity;

    function decreaseQuantity() {
        if (item.quantity <= 1) {
            return;
        }

        onQuantityUpdate(product.productId, item.quantity - 1);
    }

    function increaseQuantity() {
        onQuantityUpdate(product.productId, item.quantity + 1);
    }

    return (
        <div className="cartItem">

            <div className="productImage">
                <img 
                    src={product.imageUrl}
                    alt={product.name}
                />
            </div>

            <div className="productInfo">
                <h3>
                    {product.name}
                </h3>

                <p>
                    {product.description}
                </p>

                <p>
                    Price: ${product.price}
                </p>

            </div>

            <div className="quantitySelection">
                <button onClick={decreaseQuantity}>
                    -
                </button>

                <span>{item.quantity}</span>

                <button onClick={increaseQuantity}>
                    +
                </button>

            </div>

            <div className="subtotal">
                <p>${itemSubtotal.toFixed(2)}</p>
            </div>

            <button 
                className="removeButton"
                onClick={() => onRemoveItem(product.productId)}
            >
                Remove
            </button>

        </div>
    );
}

export default CartItem;