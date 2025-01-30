package org.aicart.store.product;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.aicart.store.product.entity.CategoryClosure;

@ApplicationScoped
public class CategoryClosureRepository implements PanacheRepository<CategoryClosure> {

    public void deleteSubtree(Long categoryId) {
        delete("descendant.id = ?1", categoryId);
    }
}
