package org.aicart.store.order;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.aicart.PaymentStatusEnum;
import org.aicart.store.order.dto.OrderListDTO;
import org.aicart.store.order.entity.Order;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order>  {

    @PersistenceContext
    EntityManager em;

    public List<OrderListDTO> findOrdersWithFilters(Shop shop, String search, OrderStatusEnum status,
                                                    PaymentStatusEnum paymentStatus, LocalDateTime startDate,
                                                    LocalDateTime endDate, int page, int size,
                                                    String sortBy, String order) {

        StringBuilder queryBuilder = new StringBuilder("""
            SELECT new org.aicart.store.order.dto.OrderListDTO(
                o.id,
                COALESCE(CONCAT(c.firstName, ' ', c.lastName), ob.fullName, 'Guest'),
                COALESCE(c.email, ob.email, 'N/A'),
                o.totalPrice,
                o.currency,
                o.status,
                o.paymentStatus,
                o.paymentType,
                o.createdAt,
                o.updatedAt,
                SIZE(o.items)
            )
            FROM Order o
            LEFT JOIN o.customer c
            LEFT JOIN o.billing ob
            WHERE o.shop.id = :shopId
        """);

        Map<String, Object> params = new HashMap<>();
        params.put("shopId", shop.id);

        // Add search filter
        if (search != null && !search.trim().isEmpty()) {
            queryBuilder.append("""
                AND (
                    LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE :search OR
                    LOWER(ob.fullName) LIKE :search OR
                    LOWER(c.email) LIKE :search OR
                    LOWER(ob.email) LIKE :search OR
                    CAST(o.id AS string) LIKE :search
                )
            """);
            params.put("search", "%" + search.toLowerCase() + "%");
        }

        // Add status filter
        if (status != null) {
            queryBuilder.append(" AND o.status = :status");
            params.put("status", status);
        }

        // Add payment status filter
        if (paymentStatus != null) {
            queryBuilder.append(" AND o.paymentStatus = :paymentStatus");
            params.put("paymentStatus", paymentStatus);
        }

        // Add date range filter
        if (startDate != null) {
            queryBuilder.append(" AND o.createdAt >= :startDate");
            params.put("startDate", startDate);
        }
        if (endDate != null) {
            queryBuilder.append(" AND o.createdAt <= :endDate");
            params.put("endDate", endDate);
        }

        // Add sorting
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String direction = "desc".equalsIgnoreCase(order) ? "DESC" : "ASC";
            queryBuilder.append(" ORDER BY o.").append(sortBy).append(" ").append(direction);
        } else {
            queryBuilder.append(" ORDER BY o.createdAt DESC");
        }

        TypedQuery<OrderListDTO> query = em.createQuery(queryBuilder.toString(), OrderListDTO.class);

        // Set parameters
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countOrdersWithFilters(Shop shop, String search, OrderStatusEnum status,
                                      PaymentStatusEnum paymentStatus, LocalDateTime startDate,
                                      LocalDateTime endDate) {

        StringBuilder queryBuilder = new StringBuilder("""
            SELECT COUNT(o) FROM Order o
            LEFT JOIN o.customer c
            LEFT JOIN o.billing ob
            WHERE o.shop.id = :shopId
        """);

        Map<String, Object> params = new HashMap<>();
        params.put("shopId", shop.id);

        // Add search filter
        if (search != null && !search.trim().isEmpty()) {
            queryBuilder.append("""
                AND (
                    LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE :search OR
                    LOWER(ob.fullName) LIKE :search OR
                    LOWER(c.email) LIKE :search OR
                    LOWER(ob.email) LIKE :search OR
                    CAST(o.id AS string) LIKE :search
                )
            """);
            params.put("search", "%" + search.toLowerCase() + "%");
        }

        // Add status filter
        if (status != null) {
            queryBuilder.append(" AND o.status = :status");
            params.put("status", status);
        }

        // Add payment status filter
        if (paymentStatus != null) {
            queryBuilder.append(" AND o.paymentStatus = :paymentStatus");
            params.put("paymentStatus", paymentStatus);
        }

        // Add date range filter
        if (startDate != null) {
            queryBuilder.append(" AND o.createdAt >= :startDate");
            params.put("startDate", startDate);
        }
        if (endDate != null) {
            queryBuilder.append(" AND o.createdAt <= :endDate");
            params.put("endDate", endDate);
        }

        TypedQuery<Long> query = em.createQuery(queryBuilder.toString(), Long.class);

        // Set parameters
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getSingleResult();
    }

    public Order findByIdAndShop(Long id, Shop shop) {
        return find("id = ?1 and shop.id = ?2", id, shop.id).firstResult();
    }

    public boolean existsByIdAndShop(Long id, Shop shop) {
        return count("id = ?1 and shop.id = ?2", id, shop.id) > 0;
    }
}
