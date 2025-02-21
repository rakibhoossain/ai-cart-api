package org.aicart.store.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.aicart.store.product.entity.ProductCollection;

import java.util.List;

@ApplicationScoped
public class ProductCollectionService {

    public List<ProductCollection> getAll() {
        return ProductCollection.listAll();
    }

    @Transactional
    public ProductCollection create(String name) {
        ProductCollection collection = new ProductCollection();
        collection.name = name;
        collection.persist();

        return collection;
    }
}
