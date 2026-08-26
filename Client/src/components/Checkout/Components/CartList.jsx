import CartItem from "./CartItem";

function CartList({ items, onQuantityUpdate, onRemoveItem }) {
    
    return (
        <div className="cartList">

            {items.map((item) => (
                <CartItem 
                    key={item.product.productId}
                    item={item}
                    onQuantityUpdate={onQuantityUpdate}
                    onRemoveItem={onRemoveItem}
                />
            ))}
        </div>
    );
}

export default CartList;