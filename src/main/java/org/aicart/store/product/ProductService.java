package org.aicart.store.product;


import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.aicart.store.product.dto.ProductItemDTO;
import org.aicart.store.product.entity.Product;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductRepository productRepository;


    public List<ProductItemDTO> getPaginateProducts(            Integer page,
                                                                Integer pageSize,
                                                                Optional<Long> minPrice,
                                                                Optional<Long> maxPrice,
                                                                Optional<String> nameFilter,
                                                                Optional<List<Long>> categoryIds,
                                                                Optional<List<Long>> brandIds) {
        return productRepository.getPaginateProducts(page, pageSize, minPrice, maxPrice, nameFilter, categoryIds, brandIds);
    }


    public ProductItemDTO getProductBySlug(String slug) {
        return productRepository.getProductBySlug(slug);
    }

    public List<Map<String, Object>> getProductsWithCategories(int page, int pageSize) {
        // Step 1: Fetch paginated products
        List<Product> products = productRepository.findPaginatedProducts(page, pageSize);
        Log.info(products);
        List<Long> productIds = products.stream().map(product -> product.id).collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Step 2: Fetch categories for these products
        List<Object[]> categoryRows = productRepository.findCategoriesByProductIds(productIds);

        Log.info(categoryRows.stream().toString());

        // Step 3: Map categories to products
        Map<Long, List<Map<String, Object>>> productCategoryMap = new HashMap<>();
        Map<Long, Map<Long, Map<String, Object>>> categoryHierarchyMap = new HashMap<>();

        for (Object[] row : categoryRows) {
            Long productId = ((Number)row[0]).longValue();
            Long categoryId = ((Number)row[1]).longValue();
            String categoryName = (String) row[3];
            Long parentId = ((Number)row[4]).longValue();

            // Build category data
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("id", categoryId);
            categoryData.put("name", categoryName);
            categoryData.put("children", new ArrayList<>());

            productCategoryMap.putIfAbsent(productId, new ArrayList<>());
            categoryHierarchyMap.putIfAbsent(productId, new HashMap<>());

            Map<Long, Map<String, Object>> productHierarchy = categoryHierarchyMap.get(productId);
            productHierarchy.putIfAbsent(categoryId, categoryData);

            if (parentId == null) {
                productCategoryMap.get(productId).add(categoryData);
            } else {
                Map<String, Object> parentCategory = productHierarchy.get(parentId);
                if (parentCategory != null) {
                    ((List<Map<String, Object>>) parentCategory.get("children")).add(categoryData);
                }
            }
        }

        // Step 4: Build the final response
        return products.stream()
                .map(product -> {
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("id", product.id);
                    productData.put("name", product.id); // Name
                    productData.put("categories", productCategoryMap.get(product.id));
                    return productData;
                })
                .collect(Collectors.toList());
    }
}

