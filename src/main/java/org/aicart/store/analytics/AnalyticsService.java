package org.aicart.store.analytics;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.aicart.store.analytics.dto.DashboardStatsDTO;
import org.aicart.store.analytics.dto.DashboardStatsDTO.ChartDataPoint;
import org.aicart.store.analytics.dto.DashboardStatsDTO.CategoryDataPoint;
import org.aicart.store.analytics.dto.DashboardStatsDTO.RecentOrderDTO;
import org.aicart.store.user.entity.Shop;
import org.aicart.store.context.ShopContext;
import org.aicart.store.order.OrderStatusEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AnalyticsService {

    @Inject
    EntityManager entityManager;

    @Inject
    ShopContext shopContext;

    public DashboardStatsDTO getDashboardStats(String period) {
        Shop shop = Shop.findById(shopContext.getShopId());
        if (shop == null) {
            throw new RuntimeException("Shop not found");
        }
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // Calculate date ranges
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentPeriodStart = getPeriodStart(now, period);
        LocalDateTime previousPeriodStart = getPeriodStart(currentPeriodStart.minusDays(1), period);
        LocalDateTime previousPeriodEnd = currentPeriodStart.minusSeconds(1);
        
        // Get KPI stats
        stats.setTotalRevenue(getTotalRevenue(shop, currentPeriodStart, now));
        stats.setRevenueGrowthPercent(calculateGrowthPercent(
            getTotalRevenue(shop, currentPeriodStart, now),
            getTotalRevenue(shop, previousPeriodStart, previousPeriodEnd)
        ));
        
        stats.setTotalCustomers(getTotalCustomers(shop, currentPeriodStart, now));
        stats.setCustomerGrowthPercent(calculateGrowthPercent(
            BigDecimal.valueOf(getTotalCustomers(shop, currentPeriodStart, now)),
            BigDecimal.valueOf(getTotalCustomers(shop, previousPeriodStart, previousPeriodEnd))
        ));
        
        stats.setTotalOrders(getTotalOrders(shop, currentPeriodStart, now));
        stats.setOrderGrowthPercent(calculateGrowthPercent(
            BigDecimal.valueOf(getTotalOrders(shop, currentPeriodStart, now)),
            BigDecimal.valueOf(getTotalOrders(shop, previousPeriodStart, previousPeriodEnd))
        ));
        
        stats.setActiveCustomers(getActiveCustomers(shop));
        stats.setActiveCustomersChange(getActiveCustomersChange(shop));
        
        // Get chart data
        stats.setRevenueChart(getRevenueChartData(shop, period));
        stats.setOrderChart(getOrderChartData(shop, period));
        stats.setTopCategories(getTopCategories(shop, period));
        stats.setRecentOrders(getRecentOrders(shop));
        
        return stats;
    }
    
    private LocalDateTime getPeriodStart(LocalDateTime date, String period) {
        return switch (period.toLowerCase()) {
            case "week" -> date.minusWeeks(1);
            case "month" -> date.minusMonths(1);
            case "quarter" -> date.minusMonths(3);
            case "year" -> date.minusYears(1);
            default -> date.minusMonths(1); // default to month
        };
    }
    
    private BigDecimal getTotalRevenue(Shop shop, LocalDateTime start, LocalDateTime end) {
        Query query = entityManager.createQuery(
            "SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o " +
            "WHERE o.shop = :shop AND o.createdAt BETWEEN :start AND :end " +
            "AND o.status NOT IN (:cancelled, :refunded)"
        );
        query.setParameter("shop", shop);
        query.setParameter("start", start);
        query.setParameter("end", end);
        query.setParameter("cancelled", OrderStatusEnum.CANCELED);
        query.setParameter("refunded", OrderStatusEnum.REFUNDED);

        Object result = query.getSingleResult();
        if (result instanceof Number) {
            return new BigDecimal(result.toString()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
    
    private Long getTotalCustomers(Shop shop, LocalDateTime start, LocalDateTime end) {
        Query query = entityManager.createQuery(
            "SELECT COUNT(DISTINCT c.id) FROM Customer c " +
            "WHERE c.shop = :shop AND c.createdAt BETWEEN :start AND :end"
        );
        query.setParameter("shop", shop);
        query.setParameter("start", start);
        query.setParameter("end", end);
        
        return (Long) query.getSingleResult();
    }
    
    private Long getTotalOrders(Shop shop, LocalDateTime start, LocalDateTime end) {
        Query query = entityManager.createQuery(
            "SELECT COUNT(o.id) FROM Order o " +
            "WHERE o.shop = :shop AND o.createdAt BETWEEN :start AND :end " +
            "AND o.status != :cancelled"
        );
        query.setParameter("shop", shop);
        query.setParameter("start", start);
        query.setParameter("end", end);
        query.setParameter("cancelled", OrderStatusEnum.CANCELED);

        return (Long) query.getSingleResult();
    }
    
    private Long getActiveCustomers(Shop shop) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Query query = entityManager.createQuery(
            "SELECT COUNT(DISTINCT o.customer.id) FROM Order o " +
            "WHERE o.shop = :shop AND o.createdAt >= :thirtyDaysAgo " +
            "AND o.status != :cancelled"
        );
        query.setParameter("shop", shop);
        query.setParameter("thirtyDaysAgo", thirtyDaysAgo);
        query.setParameter("cancelled", OrderStatusEnum.CANCELED);

        return (Long) query.getSingleResult();
    }
    
    private Long getActiveCustomersChange(Shop shop) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime sixtyDaysAgo = LocalDateTime.now().minusDays(60);
        
        Long currentActive = getActiveCustomers(shop);
        
        Query query = entityManager.createQuery(
            "SELECT COUNT(DISTINCT o.customer.id) FROM Order o " +
            "WHERE o.shop = :shop AND o.createdAt BETWEEN :sixtyDaysAgo AND :thirtyDaysAgo " +
            "AND o.status != :cancelled"
        );
        query.setParameter("shop", shop);
        query.setParameter("sixtyDaysAgo", sixtyDaysAgo);
        query.setParameter("thirtyDaysAgo", thirtyDaysAgo);
        query.setParameter("cancelled", OrderStatusEnum.CANCELED);
        
        Long previousActive = (Long) query.getSingleResult();
        return currentActive - previousActive;
    }
    
    private BigDecimal calculateGrowthPercent(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
    }
    
    private List<ChartDataPoint> getRevenueChartData(Shop shop, String period) {
        // Implementation for revenue chart data over time
        List<ChartDataPoint> chartData = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        int days = getDaysForPeriod(period);
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            LocalDateTime dayStart = date.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = date.withHour(23).withMinute(59).withSecond(59);
            
            BigDecimal revenue = getTotalRevenue(shop, dayStart, dayEnd);
            Long orderCount = getTotalOrders(shop, dayStart, dayEnd);
            
            chartData.add(new ChartDataPoint(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                revenue,
                orderCount
            ));
        }
        
        return chartData;
    }
    
    private List<ChartDataPoint> getOrderChartData(Shop shop, String period) {
        // Similar to revenue chart but focused on order counts
        return getRevenueChartData(shop, period); // Reuse for now, can be customized
    }
    
    private List<CategoryDataPoint> getTopCategories(Shop shop, String period) {
        LocalDateTime start = getPeriodStart(LocalDateTime.now(), period);
        
        Query query = entityManager.createQuery(
            "SELECT c.name, SUM(oi.price * oi.quantity), COUNT(DISTINCT o.id) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.product p " +
            "JOIN p.categories c " +
            "WHERE o.shop = :shop AND o.createdAt >= :start " +
            "AND o.status NOT IN (:cancelled, :refunded) " +
            "GROUP BY c.id, c.name " +
            "ORDER BY SUM(oi.price * oi.quantity) DESC"
        );
        query.setParameter("shop", shop);
        query.setParameter("start", start);
        query.setParameter("cancelled", OrderStatusEnum.CANCELED);
        query.setParameter("refunded", OrderStatusEnum.REFUNDED);
        query.setMaxResults(5);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<CategoryDataPoint> categories = new ArrayList<>();
        String[] colors = {"hsl(var(--chart-1))", "hsl(var(--chart-2))", "hsl(var(--chart-3))", "hsl(var(--chart-4))", "hsl(var(--chart-5))"};
        
        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);
            String categoryName = (String) row[0];
            BigDecimal revenue = new BigDecimal(row[1].toString()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            Long orderCount = (Long) row[2];
            String color = colors[i % colors.length];
            
            categories.add(new CategoryDataPoint(categoryName, revenue, orderCount, color));
        }
        
        return categories;
    }
    
    private List<RecentOrderDTO> getRecentOrders(Shop shop) {
        Query query = entityManager.createQuery(
            "SELECT o.id, CONCAT(COALESCE(c.firstName, ''), ' ', COALESCE(c.lastName, '')), " +
            "c.email, o.totalPrice, o.createdAt, o.status " +
            "FROM Order o " +
            "LEFT JOIN o.customer c " +
            "WHERE o.shop = :shop " +
            "ORDER BY o.createdAt DESC"
        );
        query.setParameter("shop", shop);
        query.setMaxResults(5);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<RecentOrderDTO> recentOrders = new ArrayList<>();
        
        for (Object[] row : results) {
            RecentOrderDTO order = new RecentOrderDTO();
            order.setOrderId((Long) row[0]);
            order.setCustomerName(((String) row[1]).trim());
            order.setCustomerEmail((String) row[2]);
            order.setOrderTotal(new BigDecimal(row[3].toString()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            order.setOrderDate(((LocalDateTime) row[4]).format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            order.setStatus(((OrderStatusEnum) row[5]).name());

            recentOrders.add(order);
        }
        
        return recentOrders;
    }
    
    private int getDaysForPeriod(String period) {
        return switch (period.toLowerCase()) {
            case "week" -> 7;
            case "month" -> 30;
            case "quarter" -> 90;
            case "year" -> 365;
            default -> 30;
        };
    }
}
