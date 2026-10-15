import "../Style/CartPageStyle.css";
import CartHeader from "../Components/CartHeader";
import CartList from "../Components/CartList";
import OrderSummary from "../Components/OrderSummary";
import EmptyCart from "../Components/EmptyCart";

import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getCart, updateCartItem, removeCartItem } from "../../../Services/cartService";
import { startCheckout } from "../../../Services/checkoutService";

// Must match the tax rate in CheckoutService on the backend
const TAX_RATE = 0.0625;

function CartPage() {

    const user = JSON.parse(localStorage.getItem("loggedInUser"));
    const userId = user?.userId;
    const token = localStorage.getItem("token");

    const [loading, setLoading] = useState(Boolean(userId && token));
    const [checkingOut, setCheckingOut] = useState(false);
    const [checkoutError, setCheckoutError] = useState("");

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

            const subtotal = cartData.items.reduce(
                (sum, item) => sum + item.product.price * item.quantity,
                0
            );

            const tax = Math.round(subtotal * TAX_RATE * 100) / 100;

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

    async function handleCheckout() {
        setCheckingOut(true);
        setCheckoutError("");

        try {
            const { checkoutUrl } = await startCheckout(cart.cartId, token);

            // Hand off to Stripe's hosted checkout page
            window.location.assign(checkoutUrl);
        } catch (error) {
            setCheckoutError(error.message);
            setCheckingOut(false);
        }
    }

    useEffect(() => {

        if (!userId || !token) {
            return;
        }

        fetchCart();
    }, [userId, token]);

    if (!userId || !token) {
        return (
            <div className="cartPage">
                <div className="cartTitle">
                    <h1>Shopping Cart</h1>
                </div>
                <p className="cartMessage">
                    Please <Link to="/login">log in</Link> to view your cart.
                </p>
            </div>
        );
    }

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
                                onCheckout={handleCheckout}
                                checkingOut={checkingOut}
                                error={checkoutError}
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

/**
 * export function calculateCartTotals(items){

    const subtotal = items.reduce(
        (sum,item)=> sum + item.product.price * item.quantity,
        0
    );

    const tax = subtotal * 0.08;

    const shipping = 0;

    const total = subtotal + tax + shipping;


    return {
        subtotal,
        tax,
        shipping,
        total
    };
}
 */