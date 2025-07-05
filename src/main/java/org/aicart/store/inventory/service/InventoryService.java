package org.aicart.store.inventory.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.aicart.entity.WarehouseLocation;
import org.aicart.store.inventory.dto.*;
import org.aicart.store.inventory.entity.InventoryAdjustment;
import org.aicart.store.product.entity.Product;
import org.aicart.store.product.entity.ProductVariant;
import org.aicart.store.product.entity.VariantStock;
import org.aicart.store.user.entity.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class InventoryService {

    @Inject
    EntityManager entityManager;

    public List<InventoryItemDTO> getInventoryItems(Shop shop, int page, int size, String sortField, 
                                                   boolean ascending, Map<String, Object> filters) {
        StringBuilder queryBuilder = new StringBuilder("""
            SELECT DISTINCT
                pv.id as variant_id,
                p.id as product_id,
                p.name as product_name,
                p.slug as product_slug,
                pv.sku as variant_sku,
                COALESCE(SUM(vs.quantity), 0) as current_stock,
                COALESCE(reserved.reserved_quantity, 0) as reserved_stock,
                (COALESCE(SUM(vs.quantity), 0) - COALESCE(reserved.reserved_quantity, 0)) as available_stock,
                pv.created_at,
                pv.updated_at
            FROM product_variants pv
            JOIN products p ON pv.product_id = p.id
            LEFT JOIN variant_stocks vs ON pv.id = vs.variant_id
            LEFT JOIN warehouse_locations wl ON vs.warehouse_id = wl.id AND wl.is_active = true AND wl.shop_id = :shopId
            LEFT JOIN (
                SELECT variant_id, SUM(quantity) as reserved_quantity
                FROM stock_reservations
                WHERE expires_at > EXTRACT(EPOCH FROM NOW())
                GROUP BY variant_id
            ) reserved ON pv.id = reserved.variant_id
            WHERE p.shop_id = :shopId
            """);

        // Add filters
        if (filters.containsKey("search") && filters.get("search") != null) {
            queryBuilder.append(" AND (p.name ILIKE :search OR pv.sku ILIKE :search)");
        }
        
        if (filters.containsKey("warehouseId") && filters.get("warehouseId") != null) {
            queryBuilder.append(" AND wl.id = :warehouseId");
        }
        
        queryBuilder.append(" GROUP BY pv.id, p.id, p.name, p.slug, pv.sku, reserved.reserved_quantity, pv.created_at, pv.updated_at");

        // Add aggregate filters using HAVING clause
        boolean hasHavingClause = false;
        if (filters.containsKey("lowStock") && Boolean.TRUE.equals(filters.get("lowStock"))) {
            queryBuilder.append(" HAVING COALESCE(SUM(vs.quantity), 0) <= 10"); // Default low stock threshold
            hasHavingClause = true;
        }

        if (filters.containsKey("outOfStock") && Boolean.TRUE.equals(filters.get("outOfStock"))) {
            if (hasHavingClause) {
                queryBuilder.append(" AND COALESCE(SUM(vs.quantity), 0) = 0");
            } else {
                queryBuilder.append(" HAVING COALESCE(SUM(vs.quantity), 0) = 0");
            }
        }
        
        // Add sorting
        String orderDirection = ascending ? "ASC" : "DESC";
        switch (sortField.toLowerCase()) {
            case "productname":
                queryBuilder.append(" ORDER BY p.name ").append(orderDirection);
                break;
            case "sku":
                queryBuilder.append(" ORDER BY pv.sku ").append(orderDirection);
                break;
            case "currentstock":
                queryBuilder.append(" ORDER BY current_stock ").append(orderDirection);
                break;
            case "availablestock":
                queryBuilder.append(" ORDER BY available_stock ").append(orderDirection);
                break;
            default:
                queryBuilder.append(" ORDER BY p.name ASC");
        }

        Query query = entityManager.createNativeQuery(queryBuilder.toString());
        query.setParameter("shopId", shop.id);
        
        // Set filter parameters
        if (filters.containsKey("search") && filters.get("search") != null) {
            query.setParameter("search", "%" + filters.get("search") + "%");
        }
        if (filters.containsKey("warehouseId") && filters.get("warehouseId") != null) {
            query.setParameter("warehouseId", filters.get("warehouseId"));
        }

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        List<InventoryItemDTO> items = new ArrayList<>();
        for (Object[] row : results) {
            InventoryItemDTO item = new InventoryItemDTO();
            item.setVariantId(((Number) row[0]).longValue());
            item.setProductId(((Number) row[1]).longValue());
            item.setProductName((String) row[2]);
            item.setProductSlug((String) row[3]);
            item.setVariantSku((String) row[4]);
            item.setCurrentStock(((Number) row[5]).intValue());
            item.setReservedStock(((Number) row[6]).intValue());
            item.setAvailableStock(((Number) row[7]).intValue());
            item.setTrackQuantity(true); // Default to true
            item.setContinueSellingWhenOutOfStock(false); // Default to false
            
            // Load warehouse stocks for this variant
            item.setWarehouseStocks(getWarehouseStocks(item.getVariantId()));
            
            items.add(item);
        }

        return items;
    }

    public long countInventoryItems(Shop shop, Map<String, Object> filters) {
        StringBuilder queryBuilder = new StringBuilder("""
            SELECT COUNT(DISTINCT pv.id)
            FROM product_variants pv
            JOIN products p ON pv.product_id = p.id
            LEFT JOIN variant_stocks vs ON pv.id = vs.variant_id
            LEFT JOIN warehouse_locations wl ON vs.warehouse_id = wl.id AND wl.is_active = true
            WHERE p.shop_id = :shopId
            """);

        // Add filters (same as in getInventoryItems)
        if (filters.containsKey("search") && filters.get("search") != null) {
            queryBuilder.append(" AND (p.name ILIKE :search OR pv.sku ILIKE :search)");
        }
        
        if (filters.containsKey("warehouseId") && filters.get("warehouseId") != null) {
            queryBuilder.append(" AND wl.id = :warehouseId");
        }

        Query query = entityManager.createNativeQuery(queryBuilder.toString());
        query.setParameter("shopId", shop.id);
        
        if (filters.containsKey("search") && filters.get("search") != null) {
            query.setParameter("search", "%" + filters.get("search") + "%");
        }
        if (filters.containsKey("warehouseId") && filters.get("warehouseId") != null) {
            query.setParameter("warehouseId", filters.get("warehouseId"));
        }

        return ((Number) query.getSingleResult()).longValue();
    }

    private List<WarehouseStockDTO> getWarehouseStocks(Long variantId) {
        Query query = entityManager.createNativeQuery("""
            SELECT 
                wl.id as warehouse_id,
                wl.name as warehouse_name,
                COALESCE(vs.quantity, 0) as quantity,
                COALESCE(reserved.reserved_quantity, 0) as reserved,
                (COALESCE(vs.quantity, 0) - COALESCE(reserved.reserved_quantity, 0)) as available
            FROM warehouse_locations wl
            LEFT JOIN variant_stocks vs ON wl.id = vs.warehouse_id AND vs.variant_id = :variantId
            LEFT JOIN (
                SELECT warehouse_id, SUM(quantity) as reserved_quantity
                FROM stock_reservations sr
                JOIN variant_stocks vs2 ON sr.variant_id = vs2.variant_id
                WHERE sr.variant_id = :variantId AND sr.expires_at > EXTRACT(EPOCH FROM NOW())
                GROUP BY warehouse_id
            ) reserved ON wl.id = reserved.warehouse_id
            WHERE wl.is_active = true
            ORDER BY wl.name
            """);
        
        query.setParameter("variantId", variantId);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream().map(row -> new WarehouseStockDTO(
            ((Number) row[0]).longValue(),
            (String) row[1],
            ((Number) row[2]).intValue(),
            ((Number) row[3]).intValue(),
            ((Number) row[4]).intValue(),
            0, // incoming - not implemented yet
            0  // committed - not implemented yet
        )).collect(Collectors.toList());
    }

    @Transactional
    public void adjustInventory(Shop shop, InventoryUpdateRequestDTO request, String createdBy) {
        // Find variant with product relationship
        ProductVariant variant = entityManager.createQuery(
                "SELECT pv FROM product_variants pv JOIN FETCH pv.product p WHERE pv.id = :variantId AND p.shop = :shop",
                ProductVariant.class)
                .setParameter("variantId", request.getVariantId())
                .setParameter("shop", shop)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (variant == null) {
            throw new IllegalArgumentException("Product variant not found in this shop");
        }

        WarehouseLocation warehouse = WarehouseLocation.findById(request.getWarehouseId());
        if (warehouse == null || !warehouse.shop.id.equals(shop.id)) {
            throw new IllegalArgumentException("Warehouse not found or not accessible");
        }

        // Find or create variant stock
        VariantStock stock = VariantStock.find("productVariant = ?1 and warehouseLocation = ?2", variant, warehouse).firstResult();
        if (stock == null) {
            stock = new VariantStock();
            stock.productVariant = variant;
            stock.warehouseLocation = warehouse;
            stock.quantity = 0;
        }

        int previousQuantity = stock.quantity;
        int newQuantity;

        switch (request.getAdjustmentType()) {
            case INCREASE:
                newQuantity = previousQuantity + request.getQuantity();
                break;
            case DECREASE:
                newQuantity = Math.max(0, previousQuantity - request.getQuantity());
                break;
            case SET:
                newQuantity = request.getQuantity();
                break;
            default:
                throw new IllegalArgumentException("Unsupported adjustment type: " + request.getAdjustmentType());
        }

        stock.quantity = newQuantity;
        stock.persist();

        // Create adjustment record
        InventoryAdjustment adjustment = new InventoryAdjustment();
        adjustment.shop = shop;
        adjustment.variant = variant;
        adjustment.warehouse = warehouse;
        adjustment.adjustmentType = InventoryAdjustment.AdjustmentType.valueOf(request.getAdjustmentType().name());
        adjustment.quantity = request.getQuantity();
        adjustment.previousQuantity = previousQuantity;
        adjustment.newQuantity = newQuantity;
        adjustment.reason = request.getReason();
        adjustment.notes = request.getNotes();
        adjustment.createdBy = createdBy;
        adjustment.persist();
    }
}
