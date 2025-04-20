package org.aicart.store.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.aicart.store.product.dto.ProductCollectionDTO;
import org.aicart.store.product.entity.*;
import org.aicart.store.product.mapper.ProductCollectionMapper;
import org.aicart.store.user.entity.Shop;
import java.util.List;

@ApplicationScoped
public class ProductCollectionService {

    public List<ProductCollection> getAll() {
        return ProductCollection.listAll();
    }

    @Transactional
    public ProductCollection create(ProductCollectionDTO dto) {
        ProductCollection collection = ProductCollectionMapper.toEntity(dto);
        collection.shop = Shop.findById(1);

        collection.persist();

        return collection;
    }

    @Transactional
    public ProductCollection update(Long collectionId, ProductCollectionDTO dto) {

        ProductCollection collection = ProductCollection.findById(collectionId);
        if( collection == null ) {
            throw new IllegalArgumentException("Collection not exists.");
        }

        ProductCollection newCollection = ProductCollectionMapper.toUpdate(collection, dto);
        newCollection.persist();

        return newCollection;
    }
}
