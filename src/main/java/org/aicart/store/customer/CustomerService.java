package org.aicart.store.customer;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.aicart.store.customer.dto.*;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.customer.entity.CustomerAddress;
import org.aicart.store.customer.entity.CustomerType;
import org.aicart.store.customer.entity.CustomerTier;
import org.aicart.store.customer.mapper.CustomerMapper;
import org.aicart.store.customer.mapper.CustomerAddressMapper;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomerService {
    @Inject
    CustomerRepository customerRepository;

    @Inject
    CustomerAddressService addressService;

    @Inject
    CustomerMapper customerMapper;

    @Inject
    CustomerAddressMapper customerAddressMapper;

    // Legacy methods for backward compatibility
    public Optional<Customer> getCustomer(Long id) {
        return customerRepository.findByIdOptional(id);
    }

    public Customer getCustomerOrThrow(Long id) {
        return customerRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    @Transactional
    public Customer createCustomer(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.shop = (Shop) Shop.findByIdOptional(dto.shopId)
                .orElseThrow(() -> new NotFoundException("Shop not found"));
        updateCustomerFromDto(customer, dto);
        customerRepository.persist(customer);
        return customer;
    }

    public Customer updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = customerRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        updateCustomerFromDto(customer, dto);
        return customer;
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public Customer setPrimaryAddress(Long customerId, Long addressId) {
        Customer customer = getCustomerOrThrow(customerId);
        customer.primaryAddress = addressService.getAddressForCustomer(addressId, customerId);
        customerRepository.persist(customer);
        return customer;
    }

    private void updateCustomerFromDto(Customer customer, CustomerDTO dto) {
        customer.email = dto.email;
        customer.firstName = dto.firstName;
        customer.lastName = dto.lastName;
        customer.phone = dto.phone;

        if(dto.password != null) {
            customer.password = BcryptUtil.bcryptHash(dto.password);
        }
    }

    // New comprehensive methods

    /**
     * Get customers with filters and pagination
     */
    public CustomerListResponseDTO getCustomers(Shop shop, String search, CustomerType customerType,
                                               CustomerTier customerTier, Boolean emailVerified,
                                               Boolean accountLocked, LocalDateTime startDate,
                                               LocalDateTime endDate, String sortBy, String order,
                                               int page, int size) {

        // Get filtered customers
        var customersQuery = customerRepository.findWithFilters(shop, search, customerType,
                                                               customerTier, emailVerified,
                                                               accountLocked, startDate, endDate,
                                                               sortBy, order);

        // Apply pagination
        var customers = customersQuery.page(Page.of(page, size)).list();

        // Get total count
        long total = customerRepository.countWithFilters(shop, search, customerType,
                                                        customerTier, emailVerified,
                                                        accountLocked, startDate, endDate);

        // Convert to DTOs
        List<CustomerListDTO> customerDTOs = customers.stream()
                .map(customerMapper::toListDTO)
                .collect(Collectors.toList());

        // Get stats
        CustomerStatsDTO stats = getCustomerStats(shop);

        return new CustomerListResponseDTO(customerDTOs, total, page, size, stats);
    }

    /**
     * Get customer by ID
     */
    public CustomerDetailDTO getCustomerDetail(Shop shop, Long customerId) {
        Customer customer = customerRepository.findByIdAndShop(customerId, shop);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        CustomerDetailDTO dto = customerMapper.toDetailDTO(customer);

        // Load addresses
        List<CustomerAddressDTO> addresses = customer.addresses.stream()
                .map(customerAddressMapper::toDTO)
                .collect(Collectors.toList());
        dto.setAddresses(addresses);

        return dto;
    }

    /**
     * Create a new customer
     */
    @Transactional
    public CustomerDetailDTO createCustomer(Shop shop, CustomerCreateRequestDTO createRequest, String createdBy) {
        try {
            // Check if email already exists
            Customer existingCustomer = customerRepository.findByEmailAndShop(createRequest.getEmail(), shop);
            if (existingCustomer != null) {
                throw new RuntimeException("Customer with this email already exists");
            }

            // Create customer entity
            Customer customer = new Customer();
            customer.shop = shop;
            customer.firstName = createRequest.getFirstName();
            customer.lastName = createRequest.getLastName();
            customer.email = createRequest.getEmail();
            customer.phone = createRequest.getPhone();
            customer.dateOfBirth = createRequest.getDateOfBirth();
            customer.gender = createRequest.getGender();
            customer.company = createRequest.getCompany();
            customer.jobTitle = createRequest.getJobTitle();
            customer.languageCode = createRequest.getLanguageCode();
            customer.currencyCode = createRequest.getCurrencyCode();
            customer.timezone = createRequest.getTimezone();
            customer.avatarUrl = createRequest.getAvatarUrl();

            // Set password if provided
            if (createRequest.getPassword() != null && !createRequest.getPassword().trim().isEmpty()) {
                customer.password = BcryptUtil.bcryptHash(createRequest.getPassword());
            }

            // Set marketing preferences
            customer.newsletterSubscribe = createRequest.isNewsletterSubscribe();
            customer.emailSubscribe = createRequest.isEmailSubscribe();
            customer.phoneSubscribe = createRequest.isPhoneSubscribe();
            customer.smsSubscribe = createRequest.isSmsSubscribe();

            // Set customer classification
            customer.customerType = createRequest.getCustomerType();
            customer.customerTier = createRequest.getCustomerTier();
            customer.tags = createRequest.getTags();
            customer.notes = createRequest.getNotes();

            // Set tax information
            customer.taxExempt = createRequest.isTaxExempt();
            customer.taxExemptionReason = createRequest.getTaxExemptionReason();
            customer.vatNumber = createRequest.getVatNumber();
            customer.taxId = createRequest.getTaxId();

            // Set email verification
            customer.emailVerified = createRequest.isVerifyEmail();

            // Save customer
            customer.persist();

            // Create addresses if provided
            if (createRequest.getAddresses() != null && !createRequest.getAddresses().isEmpty()) {
                for (CustomerCreateRequestDTO.CustomerAddressCreateDTO addressRequest : createRequest.getAddresses()) {
                    CustomerAddress address = new CustomerAddress();
                    address.customer = customer;
                    address.type = addressRequest.getType();
                    address.firstName = addressRequest.getFirstName();
                    address.lastName = addressRequest.getLastName();
                    address.company = addressRequest.getCompany();
                    address.line1 = addressRequest.getLine1();
                    address.line2 = addressRequest.getLine2();
                    address.city = addressRequest.getCity();
                    address.state = addressRequest.getState();
                    address.country = addressRequest.getCountry();
                    address.postalCode = addressRequest.getPostalCode();
                    address.phone = addressRequest.getPhone();
                    address.isDefault = addressRequest.isDefault();

                    address.persist();
                }
            }

            // TODO: Send welcome email if requested
            if (createRequest.isSendWelcomeEmail()) {
                sendWelcomeEmail(customer);
            }

            return getCustomerDetail(shop, customer.id);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create customer: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing customer
     */
    @Transactional
    public CustomerDetailDTO updateCustomer(Shop shop, Long customerId, CustomerUpdateRequestDTO updateRequest, String updatedBy) {
        try {
            Customer customer = customerRepository.findByIdAndShop(customerId, shop);
            if (customer == null) {
                throw new RuntimeException("Customer not found");
            }

            // Track changes for logging
            StringBuilder changes = new StringBuilder();

            // Update basic information
            if (updateRequest.getFirstName() != null) {
                customer.firstName = updateRequest.getFirstName();
                changes.append("First name updated; ");
            }

            if (updateRequest.getLastName() != null) {
                customer.lastName = updateRequest.getLastName();
                changes.append("Last name updated; ");
            }

            if (updateRequest.getEmail() != null) {
                // Check if new email already exists
                Customer existingCustomer = customerRepository.findByEmailAndShop(updateRequest.getEmail(), shop);
                if (existingCustomer != null && !existingCustomer.id.equals(customerId)) {
                    throw new RuntimeException("Customer with this email already exists");
                }
                customer.email = updateRequest.getEmail();
                changes.append("Email updated; ");
            }

            if (updateRequest.getPhone() != null) {
                customer.phone = updateRequest.getPhone();
                changes.append("Phone updated; ");
            }

            if (updateRequest.getDateOfBirth() != null) {
                customer.dateOfBirth = updateRequest.getDateOfBirth();
                changes.append("Date of birth updated; ");
            }

            if (updateRequest.getGender() != null) {
                customer.gender = updateRequest.getGender();
                changes.append("Gender updated; ");
            }

            if (updateRequest.getCompany() != null) {
                customer.company = updateRequest.getCompany();
                changes.append("Company updated; ");
            }

            if (updateRequest.getJobTitle() != null) {
                customer.jobTitle = updateRequest.getJobTitle();
                changes.append("Job title updated; ");
            }

            if (updateRequest.getLanguageCode() != null) {
                customer.languageCode = updateRequest.getLanguageCode();
                changes.append("Language updated; ");
            }

            if (updateRequest.getCurrencyCode() != null) {
                customer.currencyCode = updateRequest.getCurrencyCode();
                changes.append("Currency updated; ");
            }

            if (updateRequest.getTimezone() != null) {
                customer.timezone = updateRequest.getTimezone();
                changes.append("Timezone updated; ");
            }

            if (updateRequest.getAvatarUrl() != null) {
                customer.avatarUrl = updateRequest.getAvatarUrl();
                changes.append("Avatar updated; ");
            }

            // Update marketing preferences
            if (updateRequest.getNewsletterSubscribe() != null) {
                customer.newsletterSubscribe = updateRequest.getNewsletterSubscribe();
                changes.append("Newsletter subscription updated; ");
            }

            if (updateRequest.getEmailSubscribe() != null) {
                customer.emailSubscribe = updateRequest.getEmailSubscribe();
                changes.append("Email subscription updated; ");
            }

            if (updateRequest.getPhoneSubscribe() != null) {
                customer.phoneSubscribe = updateRequest.getPhoneSubscribe();
                changes.append("Phone subscription updated; ");
            }

            if (updateRequest.getSmsSubscribe() != null) {
                customer.smsSubscribe = updateRequest.getSmsSubscribe();
                changes.append("SMS subscription updated; ");
            }

            // Update customer classification
            if (updateRequest.getCustomerType() != null) {
                customer.customerType = updateRequest.getCustomerType();
                changes.append("Customer type updated; ");
            }

            if (updateRequest.getCustomerTier() != null) {
                customer.customerTier = updateRequest.getCustomerTier();
                changes.append("Customer tier updated; ");
            }

            if (updateRequest.getTags() != null) {
                customer.tags = updateRequest.getTags();
                changes.append("Tags updated; ");
            }

            if (updateRequest.getNotes() != null) {
                customer.notes = updateRequest.getNotes();
                changes.append("Notes updated; ");
            }

            // Update account status
            if (updateRequest.getAccountLocked() != null) {
                customer.accountLocked = updateRequest.getAccountLocked();
                if (updateRequest.getAccountLocked()) {
                    customer.accountLockedReason = updateRequest.getAccountLockedReason();
                    customer.accountLockedAt = LocalDateTime.now();
                } else {
                    customer.accountLockedReason = null;
                    customer.accountLockedAt = null;
                }
                changes.append("Account lock status updated; ");
            }

            // Update tax information
            if (updateRequest.getTaxExempt() != null) {
                customer.taxExempt = updateRequest.getTaxExempt();
                customer.taxExemptionReason = updateRequest.getTaxExemptionReason();
                changes.append("Tax exemption updated; ");
            }

            if (updateRequest.getVatNumber() != null) {
                customer.vatNumber = updateRequest.getVatNumber();
                changes.append("VAT number updated; ");
            }

            if (updateRequest.getTaxId() != null) {
                customer.taxId = updateRequest.getTaxId();
                changes.append("Tax ID updated; ");
            }

            // Update last activity
            customer.lastActivityAt = LocalDateTime.now();

            // Save customer
            customer.persist();

            // TODO: Log changes
            if (changes.length() > 0) {
                logCustomerUpdate(customer, changes.toString(), updatedBy);
            }

            // TODO: Notify customer if requested
            if (updateRequest.getNotifyCustomer() != null && updateRequest.getNotifyCustomer()) {
                notifyCustomerOfUpdate(customer, updateRequest.getUpdateReason());
            }

            return getCustomerDetail(shop, customerId);

        } catch (Exception e) {
            throw new RuntimeException("Failed to update customer: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a customer (soft delete)
     */
    @Transactional
    public void deleteCustomer(Shop shop, Long customerId, String deletedBy) {
        Customer customer = customerRepository.findByIdAndShop(customerId, shop);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        // Check if customer has orders
        // TODO: Check for existing orders and handle accordingly

        // Soft delete by marking as deleted
        customer.accountLocked = true;
        customer.accountLockedReason = "Account deleted";
        customer.accountLockedAt = LocalDateTime.now();

        // Anonymize email to prevent conflicts
        customer.email = "deleted_" + customer.id + "@deleted.com";

        customer.persist();

        // TODO: Log deletion
        logCustomerDeletion(customer, deletedBy);
    }

    /**
     * Get customer statistics
     */
    public CustomerStatsDTO getCustomerStats(Shop shop) {
        long totalCustomers = customerRepository.countByShop(shop);
        long verifiedCustomers = Customer.count("shop = ?1 and emailVerified = true", shop);
        long vipCustomers = Customer.count("shop = ?1 and customerType = ?2", shop, CustomerType.VIP);
        long lockedCustomers = Customer.count("shop = ?1 and accountLocked = true", shop);

        // New customers this month
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long newCustomersThisMonth = customerRepository.countNewCustomers(shop, startOfMonth, LocalDateTime.now());

        // Active customers (ordered in last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long activeCustomers = Customer.count("shop = ?1 and lastOrderAt >= ?2", shop, thirtyDaysAgo);
        long inactiveCustomers = totalCustomers - activeCustomers;

        // Calculate averages (simplified approach)
        Double avgLifetimeValue = 0.0;
        Double avgOrderValue = 0.0;
        Long totalRevenue = 0L;

        try {
            // Use native queries for aggregations
            avgLifetimeValue = (Double) Customer.getEntityManager()
                .createQuery("SELECT AVG(c.lifetimeValue) FROM Customer c WHERE c.shop = :shop")
                .setParameter("shop", shop)
                .getSingleResult();

            avgOrderValue = (Double) Customer.getEntityManager()
                .createQuery("SELECT AVG(c.averageOrderValue) FROM Customer c WHERE c.shop = :shop")
                .setParameter("shop", shop)
                .getSingleResult();

            totalRevenue = (Long) Customer.getEntityManager()
                .createQuery("SELECT SUM(c.totalSpent) FROM Customer c WHERE c.shop = :shop")
                .setParameter("shop", shop)
                .getSingleResult();
        } catch (Exception e) {
            // Handle null results
            avgLifetimeValue = avgLifetimeValue != null ? avgLifetimeValue : 0.0;
            avgOrderValue = avgOrderValue != null ? avgOrderValue : 0.0;
            totalRevenue = totalRevenue != null ? totalRevenue : 0L;
        }

        return new CustomerStatsDTO(
            totalCustomers,
            verifiedCustomers,
            vipCustomers,
            lockedCustomers,
            newCustomersThisMonth,
            activeCustomers,
            inactiveCustomers,
            avgLifetimeValue != null ? avgLifetimeValue.longValue() : 0L,
            avgOrderValue != null ? avgOrderValue : 0.0,
            totalRevenue != null ? totalRevenue : 0L
        );
    }

    // Helper methods
    private void sendWelcomeEmail(Customer customer) {
        // TODO: Implement welcome email sending
    }

    private void logCustomerUpdate(Customer customer, String changes, String updatedBy) {
        // TODO: Implement customer update logging
    }

    private void notifyCustomerOfUpdate(Customer customer, String reason) {
        // TODO: Implement customer notification
    }

    private void logCustomerDeletion(Customer customer, String deletedBy) {
        // TODO: Implement customer deletion logging
    }
}
