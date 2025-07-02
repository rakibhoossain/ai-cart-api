package org.aicart.store.order;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.PaymentStatusEnum;
import org.aicart.store.order.dto.*;
import org.aicart.store.order.entity.Order;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class OrderResource {

    @Inject
    OrderService orderService;

    private Long getShopId() {
        // In a real application, this would come from authentication context
        return 1L;
    }

    private String getCurrentUser() {
        // In a real application, this would come from authentication context
        return "admin@example.com";
    }

    /**
     * Create a new order
     */
    @POST
    public Response createOrder(@Valid OrderCreateRequestDTO createRequest) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            OrderDetailDTO order = orderService.createOrder(shop, createRequest, getCurrentUser());
            return Response.status(Response.Status.CREATED).entity(order).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Get paginated list of orders with filters
     */
    @GET
    public Response getOrders(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("search") String search,
            @QueryParam("status") OrderStatusEnum status,
            @QueryParam("paymentStatus") PaymentStatusEnum paymentStatus,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("order") @DefaultValue("desc") String order) {

        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            LocalDateTime start = null;
            LocalDateTime end = null;

            if (startDate != null && !startDate.trim().isEmpty()) {
                start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            OrderListResponse response = orderService.findOrdersWithFilters(
                shop, search, status, paymentStatus, start, end, page, size, sortBy, order);

            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Get order details by ID
     */
    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") Long id) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            OrderDetailDTO order = orderService.getOrderDetails(shop, id);
            return Response.ok(order).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Update an existing order
     */
    @PUT
    @Path("/{id}")
    public Response updateOrder(@PathParam("id") Long id, @Valid OrderUpdateRequestDTO updateRequest) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            OrderDetailDTO order = orderService.updateOrder(shop, id, updateRequest, getCurrentUser());
            return Response.ok(order).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Update order status
     */
    @PUT
    @Path("/{id}/status")
    public Response updateOrderStatus(@PathParam("id") Long id, @Valid OrderStatusUpdateDTO updateDTO) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            OrderDetailDTO order = orderService.updateOrderStatus(shop, id, updateDTO, getCurrentUser());
            return Response.ok(order).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Cancel an order
     */
    @PUT
    @Path("/{id}/cancel")
    public Response cancelOrder(@PathParam("id") Long id, Map<String, String> requestBody) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            String reason = requestBody.getOrDefault("reason", "Cancelled by admin");
            OrderDetailDTO order = orderService.cancelOrder(shop, id, reason, getCurrentUser());
            return Response.ok(order).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Create a refund for an order
     */
    @POST
    @Path("/{id}/refunds")
    public Response createRefund(@PathParam("id") Long id, @Valid OrderRefundRequestDTO refundRequest) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            OrderRefundDTO refund = orderService.createRefund(shop, id, refundRequest, getCurrentUser());
            return Response.status(Response.Status.CREATED).entity(refund).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Create an exchange for an order
     */
    @POST
    @Path("/{id}/exchanges")
    public Response createExchange(@PathParam("id") Long id, @Valid OrderExchangeRequestDTO exchangeRequest) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            OrderExchangeDTO exchange = orderService.createExchange(shop, id, exchangeRequest, getCurrentUser());
            return Response.status(Response.Status.CREATED).entity(exchange).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Create a credit note for an order
     */
    @POST
    @Path("/{id}/credit-notes")
    public Response createCreditNote(@PathParam("id") Long id, @Valid OrderCreditNoteRequestDTO creditNoteRequest) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            OrderCreditNoteDTO creditNote = orderService.createCreditNote(shop, id, creditNoteRequest, getCurrentUser());
            return Response.status(Response.Status.CREATED).entity(creditNote).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Get order statistics
     */
    @GET
    @Path("/stats")
    public Response getOrderStats(
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            LocalDateTime start = startDate != null ?
                LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME) :
                LocalDateTime.now().minusDays(30);

            LocalDateTime end = endDate != null ?
                LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME) :
                LocalDateTime.now();

            OrderStatsDTO stats = orderService.getOrderStats(shop, start, end);
            return Response.ok(stats).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete an order (soft delete)
     */
    @DELETE
    @Path("/{id}")
    public Response deleteOrder(@PathParam("id") Long id) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            Order order = orderService.orderRepository.findByIdAndShop(id, shop);
            if (order == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Order not found"))
                        .build();
            }

            // Only allow deletion of cancelled or draft orders
            if (order.status != OrderStatusEnum.CANCELED && order.status != OrderStatusEnum.PENDING) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("message", "Cannot delete order in current status: " + order.status))
                        .build();
            }

            // Soft delete by updating status
            order.status = OrderStatusEnum.CANCELED;
            order.persist();

            return Response.ok(Map.of("message", "Order deleted successfully")).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Bulk update order status
     */
    @PUT
    @Path("/bulk/status")
    public Response bulkUpdateStatus(@Valid BulkOrderStatusUpdateDTO bulkUpdate) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            List<Long> updatedOrders = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (Long orderId : bulkUpdate.getOrderIds()) {
                try {
                    orderService.updateOrderStatus(shop, orderId, bulkUpdate.getStatusUpdate(), getCurrentUser());
                    updatedOrders.add(orderId);
                } catch (Exception e) {
                    errors.add("Order " + orderId + ": " + e.getMessage());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("updated", updatedOrders);
            response.put("errors", errors);
            response.put("totalUpdated", updatedOrders.size());
            response.put("totalErrors", errors.size());

            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Bulk cancel orders
     */
    @PUT
    @Path("/bulk/cancel")
    public Response bulkCancelOrders(@Valid BulkOrderCancelDTO bulkCancel) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            List<Long> cancelledOrders = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (Long orderId : bulkCancel.getOrderIds()) {
                try {
                    orderService.cancelOrder(shop, orderId, bulkCancel.getReason(), getCurrentUser());
                    cancelledOrders.add(orderId);
                } catch (Exception e) {
                    errors.add("Order " + orderId + ": " + e.getMessage());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("cancelled", cancelledOrders);
            response.put("errors", errors);
            response.put("totalCancelled", cancelledOrders.size());
            response.put("totalErrors", errors.size());

            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Export orders to CSV
     */
    @GET
    @Path("/export")
    @Produces("text/csv")
    public Response exportOrders(
            @QueryParam("search") String search,
            @QueryParam("status") OrderStatusEnum status,
            @QueryParam("paymentStatus") PaymentStatusEnum paymentStatus,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            LocalDateTime start = null;
            LocalDateTime end = null;

            if (startDate != null && !startDate.trim().isEmpty()) {
                start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            // Get all orders for export (no pagination)
            OrderListResponse response = orderService.findOrdersWithFilters(
                shop, search, status, paymentStatus, start, end, 0, Integer.MAX_VALUE, "createdAt", "desc");

            String csv = orderService.exportOrdersToCSV(response.getOrders());

            return Response.ok(csv)
                    .header("Content-Disposition", "attachment; filename=orders_" +
                           LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Get order status options
     */
    @GET
    @Path("/status-options")
    public Response getOrderStatusOptions() {
        try {
            List<Map<String, Object>> statusOptions = new ArrayList<>();
            for (OrderStatusEnum status : OrderStatusEnum.values()) {
                Map<String, Object> option = new HashMap<>();
                option.put("value", status.name());
                option.put("label", formatStatusLabel(status));
                statusOptions.add(option);
            }

            return Response.ok(statusOptions).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Get payment status options
     */
    @GET
    @Path("/payment-status-options")
    public Response getPaymentStatusOptions() {
        try {
            List<Map<String, Object>> statusOptions = new ArrayList<>();
            for (PaymentStatusEnum status : PaymentStatusEnum.values()) {
                Map<String, Object> option = new HashMap<>();
                option.put("value", status.name());
                option.put("label", formatPaymentStatusLabel(status));
                statusOptions.add(option);
            }

            return Response.ok(statusOptions).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    // Helper methods
    private String formatStatusLabel(OrderStatusEnum status) {
        return switch (status) {
            case PENDING -> "Pending";
            case CONFIRMED -> "Confirmed";
            case PROCESSING -> "Processing";
            case PACKED -> "Packed";
            case SHIPPED -> "Shipped";
            case OUT_FOR_DELIVERY -> "Out for Delivery";
            case DELIVERED -> "Delivered";
            case COMPLETED -> "Completed";
            case CANCELED -> "Cancelled";
            case REFUNDED -> "Refunded";
            case PARTIALLY_REFUNDED -> "Partially Refunded";
            case RETURNED -> "Returned";
            case EXCHANGED -> "Exchanged";
            case FAILED -> "Failed";
        };
    }

    private String formatPaymentStatusLabel(PaymentStatusEnum status) {
        return switch (status) {
            case PENDING -> "Pending";
            case COMPLETED -> "Completed";
            case FAILED -> "Failed";
            case REFUNDED -> "Refunded";
        };
    }
}
