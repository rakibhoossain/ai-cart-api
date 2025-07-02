package org.aicart.store.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.aicart.PaymentStatusEnum;
import org.aicart.PaymentTypeEnum;
import org.aicart.store.context.ShopContext;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.order.dto.*;
import org.aicart.store.order.entity.*;
import org.aicart.store.order.utils.OrderTotal;
import org.aicart.store.product.entity.Product;
import org.aicart.store.product.entity.ProductVariant;
import org.aicart.store.product.dto.ProductVariantDTO;
import org.aicart.store.product.dto.VariantPriceDTO;
import org.aicart.store.user.entity.Shop;

import java.math.BigInteger;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderService {

    @Inject
    CartRepository cartRepository;

    @Inject
    OrderRepository orderRepository;

    @Inject
    ShopContext shopContext;

    @Transactional
    public Order convertCartToOrder(Cart cart,
                                    OrderBillingDTO billingDetails,
                                    OrderShippingDTO shippingDetails,
                                    String subject) {

        Customer customer = subject != null
                ? Customer.find("id = ?1 AND shop.id = ?2", subject, shopContext.getShopId()).firstResult()
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

        if(customer != null) {
            order.customer = customer;
        } else if(cart.customer != null) {
            order.customer = cart.customer;
        }

        order.currency = "EUR";

        // Shop
        order.shop = cart.shop;

        if (cart.customer != null) {
            order.customer = cart.customer;
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

    // ==================== ORDER MANAGEMENT METHODS ====================

    /**
     * Get paginated list of orders with filters
     */
    public OrderListResponse findOrdersWithFilters(Shop shop, String search, OrderStatusEnum status,
                                                  PaymentStatusEnum paymentStatus, LocalDateTime startDate,
                                                  LocalDateTime endDate, int page, int size,
                                                  String sortBy, String order) {

        List<OrderListDTO> orders = orderRepository.findOrdersWithFilters(
            shop, search, status, paymentStatus, startDate, endDate, page, size, sortBy, order);

        long total = orderRepository.countOrdersWithFilters(
            shop, search, status, paymentStatus, startDate, endDate);

        return new OrderListResponse(orders, total, page, size);
    }

    /**
     * Get order details by ID
     */
    public OrderDetailDTO getOrderDetails(Shop shop, Long orderId) {
        Order order = orderRepository.findByIdAndShop(orderId, shop);
        if (order == null) {
            throw new RuntimeException("Order not found with id: " + orderId + " for shop: " + shop.id);
        }

        return mapToOrderDetailDTO(order);
    }

    /**
     * Update order status with tracking information
     */
    @Transactional
    public OrderDetailDTO updateOrderStatus(Shop shop, Long orderId, OrderStatusUpdateDTO updateDTO, String adminUser) {
        Order order = orderRepository.findByIdAndShop(orderId, shop);
        if (order == null) {
            throw new RuntimeException("Order not found with id: " + orderId + " for shop: " + shop.id);
        }

        // Update order status
        order.status = updateDTO.getStatus();

        // Create tracking entry
        OrderTracking tracking = new OrderTracking();
        tracking.order = order;
        tracking.status = updateDTO.getStatus();
        tracking.notes = updateDTO.getNotes();
        tracking.trackingNumber = updateDTO.getTrackingNumber();
        tracking.carrier = updateDTO.getCarrier();
        tracking.createdBy = adminUser;

        if (updateDTO.getEstimatedDelivery() != null) {
            tracking.estimatedDelivery = LocalDateTime.parse(updateDTO.getEstimatedDelivery());
        }

        tracking.persist();
        order.persist();

        return mapToOrderDetailDTO(order);
    }

    /**
     * Cancel an order
     */
    @Transactional
    public OrderDetailDTO cancelOrder(Shop shop, Long orderId, String reason, String adminUser) {
        Order order = orderRepository.findByIdAndShop(orderId, shop);
        if (order == null) {
            throw new RuntimeException("Order not found with id: " + orderId + " for shop: " + shop.id);
        }

        // Check if order can be cancelled
        if (order.status == OrderStatusEnum.DELIVERED || order.status == OrderStatusEnum.CANCELED) {
            throw new RuntimeException("Order cannot be cancelled in current status: " + order.status);
        }

        // Update order status
        order.status = OrderStatusEnum.CANCELED;

        // Create tracking entry
        OrderTracking tracking = new OrderTracking();
        tracking.order = order;
        tracking.status = OrderStatusEnum.CANCELED;
        tracking.notes = "Order cancelled. Reason: " + reason;
        tracking.createdBy = adminUser;
        tracking.persist();

        // TODO: Restore inventory
        // TODO: Process refund if payment was made

        order.persist();
        return mapToOrderDetailDTO(order);
    }

    /**
     * Create a refund for an order
     */
    @Transactional
    public OrderRefundDTO createRefund(Shop shop, Long orderId, OrderRefundRequestDTO refundRequest, String adminUser) {
        Order order = orderRepository.findByIdAndShop(orderId, shop);
        if (order == null) {
            throw new RuntimeException("Order not found with id: " + orderId + " for shop: " + shop.id);
        }

        // Generate refund number
        String refundNumber = generateRefundNumber();

        // Create refund
        OrderRefund refund = new OrderRefund();
        refund.order = order;
        refund.refundNumber = refundNumber;
        refund.refundType = refundRequest.getRefundType();
        refund.refundAmount = refundRequest.getRefundAmount();
        refund.reason = refundRequest.getReason();
        refund.adminNotes = refundRequest.getAdminNotes();
        refund.processedBy = adminUser;
        refund.processedAt = LocalDateTime.now();
        refund.status = RefundStatusEnum.APPROVED; // Auto-approve for admin-created refunds

        // Create refund items if specified
        if (refundRequest.getItems() != null && !refundRequest.getItems().isEmpty()) {
            List<OrderRefundItem> refundItems = new ArrayList<>();
            for (OrderRefundRequestDTO.RefundItemRequestDTO itemRequest : refundRequest.getItems()) {
                OrderItem orderItem = OrderItem.findById(itemRequest.getOrderItemId());
                if (orderItem != null && orderItem.order.id.equals(orderId)) {
                    OrderRefundItem refundItem = new OrderRefundItem();
                    refundItem.refund = refund;
                    refundItem.orderItem = orderItem;
                    refundItem.quantity = itemRequest.getQuantity();
                    refundItem.refundAmount = itemRequest.getRefundAmount();
                    refundItem.reason = itemRequest.getReason();
                    refundItems.add(refundItem);
                }
            }
            refund.refundItems = refundItems;
        }

        refund.persist();

        // Update order status if full refund
        if (refundRequest.getRefundType() == RefundTypeEnum.FULL) {
            order.status = OrderStatusEnum.REFUNDED;
            order.persist();
        }

        return mapToOrderRefundDTO(refund);
    }

    /**
     * Create an exchange for an order
     */
    @Transactional
    public OrderExchangeDTO createExchange(Shop shop, Long orderId, OrderExchangeRequestDTO exchangeRequest, String adminUser) {
        Order order = orderRepository.findByIdAndShop(orderId, shop);
        if (order == null) {
            throw new RuntimeException("Order not found with id: " + orderId + " for shop: " + shop.id);
        }

        // Generate exchange number
        String exchangeNumber = generateExchangeNumber();

        // Create exchange
        OrderExchange exchange = new OrderExchange();
        exchange.order = order;
        exchange.exchangeNumber = exchangeNumber;
        exchange.reason = exchangeRequest.getReason();
        exchange.adminNotes = exchangeRequest.getAdminNotes();
        exchange.processedBy = adminUser;
        exchange.processedAt = LocalDateTime.now();
        exchange.status = ExchangeStatusEnum.APPROVED; // Auto-approve for admin-created exchanges

        // Create exchange items if specified
        if (exchangeRequest.getItems() != null && !exchangeRequest.getItems().isEmpty()) {
            List<OrderExchangeItem> exchangeItems = new ArrayList<>();
            BigInteger totalPriceDifference = BigInteger.ZERO;

            for (OrderExchangeRequestDTO.ExchangeItemRequestDTO itemRequest : exchangeRequest.getItems()) {
                OrderItem orderItem = OrderItem.findById(itemRequest.getOriginalOrderItemId());
                ProductVariant newVariant = ProductVariant.findById(itemRequest.getNewVariantId());

                if (orderItem != null && orderItem.order.id.equals(orderId) && newVariant != null) {
                    OrderExchangeItem exchangeItem = new OrderExchangeItem();
                    exchangeItem.exchange = exchange;
                    exchangeItem.originalOrderItem = orderItem;
                    exchangeItem.newVariant = newVariant;
                    exchangeItem.quantity = itemRequest.getQuantity();
                    exchangeItem.reason = itemRequest.getReason();
                    exchangeItems.add(exchangeItem);

                    // Calculate price difference (simplified - you may need more complex logic)
                    // TODO: Calculate actual price difference based on variant prices
                }
            }
            exchange.exchangeItems = exchangeItems;
            exchange.priceDifference = totalPriceDifference;
        }

        exchange.persist();
        return mapToOrderExchangeDTO(exchange);
    }

    /**
     * Create a credit note for an order
     */
    @Transactional
    public OrderCreditNoteDTO createCreditNote(Shop shop, Long orderId, OrderCreditNoteRequestDTO creditNoteRequest, String adminUser) {
        Order order = orderRepository.findByIdAndShop(orderId, shop);
        if (order == null) {
            throw new RuntimeException("Order not found with id: " + orderId + " for shop: " + shop.id);
        }

        // Generate credit note number
        String creditNoteNumber = generateCreditNoteNumber();

        // Create credit note
        OrderCreditNote creditNote = new OrderCreditNote();
        creditNote.order = order;
        creditNote.creditNoteNumber = creditNoteNumber;
        creditNote.creditAmount = creditNoteRequest.getCreditAmount();
        creditNote.reason = creditNoteRequest.getReason();
        creditNote.adminNotes = creditNoteRequest.getAdminNotes();
        creditNote.issuedBy = adminUser;
        creditNote.issuedAt = LocalDateTime.now();
        creditNote.status = CreditNoteStatusEnum.ISSUED;
        creditNote.remainingAmount = creditNoteRequest.getCreditAmount();

        if (creditNoteRequest.getExpiryDate() != null) {
            creditNote.expiryDate = LocalDateTime.parse(creditNoteRequest.getExpiryDate());
        }

        // Create credit note items if specified
        if (creditNoteRequest.getItems() != null && !creditNoteRequest.getItems().isEmpty()) {
            List<OrderCreditNoteItem> creditNoteItems = new ArrayList<>();
            for (OrderCreditNoteRequestDTO.CreditNoteItemRequestDTO itemRequest : creditNoteRequest.getItems()) {
                OrderItem orderItem = OrderItem.findById(itemRequest.getOrderItemId());
                if (orderItem != null && orderItem.order.id.equals(orderId)) {
                    OrderCreditNoteItem creditNoteItem = new OrderCreditNoteItem();
                    creditNoteItem.creditNote = creditNote;
                    creditNoteItem.orderItem = orderItem;
                    creditNoteItem.quantity = itemRequest.getQuantity();
                    creditNoteItem.creditAmount = itemRequest.getCreditAmount();
                    creditNoteItem.reason = itemRequest.getReason();
                    creditNoteItems.add(creditNoteItem);
                }
            }
            creditNote.creditNoteItems = creditNoteItems;
        }

        creditNote.persist();
        return mapToOrderCreditNoteDTO(creditNote);
    }

    /**
     * Get order statistics for dashboard
     */
    public OrderStatsDTO getOrderStats(Shop shop, LocalDateTime startDate, LocalDateTime endDate) {
        // Get total orders
        long totalOrders = orderRepository.count("shop.id = ?1 and createdAt >= ?2 and createdAt <= ?3",
                                                 shop.id, startDate, endDate);

        // Get pending orders
        long pendingOrders = orderRepository.count("shop.id = ?1 and status = ?2 and createdAt >= ?3 and createdAt <= ?4",
                                                  shop.id, OrderStatusEnum.PENDING, startDate, endDate);

        // Get completed orders
        long completedOrders = orderRepository.count("shop.id = ?1 and status = ?2 and createdAt >= ?3 and createdAt <= ?4",
                                                    shop.id, OrderStatusEnum.COMPLETED, startDate, endDate);

        // Get cancelled orders
        long cancelledOrders = orderRepository.count("shop.id = ?1 and status = ?2 and createdAt >= ?3 and createdAt <= ?4",
                                                    shop.id, OrderStatusEnum.CANCELED, startDate, endDate);

        // Calculate total revenue (simplified)
        BigInteger totalRevenue = BigInteger.ZERO;
        List<Order> completedOrdersList = orderRepository.find("shop.id = ?1 and status = ?2 and createdAt >= ?3 and createdAt <= ?4",
                                                              shop.id, OrderStatusEnum.COMPLETED, startDate, endDate).list();
        for (Order order : completedOrdersList) {
            totalRevenue = totalRevenue.add(order.totalPrice);
        }

        return new OrderStatsDTO(totalOrders, pendingOrders, completedOrders, cancelledOrders, totalRevenue);
    }

    /**
     * Create a new order
     */
    @Transactional
    public OrderDetailDTO createOrder(Shop shop, OrderCreateRequestDTO createRequest, String createdBy) {
        try {
            // Create order entity
            Order order = new Order();
            order.shop = shop;
            order.sessionId = generateSessionId();

            // Set customer information
            if (createRequest.getCustomerId() != null) {
                // TODO: Load customer from database
                // order.customer = customerRepository.findById(createRequest.getCustomerId());
            }

            // Set pricing
            order.subTotal = createRequest.getSubTotal();
            order.totalDiscount = createRequest.getTotalDiscount();
            order.shippingCost = createRequest.getShippingCost();
            order.totalTax = createRequest.getTotalTax();
            order.totalPrice = createRequest.getTotalPrice();
            order.currency = createRequest.getCurrency();

            // Set payment and status
            order.paymentType = createRequest.getPaymentType();
            order.status = createRequest.getInitialStatus();
            order.paymentStatus = PaymentStatusEnum.PENDING;

            // Set addresses
            order.billing = mapToBillingEntity(createRequest.getBilling());
            if (createRequest.getShipping() != null) {
                order.shipping = mapToShippingEntity(createRequest.getShipping());
            }

            // Set additional fields
            order.notes = createRequest.getNotes();
            order.customerNotes = createRequest.getCustomerNotes();
            order.couponCode = createRequest.getCouponCode();
            order.referenceNumber = createRequest.getReferenceNumber();
            order.shippingMethod = createRequest.getShippingMethod();
            order.deliveryInstructions = createRequest.getDeliveryInstructions();

            // Save order
            order.persist();

            // Create order items
            for (OrderCreateRequestDTO.OrderItemRequestDTO itemRequest : createRequest.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.order = order;

                // TODO: Load product and variant from database
                // orderItem.product = productRepository.findById(itemRequest.getProductId());
                // orderItem.variant = variantRepository.findById(itemRequest.getVariantId());

                orderItem.quantity = itemRequest.getQuantity();
                orderItem.price = itemRequest.getPrice();
                orderItem.tax = itemRequest.getTax();
                orderItem.discount = itemRequest.getDiscount();
                orderItem.totalPrice = calculateItemTotal(itemRequest);

                orderItem.persist();
            }

            // Create initial tracking entry
            addTrackingEntry(order, order.status, TrackingEventTypeEnum.STATUS_CHANGE,
                "Order Created", "Order has been created successfully",
                null, null, null, null, createdBy, true, true);

            // Create order log
            addOrderLog(order, OrderLogTypeEnum.ORDER_CREATED, "Order Created",
                "Order #" + order.id + " has been created", null, null, createdBy, false, true);

            return mapToOrderDetailDTO(order);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create order: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing order
     */
    @Transactional
    public OrderDetailDTO updateOrder(Shop shop, Long orderId, OrderUpdateRequestDTO updateRequest, String updatedBy) {
        try {
            Order order = orderRepository.findByIdAndShop(orderId, shop);
            if (order == null) {
                throw new RuntimeException("Order not found");
            }

            // Check if order can be updated
            if (!canOrderBeUpdated(order)) {
                throw new RuntimeException("Order cannot be updated in current status: " + order.status);
            }

            // Track changes for logging
            StringBuilder changes = new StringBuilder();

            // Update customer information
            if (updateRequest.getCustomerInfo() != null) {
                updateCustomerInfo(order, updateRequest.getCustomerInfo(), changes);
            }

            // Update addresses
            if (updateRequest.getBilling() != null) {
                updateBillingAddress(order, updateRequest.getBilling(), changes);
            }

            if (updateRequest.getShipping() != null) {
                updateShippingAddress(order, updateRequest.getShipping(), changes);
            }

            // Update pricing if provided
            if (updateRequest.getTotalPrice() != null) {
                updateOrderPricing(order, updateRequest, changes);
            }

            // Update other fields
            updateOrderFields(order, updateRequest, changes);

            // Update items if provided
            if (updateRequest.getItems() != null) {
                updateOrderItems(order, updateRequest.getItems(), changes);
            }

            // Save order
            order.persist();

            // Create order log
            if (changes.length() > 0) {
                addOrderLog(order, OrderLogTypeEnum.ORDER_UPDATED, "Order Updated",
                    changes.toString(), null, null, updatedBy, false, true);
            }

            return mapToOrderDetailDTO(order);

        } catch (Exception e) {
            throw new RuntimeException("Failed to update order: " + e.getMessage(), e);
        }
    }

    private boolean canOrderBeUpdated(Order order) {
        // Orders can be updated if they are not completed, delivered, cancelled, or refunded
        return !List.of(OrderStatusEnum.COMPLETED, OrderStatusEnum.DELIVERED,
                       OrderStatusEnum.CANCELED, OrderStatusEnum.REFUNDED).contains(order.status);
    }

    private void updateCustomerInfo(Order order, OrderUpdateRequestDTO.CustomerInfoUpdateDTO customerInfo, StringBuilder changes) {
        // For guest orders, we can update customer info through billing address
        // For registered customers, we would update the customer entity
        // This is a simplified implementation
        if (customerInfo.getEmail() != null && order.billing != null) {
            if (!customerInfo.getEmail().equals(order.billing.email)) {
                changes.append("Email changed from ").append(order.billing.email)
                       .append(" to ").append(customerInfo.getEmail()).append("; ");
                order.billing.email = customerInfo.getEmail();
            }
        }
    }

    private void updateBillingAddress(Order order, OrderUpdateRequestDTO.OrderBillingUpdateDTO billing, StringBuilder changes) {
        if (order.billing == null) {
            order.billing = new OrderBilling();
        }

        // Update billing fields if provided
        if (billing.getFullName() != null) {
            order.billing.fullName = billing.getFullName();
            changes.append("Billing name updated; ");
        }
        if (billing.getEmail() != null) {
            order.billing.email = billing.getEmail();
            changes.append("Billing email updated; ");
        }
        // Add other billing field updates as needed
    }

    private void updateShippingAddress(Order order, OrderUpdateRequestDTO.OrderShippingUpdateDTO shipping, StringBuilder changes) {
        if (order.shipping == null) {
            order.shipping = new OrderShipping();
        }

        // Update shipping fields if provided
        if (shipping.getFullName() != null) {
            order.shipping.fullName = shipping.getFullName();
            changes.append("Shipping name updated; ");
        }
        // Add other shipping field updates as needed
    }

    private void updateOrderPricing(Order order, OrderUpdateRequestDTO updateRequest, StringBuilder changes) {
        if (updateRequest.getSubTotal() != null) {
            order.subTotal = updateRequest.getSubTotal();
        }
        if (updateRequest.getTotalDiscount() != null) {
            order.totalDiscount = updateRequest.getTotalDiscount();
        }
        if (updateRequest.getShippingCost() != null) {
            order.shippingCost = updateRequest.getShippingCost();
        }
        if (updateRequest.getTotalTax() != null) {
            order.totalTax = updateRequest.getTotalTax();
        }
        if (updateRequest.getTotalPrice() != null) {
            order.totalPrice = updateRequest.getTotalPrice();
        }
        changes.append("Order pricing updated; ");
    }

    private void updateOrderFields(Order order, OrderUpdateRequestDTO updateRequest, StringBuilder changes) {
        if (updateRequest.getNotes() != null) {
            order.notes = updateRequest.getNotes();
            changes.append("Admin notes updated; ");
        }
        if (updateRequest.getCustomerNotes() != null) {
            order.customerNotes = updateRequest.getCustomerNotes();
            changes.append("Customer notes updated; ");
        }
        if (updateRequest.getShippingMethod() != null) {
            order.shippingMethod = updateRequest.getShippingMethod();
            changes.append("Shipping method updated; ");
        }
        if (updateRequest.getDeliveryInstructions() != null) {
            order.deliveryInstructions = updateRequest.getDeliveryInstructions();
            changes.append("Delivery instructions updated; ");
        }
    }

    private void updateOrderItems(Order order, List<OrderUpdateRequestDTO.OrderItemUpdateDTO> itemUpdates, StringBuilder changes) {
        // This is a simplified implementation
        // In a real system, you'd need to handle inventory, pricing recalculation, etc.

        for (OrderUpdateRequestDTO.OrderItemUpdateDTO itemUpdate : itemUpdates) {
            if (itemUpdate.getRemove() != null && itemUpdate.getRemove()) {
                // Remove item
                if (itemUpdate.getId() != null) {
                    OrderItem.deleteById(itemUpdate.getId());
                    changes.append("Item removed; ");
                }
            } else if (itemUpdate.getId() == null) {
                // Add new item
                OrderItem newItem = new OrderItem();
                newItem.order = order;
                // Set item properties
                newItem.quantity = itemUpdate.getQuantity();
                newItem.price = itemUpdate.getPrice();
                newItem.persist();
                changes.append("Item added; ");
            } else {
                // Update existing item
                OrderItem existingItem = OrderItem.findById(itemUpdate.getId());
                if (existingItem != null && existingItem.order.id.equals(order.id)) {
                    if (itemUpdate.getQuantity() != null) {
                        existingItem.quantity = itemUpdate.getQuantity();
                    }
                    if (itemUpdate.getPrice() != null) {
                        existingItem.price = itemUpdate.getPrice();
                    }
                    existingItem.persist();
                    changes.append("Item updated; ");
                }
            }
        }
    }

    /**
     * Export orders to CSV format
     */
    public String exportOrdersToCSV(List<OrderListDTO> orders) {
        StringBuilder csv = new StringBuilder();

        // CSV Header
        csv.append("Order ID,Customer Name,Customer Email,Total Price,Currency,Status,Payment Status,Payment Type,Created At,Item Count\n");

        // CSV Data
        for (OrderListDTO order : orders) {
            csv.append(order.getId()).append(",")
               .append(escapeCSV(order.getCustomerName())).append(",")
               .append(escapeCSV(order.getCustomerEmail())).append(",")
               .append(order.getTotalPrice()).append(",")
               .append(order.getCurrency()).append(",")
               .append(order.getStatus()).append(",")
               .append(order.getPaymentStatus()).append(",")
               .append(order.getPaymentType()).append(",")
               .append(order.getCreatedAt()).append(",")
               .append(order.getItemCount()).append("\n");
        }

        return csv.toString();
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // ==================== HELPER METHODS ====================

    private OrderDetailDTO mapToOrderDetailDTO(Order order) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(order.id);
        dto.setSessionId(order.sessionId);
        dto.setTotalPrice(order.totalPrice);
        dto.setSubTotal(order.subTotal);
        dto.setTotalDiscount(order.totalDiscount);
        dto.setShippingCost(order.shippingCost);
        dto.setTotalTax(order.totalTax);
        dto.setCurrency(order.currency);
        dto.setStatus(order.status);
        dto.setPaymentStatus(order.paymentStatus);
        dto.setPaymentType(order.paymentType);
        dto.setCreatedAt(order.createdAt);
        dto.setUpdatedAt(order.updatedAt);

        // Map customer info
        if (order.customer != null) {
            CustomerInfoDTO customer = new CustomerInfoDTO();
            customer.setId(order.customer.id);
            customer.setFirstName(order.customer.firstName);
            customer.setLastName(order.customer.lastName);
            customer.setEmail(order.customer.email);
            customer.setPhone(order.customer.phone);
            dto.setCustomer(customer);
        }

        // Map billing
        if (order.billing != null) {
            OrderBillingDTO billing = new OrderBillingDTO();
            billing.setFullName(order.billing.fullName);
            billing.setEmail(order.billing.email);
            billing.setPhone(order.billing.phone);
            billing.setLine1(order.billing.line1);
            billing.setLine2(order.billing.line2);
            billing.setCity(order.billing.city);
            billing.setState(order.billing.state);
            billing.setCountry(order.billing.country);
            billing.setPostalCode(order.billing.postalCode);
            billing.setVatNumber(order.billing.vatNumber);
            billing.setTaxNumber(order.billing.taxNumber);
            dto.setBilling(billing);
        }

        // Map shipping
        if (order.shipping != null) {
            OrderShippingDTO shipping = new OrderShippingDTO();
            shipping.setFullName(order.shipping.fullName);
            shipping.setPhone(order.shipping.phone);
            shipping.setLine1(order.shipping.line1);
            shipping.setLine2(order.shipping.line2);
            shipping.setCity(order.shipping.city);
            shipping.setState(order.shipping.state);
            shipping.setCountry(order.shipping.country);
            shipping.setPostalCode(order.shipping.postalCode);
            dto.setShipping(shipping);
        }

        // Map order items
        if (order.items != null) {
            List<OrderItemDetailDTO> items = order.items.stream().map(item -> {
                OrderItemDetailDTO itemDTO = new OrderItemDetailDTO();
                itemDTO.setId(item.id);
                itemDTO.setProductId(item.product.id);
                itemDTO.setProductName(item.product.name);
                itemDTO.setProductSlug(item.product.slug);
                itemDTO.setVariantId(item.variant.id);
                itemDTO.setVariantName(generateVariantDisplayName(item.variant));
                itemDTO.setVariantSku(item.variant.sku);
                itemDTO.setQuantity(item.quantity);
                itemDTO.setPrice(item.price);
                itemDTO.setTax(item.tax);
                itemDTO.setDiscount(item.discount);
                itemDTO.setTotalPrice(item.price.multiply(BigInteger.valueOf(item.quantity)));
                // TODO: Add product image
                return itemDTO;
            }).collect(Collectors.toList());
            dto.setItems(items);
        }

        // Map tracking
        List<OrderTracking> trackingList = OrderTracking.find("order.id = ?1 ORDER BY createdAt DESC", order.id).list();
        if (!trackingList.isEmpty()) {
            List<OrderTrackingDTO> tracking = trackingList.stream().map(t -> {
                OrderTrackingDTO trackingDTO = new OrderTrackingDTO();
                trackingDTO.setId(t.id);
                trackingDTO.setStatus(t.status);
                trackingDTO.setNotes(t.notes);
                trackingDTO.setTrackingNumber(t.trackingNumber);
                trackingDTO.setCarrier(t.carrier);
                trackingDTO.setEstimatedDelivery(t.estimatedDelivery);
                trackingDTO.setActualDelivery(t.actualDelivery);
                trackingDTO.setCreatedBy(t.createdBy);
                trackingDTO.setCreatedAt(t.createdAt);
                return trackingDTO;
            }).collect(Collectors.toList());
            dto.setTracking(tracking);
        }

        // Map refunds
        List<OrderRefund> refundList = OrderRefund.find("order.id = ?1 ORDER BY createdAt DESC", order.id).list();
        if (!refundList.isEmpty()) {
            List<OrderRefundDTO> refunds = refundList.stream().map(this::mapToOrderRefundDTO).collect(Collectors.toList());
            dto.setRefunds(refunds);
        }

        // Map exchanges
        List<OrderExchange> exchangeList = OrderExchange.find("order.id = ?1 ORDER BY createdAt DESC", order.id).list();
        if (!exchangeList.isEmpty()) {
            List<OrderExchangeDTO> exchanges = exchangeList.stream().map(this::mapToOrderExchangeDTO).collect(Collectors.toList());
            dto.setExchanges(exchanges);
        }

        // Map credit notes
        List<OrderCreditNote> creditNoteList = OrderCreditNote.find("order.id = ?1 ORDER BY createdAt DESC", order.id).list();
        if (!creditNoteList.isEmpty()) {
            List<OrderCreditNoteDTO> creditNotes = creditNoteList.stream().map(this::mapToOrderCreditNoteDTO).collect(Collectors.toList());
            dto.setCreditNotes(creditNotes);
        }

        return dto;
    }

    private OrderRefundDTO mapToOrderRefundDTO(OrderRefund refund) {
        OrderRefundDTO dto = new OrderRefundDTO();
        dto.setId(refund.id);
        dto.setRefundNumber(refund.refundNumber);
        dto.setRefundType(refund.refundType);
        dto.setStatus(refund.status);
        dto.setRefundAmount(refund.refundAmount);
        dto.setReason(refund.reason);
        dto.setAdminNotes(refund.adminNotes);
        dto.setProcessedBy(refund.processedBy);
        dto.setProcessedAt(refund.processedAt);
        dto.setCreatedAt(refund.createdAt);

        if (refund.refundItems != null) {
            List<OrderRefundItemDTO> items = refund.refundItems.stream().map(item -> {
                OrderRefundItemDTO itemDTO = new OrderRefundItemDTO();
                itemDTO.setId(item.id);
                itemDTO.setOrderItemId(item.orderItem.id);
                itemDTO.setProductName(item.orderItem.product.name);
                itemDTO.setVariantName(generateVariantDisplayName(item.orderItem.variant));
                itemDTO.setQuantity(item.quantity);
                itemDTO.setRefundAmount(item.refundAmount);
                itemDTO.setReason(item.reason);
                return itemDTO;
            }).collect(Collectors.toList());
            dto.setRefundItems(items);
        }

        return dto;
    }

    private String generateRefundNumber() {
        return "REF-" + System.currentTimeMillis();
    }

    private String generateExchangeNumber() {
        return "EXC-" + System.currentTimeMillis();
    }

    private String generateCreditNoteNumber() {
        return "CN-" + System.currentTimeMillis();
    }

    private OrderExchangeDTO mapToOrderExchangeDTO(OrderExchange exchange) {
        OrderExchangeDTO dto = new OrderExchangeDTO();
        dto.setId(exchange.id);
        dto.setExchangeNumber(exchange.exchangeNumber);
        dto.setStatus(exchange.status);
        dto.setReason(exchange.reason);
        dto.setAdminNotes(exchange.adminNotes);
        dto.setPriceDifference(exchange.priceDifference);
        dto.setProcessedBy(exchange.processedBy);
        dto.setProcessedAt(exchange.processedAt);
        dto.setCreatedAt(exchange.createdAt);

        if (exchange.exchangeItems != null) {
            List<OrderExchangeItemDTO> items = exchange.exchangeItems.stream().map(item -> {
                OrderExchangeItemDTO itemDTO = new OrderExchangeItemDTO();
                itemDTO.setId(item.id);
                itemDTO.setOriginalOrderItemId(item.originalOrderItem.id);
                itemDTO.setOriginalProductName(item.originalOrderItem.product.name);
                itemDTO.setOriginalVariantName(generateVariantDisplayName(item.originalOrderItem.variant));
                itemDTO.setNewVariantId(item.newVariant.id);
                itemDTO.setNewProductName(item.newVariant.product.name);
                itemDTO.setNewVariantName(generateVariantDisplayName(item.newVariant));
                itemDTO.setQuantity(item.quantity);
                itemDTO.setReason(item.reason);
                return itemDTO;
            }).collect(Collectors.toList());
            dto.setExchangeItems(items);
        }

        return dto;
    }

    private OrderCreditNoteDTO mapToOrderCreditNoteDTO(OrderCreditNote creditNote) {
        OrderCreditNoteDTO dto = new OrderCreditNoteDTO();
        dto.setId(creditNote.id);
        dto.setCreditNoteNumber(creditNote.creditNoteNumber);
        dto.setStatus(creditNote.status);
        dto.setCreditAmount(creditNote.creditAmount);
        dto.setReason(creditNote.reason);
        dto.setAdminNotes(creditNote.adminNotes);
        dto.setExpiryDate(creditNote.expiryDate);
        dto.setUsedAmount(creditNote.usedAmount);
        dto.setRemainingAmount(creditNote.remainingAmount);
        dto.setIssuedBy(creditNote.issuedBy);
        dto.setIssuedAt(creditNote.issuedAt);
        dto.setCreatedAt(creditNote.createdAt);

        if (creditNote.creditNoteItems != null) {
            List<OrderCreditNoteItemDTO> items = creditNote.creditNoteItems.stream().map(item -> {
                OrderCreditNoteItemDTO itemDTO = new OrderCreditNoteItemDTO();
                itemDTO.setId(item.id);
                itemDTO.setOrderItemId(item.orderItem.id);
                itemDTO.setProductName(item.orderItem.product.name);
                itemDTO.setVariantName(generateVariantDisplayName(item.orderItem.variant));
                itemDTO.setQuantity(item.quantity);
                itemDTO.setCreditAmount(item.creditAmount);
                itemDTO.setReason(item.reason);
                return itemDTO;
            }).collect(Collectors.toList());
            dto.setCreditNoteItems(items);
        }

        return dto;
    }

    private String generateSessionId() {
        return "SES-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    private BigInteger calculateItemTotal(OrderCreateRequestDTO.OrderItemRequestDTO itemRequest) {
        BigInteger total = itemRequest.getPrice().multiply(BigInteger.valueOf(itemRequest.getQuantity()));
        total = total.add(itemRequest.getTax());
        total = total.subtract(itemRequest.getDiscount());
        return total;
    }

    private OrderBilling mapToBillingEntity(OrderCreateRequestDTO.OrderBillingRequestDTO billingRequest) {
        OrderBilling billing = new OrderBilling();
        billing.fullName = billingRequest.getFullName();
        billing.email = billingRequest.getEmail();
        billing.phone = billingRequest.getPhone();
        billing.line1 = billingRequest.getLine1();
        billing.line2 = billingRequest.getLine2();
        billing.city = billingRequest.getCity();
        billing.state = billingRequest.getState();
        billing.country = billingRequest.getCountry();
        billing.postalCode = billingRequest.getPostalCode();
        billing.vatNumber = billingRequest.getVatNumber();
        billing.taxNumber = billingRequest.getTaxNumber();
        return billing;
    }

    private OrderShipping mapToShippingEntity(OrderCreateRequestDTO.OrderShippingRequestDTO shippingRequest) {
        OrderShipping shipping = new OrderShipping();
        shipping.fullName = shippingRequest.getFullName();
        shipping.phone = shippingRequest.getPhone();
        shipping.line1 = shippingRequest.getLine1();
        shipping.line2 = shippingRequest.getLine2();
        shipping.city = shippingRequest.getCity();
        shipping.state = shippingRequest.getState();
        shipping.country = shippingRequest.getCountry();
        shipping.postalCode = shippingRequest.getPostalCode();
        return shipping;
    }

    private void addTrackingEntry(Order order, OrderStatusEnum status, TrackingEventTypeEnum eventType,
                                 String title, String description, String trackingNumber, String carrier,
                                 String location, LocalDateTime eventTimestamp, String createdBy,
                                 Boolean isPublic, Boolean isMilestone) {
        OrderTracking tracking = new OrderTracking();
        tracking.order = order;
        tracking.status = status;
        tracking.eventType = eventType;
        tracking.title = title;
        tracking.description = description;
        tracking.trackingNumber = trackingNumber;
        tracking.carrier = carrier;
        tracking.location = location;
        tracking.eventTimestamp = eventTimestamp != null ? eventTimestamp : LocalDateTime.now();
        tracking.createdBy = createdBy;
        tracking.isPublic = isPublic;
        tracking.isMilestone = isMilestone;
        tracking.persist();
    }

    private void addOrderLog(Order order, OrderLogTypeEnum logType, String title, String description,
                           String oldValue, String newValue, String createdBy, Boolean isSystemGenerated,
                           Boolean isVisibleToCustomer) {
        OrderLog log = new OrderLog();
        log.order = order;
        log.logType = logType;
        log.title = title;
        log.description = description;
        log.oldValue = oldValue;
        log.newValue = newValue;
        log.createdBy = createdBy;
        log.createdByType = "ADMIN"; // TODO: Determine based on user type
        log.isSystemGenerated = isSystemGenerated;
        log.isVisibleToCustomer = isVisibleToCustomer;
        log.persist();
    }

    private String generateVariantDisplayName(ProductVariant variant) {
        if (variant == null) {
            return "Default";
        }

        if (variant.attributeValues != null && !variant.attributeValues.isEmpty()) {
            return variant.attributeValues.stream()
                    .map(av -> av.value)
                    .collect(Collectors.joining(" / "));
        }

        return variant.sku != null ? variant.sku : "Default";
    }
}
