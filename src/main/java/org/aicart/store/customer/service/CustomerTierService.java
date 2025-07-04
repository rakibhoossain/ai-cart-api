package org.aicart.store.customer.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.customer.entity.CustomerTier;
import org.aicart.store.customer.CustomerRepository;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class CustomerTierService {

    @Inject
    CustomerRepository customerRepository;

    // Tier thresholds in cents (configurable per shop in the future)
    private static final Long BRONZE_THRESHOLD = 0L;
    private static final Long SILVER_THRESHOLD = 50000L; // $500.00
    private static final Long GOLD_THRESHOLD = 200000L; // $2000.00
    private static final Long PLATINUM_THRESHOLD = 500000L; // $5000.00
    private static final Long DIAMOND_THRESHOLD = 1000000L; // $10000.00

    /**
     * Calculate and update customer tier based on total spending
     */
    @Transactional
    public CustomerTier calculateTier(Customer customer) {
        Long totalSpent = customer.totalSpent != null ? customer.totalSpent : 0L;

        CustomerTier newTier;
        if (totalSpent >= DIAMOND_THRESHOLD) {
            newTier = CustomerTier.DIAMOND;
        } else if (totalSpent >= PLATINUM_THRESHOLD) {
            newTier = CustomerTier.PLATINUM;
        } else if (totalSpent >= GOLD_THRESHOLD) {
            newTier = CustomerTier.GOLD;
        } else if (totalSpent >= SILVER_THRESHOLD) {
            newTier = CustomerTier.SILVER;
        } else {
            newTier = CustomerTier.BRONZE;
        }

        // Update tier if it has changed
        if (customer.customerTier != newTier) {
            CustomerTier previousTier = customer.customerTier;
            customer.customerTier = newTier;
            customer.tierUpdatedAt = LocalDateTime.now();
            
            // Log tier change
            logTierChange(customer, previousTier, newTier, "AUTOMATIC", totalSpent);
        }

        return newTier;
    }

    /**
     * Manually override customer tier
     */
    @Transactional
    public void overrideTier(Customer customer, CustomerTier newTier, String updatedBy, String reason) {
        CustomerTier previousTier = customer.customerTier;
        customer.customerTier = newTier;
        customer.tierUpdatedAt = LocalDateTime.now();
        customer.tierOverridden = true;
        customer.tierOverrideReason = reason;
        
        // Log manual tier change
        logTierChange(customer, previousTier, newTier, "MANUAL_" + updatedBy, customer.totalSpent);
    }

    /**
     * Reset tier override and recalculate based on spending
     */
    @Transactional
    public CustomerTier resetTierOverride(Customer customer, String updatedBy) {
        customer.tierOverridden = false;
        customer.tierOverrideReason = null;
        
        CustomerTier calculatedTier = calculateTier(customer);
        logTierChange(customer, customer.customerTier, calculatedTier, "RESET_" + updatedBy, customer.totalSpent);
        
        return calculatedTier;
    }

    /**
     * Update customer spending and recalculate tier
     */
    @Transactional
    public void updateSpendingAndTier(Customer customer, Long orderAmountInCents) {
        // Update total spent
        if (customer.totalSpent == null) {
            customer.totalSpent = 0L;
        }
        customer.totalSpent = customer.totalSpent + orderAmountInCents;

        // Update order count
        customer.totalOrders = (customer.totalOrders != null ? customer.totalOrders : 0) + 1;

        // Update last order date
        customer.lastOrderAt = LocalDateTime.now();
        
        // Recalculate tier only if not manually overridden
        if (!customer.tierOverridden) {
            calculateTier(customer);
        }
        
        // Update customer type based on behavior
        updateCustomerType(customer);
    }

    /**
     * Update customer type based on behavior patterns
     */
    private void updateCustomerType(Customer customer) {
        // Don't override manually set customer types
        if (customer.customerTypeOverridden) {
            return;
        }

        int totalOrders = customer.totalOrders != null ? customer.totalOrders : 0;
        Long totalSpent = customer.totalSpent != null ? customer.totalSpent : 0L;

        // Determine customer type based on behavior
        if (totalOrders == 0) {
            customer.customerType = org.aicart.store.customer.entity.CustomerType.PROSPECT;
        } else if (totalOrders == 1) {
            customer.customerType = org.aicart.store.customer.entity.CustomerType.GUEST;
        } else if (totalOrders >= 2 && totalOrders < 5) {
            customer.customerType = org.aicart.store.customer.entity.CustomerType.RETURNING;
        } else if (totalOrders >= 5 && totalSpent >= 100000L) { // $1000.00
            customer.customerType = org.aicart.store.customer.entity.CustomerType.LOYAL;
        } else if (totalSpent >= 500000L) { // $5000.00
            customer.customerType = org.aicart.store.customer.entity.CustomerType.VIP;
        } else {
            customer.customerType = org.aicart.store.customer.entity.CustomerType.REGULAR;
        }
    }

    /**
     * Bulk update tiers for all customers in a shop
     */
    @Transactional
    public int bulkUpdateTiers(Shop shop) {
        List<Customer> customers = customerRepository.find("shop = ?1", shop).list();
        int updatedCount = 0;

        for (Customer customer : customers) {
            if (!customer.tierOverridden) {
                CustomerTier oldTier = customer.customerTier;
                CustomerTier newTier = calculateTier(customer);
                if (oldTier != newTier) {
                    updatedCount++;
                }
            }
        }

        return updatedCount;
    }

    /**
     * Get tier requirements/thresholds
     */
    public TierRequirements getTierRequirements() {
        return new TierRequirements(
            BRONZE_THRESHOLD,
            SILVER_THRESHOLD,
            GOLD_THRESHOLD,
            PLATINUM_THRESHOLD,
            DIAMOND_THRESHOLD
        );
    }

    /**
     * Log tier changes for audit purposes
     */
    private void logTierChange(Customer customer, CustomerTier fromTier, CustomerTier toTier,
                              String changeType, Long totalSpent) {
        // TODO: Implement tier change logging
        System.out.println(String.format(
            "Customer %d tier changed from %s to %s (%s) - Total Spent: %s",
            customer.id, fromTier, toTier, changeType, totalSpent
        ));
    }

    /**
     * DTO for tier requirements
     */
    public static class TierRequirements {
        public final Long bronze;
        public final Long silver;
        public final Long gold;
        public final Long platinum;
        public final Long diamond;

        public TierRequirements(Long bronze, Long silver, Long gold,
                               Long platinum, Long diamond) {
            this.bronze = bronze;
            this.silver = silver;
            this.gold = gold;
            this.platinum = platinum;
            this.diamond = diamond;
        }
    }
}
