package org.aicart.store.customer.service;

import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.aicart.store.customer.dto.WishlistDTO;
import org.aicart.store.customer.dto.WishlistResponseDTO;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.customer.entity.Wishlist;
import org.aicart.store.product.ProductService;
import org.aicart.store.product.dto.ProductItemDTO;
import org.aicart.store.product.entity.Product;
import org.aicart.store.user.entity.Shop;
import org.aicart.store.context.ShopContext;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class WishlistService {

    @Inject
    ShopContext shopContext;

    @Inject
    ProductService productService;

    @Transactional
    public boolean addToWishlist(Customer customer, Long productId) {
        Shop shop = Shop.findById(shopContext.getShopId());
        if (shop == null) {
            return false;
        }

        Product product = Product.findById(productId);
        if (product == null || !product.shop.id.equals(shop.id)) {
            return false;
        }

        // Check if already exists
        if (Wishlist.existsByCustomerAndProduct(customer, product)) {
            return false; // Already in wishlist
        }

        Wishlist wishlist = new Wishlist();
        wishlist.customer = customer;
        wishlist.product = product;
        wishlist.shop = shop;
        wishlist.persist();

        return true;
    }

    @Transactional
    public boolean removeFromWishlist(Customer customer, Long productId) {
        Product product = Product.findById(productId);
        if (product == null) {
            return false;
        }

        Wishlist wishlist = Wishlist.findByCustomerAndProduct(customer, product);
        if (wishlist == null) {
            return false;
        }

        wishlist.delete();
        return true;
    }

    public WishlistResponseDTO getWishlist(Customer customer, int page, int size) {
        Shop shop = Shop.findById(shopContext.getShopId());
        if (shop == null) {
            return new WishlistResponseDTO(List.of(), 0, page, size);
        }

        List<Wishlist> wishlists = Wishlist.find("customer = ?1 and shop = ?2 order by createdAt desc", customer, shop)
                .page(Page.of(page, size))
                .list();

        long totalCount = Wishlist.count("customer = ?1 and shop = ?2", customer, shop);

        List<WishlistDTO> wishlistDTOs = wishlists.stream()
                .map(wishlist -> {
                    ProductItemDTO productDTO = productService.getProductById(wishlist.product.id);
                    return WishlistDTO.fromEntity(wishlist, productDTO);
                })
                .collect(Collectors.toList());

        return new WishlistResponseDTO(wishlistDTOs, totalCount, page, size);
    }

    public boolean isInWishlist(Customer customer, Long productId) {
        Product product = Product.findById(productId);
        if (product == null) {
            return false;
        }
        return Wishlist.existsByCustomerAndProduct(customer, product);
    }

    public long getWishlistCount(Customer customer) {
        return Wishlist.countByCustomer(customer);
    }

    public List<Long> getWishlistProductIds(Customer customer) {
        Shop shop = Shop.findById(shopContext.getShopId());
        if (shop == null) {
            return List.of();
        }

        return Wishlist.find("customer = ?1 and shop = ?2", customer, shop)
                .project(Long.class)
                .list();
    }
}
