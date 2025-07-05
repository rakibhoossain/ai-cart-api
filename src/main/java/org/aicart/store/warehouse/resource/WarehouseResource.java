package org.aicart.store.warehouse.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.entity.WarehouseLocation;
import org.aicart.store.user.entity.Shop;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/warehouses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Warehouse Management", description = "Warehouse management operations")
public class WarehouseResource {

    @GET
    @Operation(summary = "Get warehouses", description = "Get list of warehouses for a shop")
    @APIResponse(responseCode = "200", description = "List of warehouses")
    public Response getWarehouses(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            List<WarehouseLocation> warehouses = WarehouseLocation.find("shop = ?1 and isActive = true", shop).list();
            
            List<Map<String, Object>> warehouseData = warehouses.stream()
                    .map(warehouse -> {
                        Map<String, Object> data = new java.util.HashMap<>();
                        data.put("id", warehouse.id);
                        data.put("name", warehouse.name);
                        data.put("addressLine1", warehouse.addressLine1);
                        data.put("addressLine2", warehouse.addressLine2 != null ? warehouse.addressLine2 : "");
                        data.put("city", warehouse.city);
                        data.put("state", warehouse.state != null ? warehouse.state : "");
                        data.put("postalCode", warehouse.postalCode);
                        data.put("country", warehouse.country.name);
                        data.put("contactNumber", warehouse.contactNumber != null ? warehouse.contactNumber : "");
                        data.put("isActive", warehouse.isActive);
                        return data;
                    })
                    .collect(Collectors.toList());

            return Response.ok(Map.of("data", warehouseData)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to fetch warehouses: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get warehouse", description = "Get warehouse details by ID")
    @APIResponse(responseCode = "200", description = "Warehouse details")
    @APIResponse(responseCode = "404", description = "Warehouse not found")
    public Response getWarehouse(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId,
            @PathParam("id") Long id) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            WarehouseLocation warehouse = WarehouseLocation.find("id = ?1 and shop = ?2", id, shop).firstResult();
            if (warehouse == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Warehouse not found"))
                        .build();
            }

            Map<String, Object> warehouseData = new java.util.HashMap<>();
            warehouseData.put("id", warehouse.id);
            warehouseData.put("name", warehouse.name);
            warehouseData.put("addressLine1", warehouse.addressLine1);
            warehouseData.put("addressLine2", warehouse.addressLine2 != null ? warehouse.addressLine2 : "");
            warehouseData.put("city", warehouse.city);
            warehouseData.put("state", warehouse.state != null ? warehouse.state : "");
            warehouseData.put("postalCode", warehouse.postalCode);
            warehouseData.put("country", warehouse.country.name);
            warehouseData.put("contactNumber", warehouse.contactNumber != null ? warehouse.contactNumber : "");
            warehouseData.put("isActive", warehouse.isActive);

            return Response.ok(warehouseData).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to fetch warehouse: " + e.getMessage()))
                    .build();
        }
    }
}
