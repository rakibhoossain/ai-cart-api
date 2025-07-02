package org.aicart.store.customer;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.customer.entity.CustomerType;
import org.aicart.store.customer.entity.CustomerTier;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {

    public Optional<Customer> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public List<Customer> search(String query) {
        return find("lower(firstName) like lower(?1) or lower(lastName) like lower(?1)", "%" + query + "%").list();
    }

    /**
     * Find customer by ID and shop
     */
    public Customer findByIdAndShop(Long id, Shop shop) {
        return find("id = ?1 and shop = ?2", id, shop).firstResult();
    }

    /**
     * Find customer by email and shop
     */
    public Customer findByEmailAndShop(String email, Shop shop) {
        return find("email = ?1 and shop = ?2", email, shop).firstResult();
    }

    /**
     * Find customers with filters and pagination
     */
    public PanacheQuery<Customer> findWithFilters(Shop shop, String search, CustomerType customerType,
                                                 CustomerTier customerTier, Boolean emailVerified,
                                                 Boolean accountLocked, LocalDateTime startDate,
                                                 LocalDateTime endDate, String sortBy, String order) {

        StringBuilder query = new StringBuilder("shop = ?1");
        List<Object> params = new ArrayList<>();
        params.add(shop);
        int paramIndex = 2;

        // Search filter
        if (search != null && !search.trim().isEmpty()) {
            query.append(" and (lower(firstName) like ?").append(paramIndex)
                 .append(" or lower(lastName) like ?").append(paramIndex)
                 .append(" or lower(email) like ?").append(paramIndex)
                 .append(" or lower(phone) like ?").append(paramIndex)
                 .append(" or lower(company) like ?").append(paramIndex).append(")");
            params.add("%" + search.toLowerCase() + "%");
            paramIndex++;
        }

        // Customer type filter
        if (customerType != null) {
            query.append(" and customerType = ?").append(paramIndex);
            params.add(customerType);
            paramIndex++;
        }

        // Customer tier filter
        if (customerTier != null) {
            query.append(" and customerTier = ?").append(paramIndex);
            params.add(customerTier);
            paramIndex++;
        }

        // Email verified filter
        if (emailVerified != null) {
            query.append(" and emailVerified = ?").append(paramIndex);
            params.add(emailVerified);
            paramIndex++;
        }

        // Account locked filter
        if (accountLocked != null) {
            query.append(" and accountLocked = ?").append(paramIndex);
            params.add(accountLocked);
            paramIndex++;
        }

        // Date range filter
        if (startDate != null) {
            query.append(" and createdAt >= ?").append(paramIndex);
            params.add(startDate);
            paramIndex++;
        }

        if (endDate != null) {
            query.append(" and createdAt <= ?").append(paramIndex);
            params.add(endDate);
            paramIndex++;
        }

        // Create sort
        Sort sort = Sort.by(sortBy != null ? sortBy : "createdAt");
        if ("desc".equalsIgnoreCase(order)) {
            sort = sort.descending();
        }

        return find(query.toString(), sort, params.toArray());
    }

    /**
     * Count customers with filters
     */
    public long countWithFilters(Shop shop, String search, CustomerType customerType,
                                CustomerTier customerTier, Boolean emailVerified,
                                Boolean accountLocked, LocalDateTime startDate,
                                LocalDateTime endDate) {

        StringBuilder query = new StringBuilder("shop = ?1");
        List<Object> params = new ArrayList<>();
        params.add(shop);
        int paramIndex = 2;

        // Apply same filters as findWithFilters
        if (search != null && !search.trim().isEmpty()) {
            query.append(" and (lower(firstName) like ?").append(paramIndex)
                 .append(" or lower(lastName) like ?").append(paramIndex)
                 .append(" or lower(email) like ?").append(paramIndex)
                 .append(" or lower(phone) like ?").append(paramIndex)
                 .append(" or lower(company) like ?").append(paramIndex).append(")");
            params.add("%" + search.toLowerCase() + "%");
            paramIndex++;
        }

        if (customerType != null) {
            query.append(" and customerType = ?").append(paramIndex);
            params.add(customerType);
            paramIndex++;
        }

        if (customerTier != null) {
            query.append(" and customerTier = ?").append(paramIndex);
            params.add(customerTier);
            paramIndex++;
        }

        if (emailVerified != null) {
            query.append(" and emailVerified = ?").append(paramIndex);
            params.add(emailVerified);
            paramIndex++;
        }

        if (accountLocked != null) {
            query.append(" and accountLocked = ?").append(paramIndex);
            params.add(accountLocked);
            paramIndex++;
        }

        if (startDate != null) {
            query.append(" and createdAt >= ?").append(paramIndex);
            params.add(startDate);
            paramIndex++;
        }

        if (endDate != null) {
            query.append(" and createdAt <= ?").append(paramIndex);
            params.add(endDate);
            paramIndex++;
        }

        return count(query.toString(), params.toArray());
    }

    /**
     * Find customers by tags
     */
    public List<Customer> findByTags(Shop shop, String tag) {
        return find("shop = ?1 and tags like ?2", shop, "%" + tag + "%").list();
    }

    /**
     * Find VIP customers
     */
    public List<Customer> findVIPCustomers(Shop shop) {
        return find("shop = ?1 and (customerType = ?2 or customerTier in (?3, ?4, ?5))",
                   shop, CustomerType.VIP, CustomerTier.GOLD, CustomerTier.PLATINUM, CustomerTier.DIAMOND).list();
    }

    /**
     * Find customers with high lifetime value
     */
    public List<Customer> findHighValueCustomers(Shop shop, Long minLifetimeValue) {
        return find("shop = ?1 and lifetimeValue >= ?2 order by lifetimeValue desc",
                   shop, minLifetimeValue).list();
    }

    /**
     * Find inactive customers
     */
    public List<Customer> findInactiveCustomers(Shop shop, LocalDateTime lastActivityBefore) {
        return find("shop = ?1 and (lastActivityAt < ?2 or lastActivityAt is null)",
                   shop, lastActivityBefore).list();
    }

    /**
     * Count customers by shop
     */
    public long countByShop(Shop shop) {
        return count("shop = ?1", shop);
    }

    /**
     * Count new customers in date range
     */
    public long countNewCustomers(Shop shop, LocalDateTime startDate, LocalDateTime endDate) {
        return count("shop = ?1 and createdAt >= ?2 and createdAt <= ?3",
                    shop, startDate, endDate);
    }
}
