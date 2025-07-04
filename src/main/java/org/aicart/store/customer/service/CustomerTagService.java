package org.aicart.store.customer.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.aicart.store.customer.dto.CustomerTagDTO;
import org.aicart.store.customer.entity.CustomerTag;
import org.aicart.store.customer.mapper.CustomerTagMapper;
import org.aicart.store.customer.repository.CustomerTagRepository;
import org.aicart.store.user.entity.Shop;
import org.aicart.util.SlugGenerator;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerTagService {

    @Inject
    CustomerTagRepository customerTagRepository;

    @Inject
    SlugGenerator slugGenerator;

    @Inject
    EntityManager em;

    public List<CustomerTagDTO> findByShop(Shop shop, int page, int size, String sortField, boolean ascending, String searchQuery) {
        List<CustomerTag> tags = customerTagRepository.findByShop(shop, page, size, sortField, ascending, searchQuery);
        return CustomerTagMapper.toDtoList(tags);
    }

    public long countByShop(Shop shop, String searchQuery) {
        return customerTagRepository.countByShop(shop, searchQuery);
    }

    public List<CustomerTagDTO> findAllByShop(Shop shop) {
        List<CustomerTag> tags = customerTagRepository.findAllByShop(shop);
        return CustomerTagMapper.toDtoList(tags);
    }

    public List<CustomerTagDTO> findAllByShopWithCounts(Shop shop) {
        List<CustomerTag> tags = customerTagRepository.findByShopWithCustomerCounts(shop);
        return tags.stream()
                .map(tag -> {
                    CustomerTagDTO dto = CustomerTagMapper.toDto(tag);
                    dto.setCustomerCount((long) tag.customers.size());
                    return dto;
                })
                .filter(dto -> dto.getCustomerCount() > 0) // Only show tags with customers
                .collect(java.util.stream.Collectors.toList());
    }

    public Optional<CustomerTagDTO> findById(Long id, Shop shop) {
        Optional<CustomerTag> tag = customerTagRepository.findByIdOptional(id);
        if (tag.isPresent() && tag.get().shop.id.equals(shop.id)) {
            return Optional.of(CustomerTagMapper.toDto(tag.get()));
        }
        return Optional.empty();
    }

    public Optional<CustomerTagDTO> findByName(String name, Shop shop) {
        Optional<CustomerTag> tag = customerTagRepository.findByName(name, shop);
        return tag.map(CustomerTagMapper::toDto);
    }

    public Optional<CustomerTagDTO> findBySlug(String slug, Shop shop) {
        Optional<CustomerTag> tag = customerTagRepository.findBySlug(slug, shop);
        return tag.map(CustomerTagMapper::toDto);
    }

    @Transactional
    public CustomerTagDTO create(CustomerTagDTO dto, Shop shop) {
        // Validate name uniqueness within shop
        if (customerTagRepository.existsByNameAndShop(dto.getName(), shop)) {
            throw new IllegalArgumentException("Tag with this name already exists");
        }

        CustomerTag tag = new CustomerTag();
        tag.name = dto.getName();
        tag.slug = slugGenerator.generateSlug(dto.getName());
        tag.color = dto.getColor();
        tag.description = dto.getDescription();
        tag.shop = shop;

        // Check slug uniqueness
        if (customerTagRepository.existsBySlugAndShop(tag.slug, shop)) {
            tag.slug = tag.slug + "-" + System.currentTimeMillis();
        }

        customerTagRepository.persist(tag);
        return CustomerTagMapper.toDto(tag);
    }

    @Transactional
    public CustomerTagDTO update(Long id, CustomerTagDTO dto, Shop shop) {
        CustomerTag tag = customerTagRepository.findById(id);
        if (tag == null) {
            throw new NotFoundException("Tag not found with id: " + id);
        }

        // Verify shop ownership
        if (!tag.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to update this tag");
        }

        // Check name uniqueness if changed
        if (!tag.name.equals(dto.getName()) && 
            customerTagRepository.existsByNameAndShopExcludingId(dto.getName(), shop, id)) {
            throw new IllegalArgumentException("Tag with this name already exists");
        }

        tag.name = dto.getName();
        tag.color = dto.getColor();
        tag.description = dto.getDescription();

        // Update slug if name changed
        if (!tag.name.equals(dto.getName())) {
            String newSlug = slugGenerator.generateSlug(dto.getName());
            
            // Check slug uniqueness
            if (customerTagRepository.existsBySlugAndShopExcludingId(newSlug, shop, id)) {
                newSlug = newSlug + "-" + System.currentTimeMillis();
            }
            
            tag.slug = newSlug;
        }

        customerTagRepository.persist(tag);
        return CustomerTagMapper.toDto(tag);
    }

    @Transactional
    public void delete(Long id, Shop shop) {
        CustomerTag tag = customerTagRepository.findById(id);
        if (tag == null) {
            throw new NotFoundException("Tag not found with id: " + id);
        }

        // Verify shop ownership
        if (!tag.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to delete this tag");
        }

        // Remove tag from all customers
        tag.customers.clear();
        
        customerTagRepository.delete(tag);
    }

    public long countCustomersWithTag(Long tagId, Shop shop) {
        CustomerTag tag = customerTagRepository.findById(tagId);
        if (tag == null || !tag.shop.id.equals(shop.id)) {
            return 0;
        }
        return customerTagRepository.countCustomersWithTag(tag);
    }

    @Transactional
    public CustomerTag findOrCreateTag(String tagName, Shop shop) {
        Optional<CustomerTag> existingTag = customerTagRepository.findByName(tagName, shop);
        if (existingTag.isPresent()) {
            return existingTag.get();
        }

        // Create new tag
        CustomerTag newTag = new CustomerTag();
        newTag.name = tagName;
        newTag.slug = slugGenerator.generateSlug(tagName);
        newTag.shop = shop;
        
        // Ensure unique slug
        if (customerTagRepository.existsBySlugAndShop(newTag.slug, shop)) {
            newTag.slug = newTag.slug + "-" + System.currentTimeMillis();
        }

        customerTagRepository.persist(newTag);
        return newTag;
    }
}
