import "../Style/CartPageStyle.css";
import CartHeader from "../Components/CartHeader";
import CartList from "../Components/CartList";
import OrderSummary from "../Components/OrderSummary";
import EmptyCart from "../Components/EmptyCart";

import { useEffect, useState } from "react";
import { getCart, updateCartItem, removeCartItem } from "../../../Services/cartService";

function CartPage() {

    const user = JSON.parse(localStorage.getItem("loggedInUser"));
    const userId = user?.userId;
    const token = localStorage.getItem("token");

    const [loading, setLoading] = useState(true);

    const [cart, setCart] = useState({
        cartId: null,
        items: [],
        subtotal: 0,
        shipping: 0,
        total: 0,
        tax: 0
    });

    async function fetchCart() {
        try {
            const cartData = await getCart(userId, token);

            const subtotal = cartData.items.reduce((sum, item) => (sum + (item.product.price * item.quantity), 0));

            const tax = subtotal * 0.08;

            const shipping = 0;

            const total = subtotal + tax + shipping;

            setCart({
                ...cartData,
                subtotal,
                tax,
                shipping,
                total
            });

        } catch (error) {
            console.error(error);
        } finally {

            setLoading(false);
        }
    }

    async function handleQuantityUpdate(productId, quantity) {
        try {
            await updateCartItem(userId, token, productId, quantity);

            await fetchCart();
        } catch (error) {
            console.error(error);
        }
    }

    async function handleRemoveItem(productId) {
        try {
            await removeCartItem(userId, token, productId);

            await fetchCart();
        } catch (error) {
            console.error(error);
        }
    }

    useEffect(() => {

        if (!userId || !token) {
            return;
        }

        fetchCart();
    }, [userId, token]);

    if (loading) {
        return <h2>Loading</h2>
    }
    
    return (
        <div className="cartPage">

            <div className="cartTitle">
                <h1>Shopping Cart</h1>
            </div>

            {cart.items.length === 0 ? (
                
                <EmptyCart />
            
            ) : (
                <>
                    <CartHeader itemCount={cart.items.length}/>

                    <div className="contentWrapper">

                        <div className="leftBar">
                            <CartList 
                                items={cart.items}
                                onQuantityUpdate={handleQuantityUpdate}
                                onRemoveItem={handleRemoveItem}
                            />
                        </div>

                        <div className="rightBar">
                            <OrderSummary 
                                subtotal={cart.subtotal}
                                shipping={cart.shipping}
                                total={cart.total}
                                tax={cart.tax}    
                            />
                        </div>

                    </div>
                </>
            )
        }   
        </div>
    );
}

export default CartPage;