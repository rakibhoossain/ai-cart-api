package org.aicart.store.product.process;

import jakarta.enterprise.context.ApplicationScoped;
import org.aicart.store.product.ProductCollectionFieldEnum;
import org.aicart.store.product.ProductCollectionOperatorEnum;
import org.aicart.store.product.ProductConditionMatchEnum;
import org.aicart.store.product.entity.ProductCollectionCondition;

import java.math.BigDecimal;
import java.util.*;

@ApplicationScoped
public class SmartCollectionQueryBuilder {

    private static final int BATCH_SIZE = 5000;

    public static class QueryBuildResult {
        public final String query;
        public final Map<String, Object> params;
        public final String countQuery;

        public QueryBuildResult(String query, Map<String, Object> params) {
            this.query = query;
            this.params = params;
            this.countQuery = query.replaceFirst("SELECT p.id", "SELECT COUNT(DISTINCT p.id)");
        }
    }

    public QueryBuildResult buildQuery(Long collectionId, ProductConditionMatchEnum matchType, List<ProductCollectionCondition> conditions) {
        if (conditions.isEmpty()) {
            return new QueryBuildResult("SELECT p.id FROM products p WHERE false", Map.of());
        }

        StringBuilder query = new StringBuilder("""
            WITH active_prices AS (
                SELECT pv.product_id, MIN(vp.price) AS min_price
                FROM product_variants pv
                JOIN variant_prices vp ON pv.id = vp.variant_id AND vp.is_active = true
                GROUP BY pv.product_id
            )
            SELECT DISTINCT p.id FROM products p
            """);

        Map<String, Object> params = new HashMap<>();
        List<String> conditionClauses = new ArrayList<>();
        int paramCount = 0;

        for (ProductCollectionCondition cond : conditions) {
            String paramPrefix = "p" + (++paramCount);
            String clause = buildCondition(cond, paramPrefix, params);
            conditionClauses.add(clause);
        }

        if (matchType.equals(ProductConditionMatchEnum.ALL)) {
            query.append("WHERE (").append(String.join(" AND ", conditionClauses)).append(")");
        } else {
            query.append("WHERE (").append(String.join(" OR ", conditionClauses)).append(")");
        }

        query.append(" ORDER BY p.id LIMIT ").append(BATCH_SIZE);


        System.out.println(query.toString());

        return new QueryBuildResult(query.toString(), params);
    }

    private String buildCondition(ProductCollectionCondition condition, String paramPrefix, Map<String, Object> params) {
        return switch (condition.field) {
            case ProductCollectionFieldEnum.PRICE -> buildPriceCondition(condition, paramPrefix, params);
            case ProductCollectionFieldEnum.TAG -> buildTagCondition(condition, paramPrefix, params);
            case ProductCollectionFieldEnum.CATEGORY -> buildCategoryCondition(condition, paramPrefix, params);
            case ProductCollectionFieldEnum.TITLE -> buildTitleCondition(condition, paramPrefix, params);
            case ProductCollectionFieldEnum.SKU -> buildSkuCondition(condition, paramPrefix, params);
            default -> throw new IllegalArgumentException("Unsupported field: " + condition.field.getValue());
        };
    }

    private String buildPriceCondition(ProductCollectionCondition condition, String paramPrefix, Map<String, Object> params) {
        params.put(paramPrefix, new BigDecimal(condition.numericValue));
        return """
            COALESCE((SELECT min_price FROM active_prices WHERE product_id = p.id), 0) %s :%s
            """.formatted(getSqlOperator(condition.operator), paramPrefix);
    }

    private String buildTagCondition(ProductCollectionCondition condition, String paramPrefix, Map<String, Object> params) {
        params.put(paramPrefix, condition.referenceId);
        return """
            EXISTS (SELECT 1 FROM product_tag_pivot WHERE product_id = p.id AND tag_id = :%s)
            """.formatted(paramPrefix);
    }

    private String buildCategoryCondition(ProductCollectionCondition condition, String paramPrefix, Map<String, Object> params) {
        params.put(paramPrefix, condition.referenceId);
        return """
            EXISTS (
                SELECT 1 FROM product_category pc
                JOIN category_closure cc ON pc.category_id = cc.descendant_id
                WHERE pc.product_id = p.id AND cc.ancestor_id = :%s
            )
            """.formatted(paramPrefix);
    }

    private String buildTitleCondition(ProductCollectionCondition condition, String paramPrefix, Map<String, Object> params) {
        params.put(paramPrefix, condition.stringValue);
        return "p.name " + getSqlOperator(condition.operator) + " :" + paramPrefix;
    }

    private String buildSkuCondition(ProductCollectionCondition condition, String paramPrefix, Map<String, Object> params) {
        params.put(paramPrefix, condition.stringValue);
        return """
            EXISTS (SELECT 1 FROM product_variants WHERE product_id = p.id AND sku %s :%s)
            """.formatted(getSqlOperator(condition.operator), paramPrefix);
    }

    private String getSqlOperator(ProductCollectionOperatorEnum operator) {
        return switch (operator) {
            case ProductCollectionOperatorEnum.EQUALS -> "=";
            case ProductCollectionOperatorEnum.NOT_EQUALS -> "<>";
            case ProductCollectionOperatorEnum.GREATER_THAN -> ">";
            case ProductCollectionOperatorEnum.LESS_THAN -> "<";
            case ProductCollectionOperatorEnum.CONTAINS -> "ILIKE '%' || ? || '%'";
            case ProductCollectionOperatorEnum.NOT_CONTAINS -> "NOT ILIKE '%' || ? || '%'";
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }
}
