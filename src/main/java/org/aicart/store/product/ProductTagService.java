package org.aicart.store.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.aicart.store.product.entity.ProductTag;

import java.util.List;

@ApplicationScoped
public class ProductTagService {

    public List<ProductTag>  getAll() {
        return ProductTag.listAll();
    }

    @Transactional
    public ProductTag create(String name) {
        ProductTag tag = new ProductTag();
        tag.name = name;
        tag.persist();

        return tag;
    }
}
