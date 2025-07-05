package org.aicart.store.inventory.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.inventory.dto.InventoryItemDTO;
import org.aicart.store.inventory.dto.InventoryUpdateRequestDTO;
import org.aicart.store.inventory.service.InventoryService;
import org.aicart.store.user.entity.Shop;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Inventory Management", description = "Inventory management operations")
public class InventoryResource {

    @Inject
    InventoryService inventoryService;

    private Long getShopId() {
        // TODO: Get from JWT token or context
        return 1L;
    }

    @GET
    @Operation(summary = "Get inventory items", description = "Get paginated list of inventory items with optional filters")
    @APIResponse(responseCode = "200", description = "List of inventory items")
    public Response getInventoryItems(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sort") @DefaultValue("productName") String sortField,
            @QueryParam("order") @DefaultValue("asc") String order,
            @QueryParam("search") String search,
            @QueryParam("warehouseId") Long warehouseId,
            @QueryParam("lowStock") Boolean lowStock,
            @QueryParam("outOfStock") Boolean outOfStock,
            @QueryParam("trackQuantity") Boolean trackQuantity,
            @QueryParam("productId") Long productId,
            @QueryParam("brandId") Long brandId,
            @QueryParam("categoryId") Long categoryId) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            // Build filters map
            Map<String, Object> filters = new HashMap<>();
            if (search != null && !search.trim().isEmpty()) {
                filters.put("search", search.trim());
            }
            if (warehouseId != null) {
                filters.put("warehouseId", warehouseId);
            }
            if (lowStock != null) {
                filters.put("lowStock", lowStock);
            }
            if (outOfStock != null) {
                filters.put("outOfStock", outOfStock);
            }
            if (trackQuantity != null) {
                filters.put("trackQuantity", trackQuantity);
            }
            if (productId != null) {
                filters.put("productId", productId);
            }
            if (brandId != null) {
                filters.put("brandId", brandId);
            }
            if (categoryId != null) {
                filters.put("categoryId", categoryId);
            }

            boolean ascending = "asc".equalsIgnoreCase(order);
            List<InventoryItemDTO> items = inventoryService.getInventoryItems(
                    shop, page, size, sortField, ascending, filters);
            long total = inventoryService.countInventoryItems(shop, filters);

            return Response.ok(Map.of(
                    "data", items,
                    "total", total,
                    "page", page,
                    "size", size
            )).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to fetch inventory: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/adjust")
    @Operation(summary = "Adjust inventory", description = "Adjust inventory quantity for a variant in a warehouse")
    @APIResponse(responseCode = "200", description = "Inventory adjusted successfully")
    @APIResponse(responseCode = "400", description = "Invalid request")
    public Response adjustInventory(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId,
            @Valid InventoryUpdateRequestDTO request) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            inventoryService.adjustInventory(shop, request, "admin"); // TODO: Get actual user
            
            return Response.ok(Map.of("message", "Inventory adjusted successfully")).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to adjust inventory: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/variant/{variantId}")
    @Operation(summary = "Get inventory for variant", description = "Get inventory details for a specific variant")
    @APIResponse(responseCode = "200", description = "Variant inventory details")
    @APIResponse(responseCode = "404", description = "Variant not found")
    public Response getVariantInventory(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId,
            @PathParam("variantId") Long variantId) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            // Build filter for specific variant
            Map<String, Object> filters = new HashMap<>();
            filters.put("variantId", variantId);

            List<InventoryItemDTO> items = inventoryService.getInventoryItems(
                    shop, 0, 1, "productName", true, filters);

            if (items.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Variant not found"))
                        .build();
            }

            return Response.ok(items.get(0)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to fetch variant inventory: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/low-stock")
    @Operation(summary = "Get low stock items", description = "Get items with low stock levels")
    @APIResponse(responseCode = "200", description = "List of low stock items")
    public Response getLowStockItems(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId,
            @QueryParam("threshold") @DefaultValue("10") int threshold) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            Map<String, Object> filters = new HashMap<>();
            filters.put("lowStock", true);

            List<InventoryItemDTO> items = inventoryService.getInventoryItems(
                    shop, 0, 100, "currentStock", true, filters);

            return Response.ok(Map.of("data", items)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to fetch low stock items: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/out-of-stock")
    @Operation(summary = "Get out of stock items", description = "Get items that are out of stock")
    @APIResponse(responseCode = "200", description = "List of out of stock items")
    public Response getOutOfStockItems(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            Map<String, Object> filters = new HashMap<>();
            filters.put("outOfStock", true);

            List<InventoryItemDTO> items = inventoryService.getInventoryItems(
                    shop, 0, 100, "productName", true, filters);

            return Response.ok(Map.of("data", items)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to fetch out of stock items: " + e.getMessage()))
                    .build();
        }
    }
}
