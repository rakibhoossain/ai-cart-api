package org.aicart.store.product.brand.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.aicart.store.product.brand.dto.ProductBrandDTO;
import org.aicart.store.product.brand.mapper.ProductBrandMapper;
import org.aicart.store.product.brand.repository.ProductBrandRepository;
import org.aicart.store.product.entity.ProductBrand;
import org.aicart.store.user.entity.Shop;

import java.util.List;

@ApplicationScoped
public class ProductBrandService {

    @Inject
    ProductBrandRepository brandRepository;
    
    /**
     * Find a brand by ID
     */
    public ProductBrand findById(Long id, Shop shop) {
        return brandRepository.findByIdAndShop(id, shop);
    }
    
    /**
     * Get brands with pagination, sorting, and optional search
     */
    public List<ProductBrand> getBrands(int page, int size, String sortField, boolean ascending, String searchQuery, Shop shop) {
        return brandRepository.getBrands(page, size, sortField, ascending, searchQuery, shop);
    }
    
    /**
     * Count total number of brands with optional filtering
     */
    public long countBrands(String searchQuery, Shop shop) {
        return brandRepository.countBrands(searchQuery, shop);
    }
    
    /**
     * Create a new brand
     */
    @Transactional
    public ProductBrand createBrand(String name, Shop shop) {
        ProductBrand brand = new ProductBrand();
        brand.name = name;
        brand.shop = shop;
        brand.persist();
        return brand;
    }
    
    /**
     * Update an existing brand
     */
    @Transactional
    public ProductBrand updateBrand(Long id, String name, Shop shop) {
        ProductBrand brand = findById(id, shop);
        if (brand == null) {
            throw new IllegalArgumentException("Brand not found");
        }
        
        brand.name = name;
        brand.persist();
        return brand;
    }
    
    /**
     * Delete a brand
     */
    @Transactional
    public void deleteBrand(Long id, Shop shop) {
        ProductBrand brand = findById(id, shop);
        if (brand == null) {
            throw new IllegalArgumentException("Brand not found");
        }
        
        // Check if brand is used by any products before deleting
        long productCount = brandRepository.getEntityManager()
                .createQuery("SELECT COUNT(p) FROM products p WHERE p.productBrand.id = :brandId", Long.class)
                .setParameter("brandId", id)
                .getSingleResult();
                
        if (productCount > 0) {
            throw new IllegalArgumentException("Cannot delete brand that is used by products");
        }
        
        brand.delete();
    }
}