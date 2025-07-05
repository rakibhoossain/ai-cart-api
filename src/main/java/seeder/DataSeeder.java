package seeder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/seed")
@ApplicationScoped
public class DataSeeder {

    @Inject
    EntityManager em;

    Integer range = 500;

    @POST
    @Path("/generate")
    @Transactional
    public void seedData() {
        // Seed products
        insertProducts(range); // Insert 1 million products

        // Insert other data like variants, stocks, prices, images etc.
        insertVariants(range);
        insertVariantPrices();
    }



    private void insertProducts(int numberOfProducts) {
        for (int i = 6; i <= numberOfProducts; i++) {
            String name = "Test product " + i;
            String slug = name.toLowerCase().replace(" ", "-")+i;

            em.createNativeQuery("INSERT INTO products(id, shop_id, name, slug, status, created_at, updated_at) VALUES (:id, 1, :name, :slug, 1, NOW(), NOW())")
                    .setParameter("id", i)
                    .setParameter("name", name)
                    .setParameter("slug", slug)
                    .executeUpdate();
        }
    }

    private void insertVariants(int numberOfVariants) {
        for (int i = 6; i <= numberOfVariants; i++) {
            String sku = "SKU_V_" + i;
            em.createNativeQuery("INSERT INTO product_variants(id, product_id, sku) VALUES (:id, :productId, :sku)")
                    .setParameter("id", i)
                    .setParameter("productId", i) // Linking to the product id
                    .setParameter("sku", sku)
                    .executeUpdate();

            // Insert product variant values
            em.createNativeQuery("INSERT INTO product_variant_value(variant_id, attribute_value_id) VALUES (:variantId, 1), (:variantId, 2)")
                    .setParameter("variantId", i)
                    .executeUpdate();

            // Insert variant stock for the new variant
            em.createNativeQuery("INSERT INTO variant_stocks(variant_id, quantity, warehouse_id) VALUES (:variantId, 50, 1)")
                    .setParameter("variantId", i)
                    .executeUpdate();
        }
    }

    private void insertVariantPrices() {
        for (int i = 6; i <= range; i++) {
            long price = 1345; // Random price between 10 and 5000
            em.createNativeQuery("INSERT INTO variant_prices(country_id, variant_id, price, discount, is_active) VALUES (1, :variantId, :price, 0, TRUE)")
                    .setParameter("variantId", i)
                    .setParameter("price", price)
                    .executeUpdate();
        }
    }
}
