package org.aicart.store.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.aicart.store.product.entity.ProductType;

import java.util.List;

@ApplicationScoped
public class ProductTypeService {

    public List<ProductType> getAll() {
        return ProductType.listAll();
    }

    @Transactional
    public ProductType create(String name) {
        ProductType productType = new ProductType();
        productType.name = name;
        productType.persist();

        return productType;
    }
}
