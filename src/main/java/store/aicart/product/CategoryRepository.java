package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import store.aicart.product.entity.Category;
import jakarta.transaction.Transactional;
import store.aicart.product.entity.CategoryClosure;

import java.util.List;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<Category> {

    @Transactional
    public Category addCategory(String name, Long parentId) {
        Category category = new Category();
        category.name = name;

        if (parentId != null) {
            Category parent = findById(parentId);
            if (parent == null) {
                throw new IllegalArgumentException("Parent category not found");
            }
            category.parentCategory = parent;
        }

        category.persist();

        addToClosureTable(category, category.parentCategory);

        return category;
    }

    @Transactional
    public void updateParent(Long categoryId, Long newParentId) {
        Category category = findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        Category newParent = newParentId != null ? findById(newParentId) : null;
        if (newParentId != null && newParent == null) {
            throw new IllegalArgumentException("New parent category not found");
        }

        category.parentCategory = newParent;
        category.persist();

        // Clear old closure table relationships
        getEntityManager()
                .createQuery("DELETE FROM CategoryClosure WHERE descendant.id IN " +
                        "(SELECT descendant.id FROM CategoryClosure WHERE ancestor.id = :categoryId)")
                .setParameter("categoryId", categoryId)
                .executeUpdate();

        // Rebuild closure table for the new hierarchy
        addToClosureTable(category, newParent);
    }



    @Transactional
    public void deleteCategory(Long id) {
        Category category = findById(id);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        // Reassign children to the deleted category's parent
        List<Category> children = list("parent.id", id);
        for (Category child : children) {
            child.parentCategory = category.parentCategory;
            child.persist();
        }

        delete("categoryId", id);
        CategoryClosure.delete("descendant.id", id);
    }


    public List<Category> getDescendants(Long categoryId) {
        return getEntityManager()
                .createQuery("SELECT cc.descendant FROM CategoryClosure cc WHERE cc.ancestor.id = :id", Category.class)
                .setParameter("id", categoryId)
                .getResultList();
    }

    public List<Category> getAncestors(Long categoryId) {
        return getEntityManager()
                .createQuery("SELECT cc.ancestor FROM CategoryClosure cc WHERE cc.descendant.id = :id", Category.class)
                .setParameter("id", categoryId)
                .getResultList();
    }

    private void addToClosureTable(Category category, Category parent) {
        // Add ancestor relationships
        if (parent != null) {
            List<CategoryClosure> parentClosures = CategoryClosure.list("descendant.id", parent.id);
            for (CategoryClosure closure : parentClosures) {
                CategoryClosure newClosure = new CategoryClosure();
                newClosure.ancestor = closure.ancestor;
                newClosure.descendant = category;
                newClosure.depth = closure.depth + 1;
                newClosure.persist();
            }
        }

        // Add self-relationship
        CategoryClosure selfClosure = new CategoryClosure();
        selfClosure.ancestor = category;
        selfClosure.descendant = category;
        selfClosure.depth = 0;
        selfClosure.persist();
    }
}
