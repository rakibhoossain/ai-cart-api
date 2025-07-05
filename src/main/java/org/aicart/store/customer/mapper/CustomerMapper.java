package org.aicart.store.customer.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.aicart.store.customer.dto.CustomerDTO;
import org.aicart.store.customer.dto.CustomerListDTO;
import org.aicart.store.customer.dto.CustomerDetailDTO;
import org.aicart.store.customer.entity.Customer;

@ApplicationScoped
public class CustomerMapper {

    public static CustomerDTO toDto(Customer customer) {
        if (customer == null) return null;

        CustomerDTO dto = new CustomerDTO();
        dto.id = String.valueOf(customer.id);
        dto.firstName = customer.firstName;
        dto.lastName = customer.lastName;
        dto.email = customer.email;
        dto.phone = customer.phone;
        dto.shopId = customer.shop.id;

        return dto;
    }

    public CustomerListDTO toListDTO(Customer customer) {
        if (customer == null) return null;

        return new CustomerListDTO(
            customer.id,
            customer.firstName,
            customer.lastName,
            customer.email,
            customer.phone,
            customer.company,
            customer.customerType,
            customer.customerTier,
            customer.emailVerified,
            customer.accountLocked,
            customer.totalOrders,
            customer.totalSpent,
            customer.lifetimeValue,
            customer.lastOrderAt,
            customer.lastActivityAt,
            customer.createdAt
        );
    }

    public CustomerDetailDTO toDetailDTO(Customer customer) {
        if (customer == null) return null;

        CustomerDetailDTO dto = new CustomerDetailDTO();
        dto.setId(customer.id);
        dto.setFirstName(customer.firstName);
        dto.setLastName(customer.lastName);
        dto.setEmail(customer.email);
        dto.setPhone(customer.phone);
        dto.setDateOfBirth(customer.dateOfBirth);
        dto.setGender(customer.gender);
        dto.setCompany(customer.company);
        dto.setJobTitle(customer.jobTitle);
        dto.setLanguageCode(customer.languageCode);
        dto.setCurrencyCode(customer.currencyCode);
        dto.setTimezone(customer.timezone);
        dto.setAvatarUrl(customer.avatarUrl);
        dto.setEmailVerified(customer.emailVerified);
        dto.setPhoneVerified(customer.phoneVerified);
        dto.setNewsletterSubscribe(customer.newsletterSubscribe);
        dto.setEmailSubscribe(customer.emailSubscribe);
        dto.setPhoneSubscribe(customer.phoneSubscribe);
        dto.setSmsSubscribe(customer.smsSubscribe);
        dto.setTotalOrders(customer.totalOrders);
        dto.setTotalSpent(customer.totalSpent);
        dto.setAverageOrderValue(customer.averageOrderValue);
        dto.setLifetimeValue(customer.lifetimeValue);
        dto.setFirstOrderAt(customer.firstOrderAt);
        dto.setLastOrderAt(customer.lastOrderAt);
        dto.setLastActivityAt(customer.lastActivityAt);
        dto.setCustomerType(customer.customerType);
        dto.setCustomerTier(customer.customerTier);
        // Convert tags to comma-separated string
        if (customer.tags != null && !customer.tags.isEmpty()) {
            String tagsString = customer.tags.stream()
                    .map(tag -> tag.name)
                    .collect(java.util.stream.Collectors.joining(", "));
            dto.setTags(tagsString);
        } else {
            dto.setTags("");
        }
        dto.setNotes(customer.notes);
        dto.setAccountLocked(customer.accountLocked);
        dto.setAccountLockedReason(customer.accountLockedReason);
        dto.setAccountLockedAt(customer.accountLockedAt);
        dto.setTaxExempt(customer.taxExempt);
        dto.setTaxExemptionReason(customer.taxExemptionReason);
        dto.setVatNumber(customer.vatNumber);
        dto.setTaxId(customer.taxId);
        dto.setCreatedAt(customer.createdAt);
        dto.setUpdatedAt(customer.updatedAt);

        return dto;
    }
}
