package org.aicart.store.analytics;


import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.order.OrderStatusEnum;
import org.aicart.store.order.entity.Order;
import org.aicart.store.order.entity.OrderBilling;
import org.aicart.store.order.entity.OrderItem;
import org.aicart.store.order.entity.OrderShipping;
import org.aicart.store.product.entity.Product;
import org.aicart.store.product.entity.ProductVariant;
import org.aicart.store.user.entity.Shop;
import org.aicart.PaymentStatusEnum;
import org.aicart.PaymentTypeEnum;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Path("/test-data")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestDataResource {

    @POST
    @Path("/create-sample-orders")
    @Transactional
    public Response createSampleOrders() {
        try {
            // Get existing entities
            Shop shop = Shop.findById(1L);
            Customer customer = Customer.findById(1L);
            Product product1 = Product.findById(1L);
            Product product2 = Product.findById(2L);
            ProductVariant variant1 = ProductVariant.findById(1L);
            ProductVariant variant2 = ProductVariant.findById(2L);

            if (shop == null || customer == null || product1 == null || variant1 == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Required entities not found")
                        .build();
            }

            // Create sample orders (recent)
            createOrder(shop, customer, product1, variant1, "ORD-001", 3250L, LocalDateTime.now().minusDays(5));
            createOrder(shop, customer, product2, variant2, "ORD-002", 2280L, LocalDateTime.now().minusDays(10));
            createOrder(shop, customer, product1, variant1, "ORD-003", 3920L, LocalDateTime.now().minusDays(15));
            createOrder(shop, customer, product2, variant2, "ORD-004", 1850L, LocalDateTime.now().minusDays(20));
            createOrder(shop, customer, product1, variant1, "ORD-005", 3430L, LocalDateTime.now().minusDays(25));

            // Create older orders for comparison
            createOrder(shop, customer, product1, variant1, "ORD-006", 2500L, LocalDateTime.now().minusDays(35));
            createOrder(shop, customer, product2, variant2, "ORD-007", 1520L, LocalDateTime.now().minusDays(40));

            return Response.ok("Sample orders created successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating sample orders: " + e.getMessage())
                    .build();
        }
    }

    private void createOrder(Shop shop, Customer customer, Product product, ProductVariant variant, 
                           String orderNumber, Long totalPrice, LocalDateTime createdAt) {
        
        Order order = new Order();
        order.shop = shop;
        order.customer = customer;
        order.referenceNumber = orderNumber;
        order.currency = "USD";
        order.subTotal = BigInteger.valueOf(totalPrice - 750); // Subtract shipping and tax
        order.shippingCost = BigInteger.valueOf(500);
        order.totalTax = BigInteger.valueOf(250);
        order.totalPrice = BigInteger.valueOf(totalPrice);
        order.status = OrderStatusEnum.COMPLETED;
        order.paymentStatus = PaymentStatusEnum.COMPLETED;
        order.paymentType = PaymentTypeEnum.STRIPE;
        order.createdAt = createdAt;
        order.updatedAt = createdAt;
        order.items = new ArrayList<>();

        // Create order item
        OrderItem item = new OrderItem();
        item.order = order;
        item.product = product;
        item.variant = variant;
        item.quantity = 1;
        item.price = BigInteger.valueOf(totalPrice - 750);
        item.discount = BigInteger.ZERO;
        item.tax = BigInteger.valueOf(250);
        item.taxRate = 10; // 10% tax rate
        item.totalPrice = BigInteger.valueOf(totalPrice - 750);
        order.items.add(item);

        // Create billing info
        OrderBilling billing = new OrderBilling();
        billing.order = order;
        billing.fullName = "Admin Customer";
        billing.email = "admin@aicart.store";
        billing.phone = "+1 (555) 987-6543";
        billing.line1 = "123 Main St";
        billing.city = "New York";
        billing.state = "NY";
        billing.postalCode = "10001";
        billing.country = "US";
        order.billing = billing;

        // Create shipping info
        OrderShipping shipping = new OrderShipping();
        shipping.order = order;
        shipping.fullName = "Admin Customer";
        shipping.phone = "+1 (555) 987-6543";
        shipping.line1 = "123 Main St";
        shipping.city = "New York";
        shipping.state = "NY";
        shipping.postalCode = "10001";
        shipping.country = "US";
        order.shipping = shipping;

        // Persist the order (this will cascade to items, billing, and shipping)
        order.persist();
    }

    @DELETE
    @Path("/clear-sample-orders")
    @Transactional
    public Response clearSampleOrders() {
        try {
            // Delete all orders that start with "ORD-"
            Order.delete("referenceNumber LIKE 'ORD-%'");
            return Response.ok("Sample orders cleared successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error clearing sample orders: " + e.getMessage())
                    .build();
        }
    }
}
