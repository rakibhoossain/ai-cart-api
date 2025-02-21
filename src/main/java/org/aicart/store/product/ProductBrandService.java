package org.aicart.store.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.aicart.store.product.entity.ProductBrand;

import java.util.List;

@ApplicationScoped
public class ProductBrandService {

    public List<ProductBrand> getAll() {
        return ProductBrand.listAll();
    }

    @Transactional
    public ProductBrand create(String name) {
        ProductBrand brand = new ProductBrand();
        brand.name = name;
        brand.persist();

        return brand;
    }
}
