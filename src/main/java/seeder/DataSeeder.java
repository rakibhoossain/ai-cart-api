package seeder;

import com.github.javafaker.Faker;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/seed")
@ApplicationScoped
public class DataSeeder {

    private final Faker faker = new Faker();

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
        insertVariantImages();
    }



    private void insertProducts(int numberOfProducts) {
        for (int i = 6; i <= numberOfProducts; i++) {
            String sku = "SKU_" + i;
            String name = faker.commerce().productName();
            String slug = name.toLowerCase().replace(" ", "-")+i;

            em.createNativeQuery("INSERT INTO products(id, sku, name, slug, created_at, updated_at) VALUES (:id, :sku, :name, :slug, NOW(), NOW())")
                    .setParameter("id", i)
                    .setParameter("sku", sku)
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
        }
    }

    private void insertVariantPrices() {
        for (int i = 6; i <= range; i++) {
            double price = faker.number().randomDouble(2, 10, 5000); // Random price between 10 and 5000
            em.createNativeQuery("INSERT INTO variant_prices(id, country_id, variant_id, currency_id, price, discount, tax_rate, is_active) VALUES (:id, 1, :variantId, 1, :price, 0, 0, TRUE)")
                    .setParameter("id", i)
                    .setParameter("variantId", i)
                    .setParameter("price", price)
                    .executeUpdate();
        }
    }

    private void insertVariantImages() {
        for (int i = 6; i <= range; i++) {
            String imageUrl = "https://laravel.pixelstrap.net/multikart/storage/49/fashion_173.jpg"; // Random image URL
            em.createNativeQuery("INSERT INTO variant_images(id, variant_id, url) VALUES (:id, :variantId, :url)")
                    .setParameter("id", i)
                    .setParameter("variantId", i)
                    .setParameter("url", imageUrl)
                    .executeUpdate();
        }
    }
}
