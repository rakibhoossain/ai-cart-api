package org.aicart.store.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.aicart.PaymentStatusEnum;
import org.aicart.PaymentTypeEnum;
import org.aicart.store.order.dto.CartItemDTO;
import org.aicart.store.order.dto.OrderShippingDTO;
import org.aicart.store.order.dto.OrderBillingDTO;
import org.aicart.store.order.entity.*;
import org.aicart.store.order.utils.OrderTotal;
import org.aicart.store.product.Product;
import org.aicart.store.product.ProductVariant;
import org.aicart.store.product.dto.ProductVariantDTO;
import org.aicart.store.product.dto.VariantPriceDTO;
import org.aicart.store.user.entity.User;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class OrderService {

    @Inject
    CartRepository cartRepository;

    @Transactional
    public Order convertCartToOrder(Cart cart,
                                    OrderBillingDTO billingDetails,
                                    OrderShippingDTO shippingDetails,
                                    String subject) {

        User user = subject != null
                ? User.find("id", subject).firstResult()
                : null;


        // 1. Validate Cart
//        validateCart(cart);

        List<CartItemDTO> cartItems = cartRepository.getCartItems(cart);

        Locale.getISOCountries();

        // 3. Calculate Totals
        OrderTotal totals = calculateOrderTotals(cartItems, billingDetails);

        // 4. Create Order
        Order order = new Order();
        order.sessionId = cart.sessionId;
        order.subTotal = totals.subTotal;
        order.totalTax = totals.totalTax;
        order.shippingCost = totals.shippingCost;
        order.totalDiscount = totals.totalDiscount;
        order.totalPrice = totals.totalPrice;
        order.paymentType = PaymentTypeEnum.CASH_ON_DELIVERY;
        order.paymentStatus = PaymentStatusEnum.PENDING;
        order.status = OrderStatusEnum.PENDING;

        if(user != null) {
            order.user = user;
        } else if(cart.user != null) {
            order.user = cart.user;
        }

        order.currency = "EUR";

        //
        if (cart.user != null) {
            order.user = cart.user;
        } else {
            order.sessionId = cart.sessionId;
        }

        // 2. Create Billing and Shipping Entities
        order.billing = createBilling(order, billingDetails);

        if(shippingDetails != null) {
            order.shipping = createShipping(order, shippingDetails);
        }


        // 5. Add Order Items
        order.items = createOrderItemsFromCart(order, cartItems);

        // 6. Create Payment Entity
//        Payment payment = createPayment(order, paymentDetails, paymentType);
//        order.payment = payment;

        // 7. Persist Order and Payment
        order.persist();


//        payment.persist();

        // 8. Clear the Cart
        clearCart(cart);

        return order;
    }

//    private void validateCart(Cart cart) {
//        if (cart == null || cart.items.isEmpty()) {
//            throw new IllegalArgumentException("Cart is empty.");
//        }
//
//        for (CartItem item : cart.items) {
//            ProductVariant variant = ProductVariant.findById(item.variant.id);
//            if (variant == null || variant.stock < item.quantity) {
//                throw new IllegalArgumentException("Insufficient stock for variant: " + item.variant.id);
//            }
//        }
//    }

    private OrderBilling createBilling(Order order, OrderBillingDTO details) {
        OrderBilling billing = new OrderBilling();
        billing.order = order;
        billing.fullName = details.getFullName();
        billing.email = details.getEmail();
        billing.phone = details.getPhone();
        billing.line1 = details.getLine1();
        billing.line2 = details.getLine2();
        billing.city = details.getCity();
        billing.state = details.getState();
        billing.country = details.getCountry();
        billing.postalCode = details.getPostalCode();
        billing.vatNumber = details.getVatNumber();
        billing.taxNumber = details.getTaxNumber();
        return billing;
    }

    private OrderShipping createShipping(Order order, OrderShippingDTO details) {
        if (details == null) return null; // Shipping is optional
        OrderShipping shipping = new OrderShipping();
        shipping.order = order;
        shipping.fullName = details.getFullName();
        shipping.phone = details.getPhone();
        shipping.line1 = details.getLine1();
        shipping.line2 = details.getLine2();
        shipping.city = details.getCity();
        shipping.state = details.getState();
        shipping.country = details.getCountry();
        shipping.postalCode = details.getPostalCode();
        return shipping;
    }

    private OrderTotal calculateOrderTotals(List<CartItemDTO> cartItems, OrderBillingDTO billingDetails) {
        BigInteger subTotal = BigInteger.ZERO;
        BigInteger totalTax = BigInteger.ZERO;
        BigInteger shippingCost = BigInteger.ZERO; // Example flat shipping rate
        BigInteger totalDiscount = BigInteger.ZERO; // cart.discount != null ? cart.discount :



        for (CartItemDTO item : cartItems) {
            ProductVariantDTO variant = item.getVariant();
            VariantPriceDTO variantPrice = variant.getPrice();
            subTotal = BigInteger.valueOf((long) variantPrice.getPrice() * item.getQuantity());

//            BigDecimal tax = item.price.multiply(BigDecimal.valueOf(item.taxRate)).divide(BigDecimal.valueOf(100));
//            totalTax = totalTax.add(tax.multiply(BigDecimal.valueOf(item.quantity)));
        }

        BigInteger totalPrice = subTotal;

//        BigDecimal totalPrice = subTotal.add(totalTax).add(shippingCost).subtract(totalDiscount);

        return new OrderTotal(subTotal, totalTax, shippingCost, totalDiscount, totalPrice);
    }

    private List<OrderItem> createOrderItemsFromCart(Order order, List<CartItemDTO> cartItems) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemDTO cartItem : cartItems) {

            ProductVariantDTO variant = cartItem.getVariant();
            VariantPriceDTO variantPrice = variant.getPrice();

            OrderItem orderItem = new OrderItem();
            orderItem.order = order;
            orderItem.product = Product.findById(cartItem.getProductId());
            orderItem.variant = ProductVariant.findById(cartItem.getVariantId());
            orderItem.quantity = cartItem.getQuantity();
            orderItem.price = BigInteger.valueOf(variantPrice.getPrice());
            orderItem.tax = BigInteger.ZERO; //cartItem.price.multiply(BigDecimal.valueOf(cartItem.taxRate)).divide(BigDecimal.valueOf(100));
            orderItem.discount = BigInteger.ZERO; // Handle item-specific discounts if any
            orderItems.add(orderItem);
        }
        return orderItems;
    }


//    private OrderPayment createPayment(Order order, PaymentDetails paymentDetails, PaymentType paymentType) {
//        OrderPayment payment = new OrderPayment();
//        payment.order = order;
//        payment.paymentType = paymentType;
//        payment.amount = order.totalPrice;
//        payment.currency = paymentDetails.currency;
//        payment.paymentStatus = paymentDetails.status;
//        if (paymentDetails.providerId != null) {
//            payment.providerId = paymentDetails.providerId;
//        }
//        return payment;
//    }

    public void clearCart(Cart cart) {
        cartRepository.removeItemFromCart(cart);
    }
}
