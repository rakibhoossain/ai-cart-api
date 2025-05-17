package org.aicart.store.customer;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.aicart.store.customer.entity.CustomerAddress;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerAddressRepository implements PanacheRepository<CustomerAddress> {

    @Transactional
    public boolean deleteByIdAndCustomerId(Long addressId, Long customerId) {
        return delete("id = ?1 and customer.id = ?2", addressId, customerId) > 0;
    }

    public List<CustomerAddress> listByCustomerId(Long customerId) {
        return list("customer.id", customerId);
    }

    public Optional<CustomerAddress> findByIdAndCustomerId(Long id, Long customerId) {
        return find("id = ?1 and customer.id = ?2", id, customerId).firstResultOptional();
    }

    public void setPrimaryAddress(Long customerId, Long addressId) {
        update("isPrimary = false where customer.id = ?1", customerId);
        update("isPrimary = true where id = ?1 and customer.id = ?2", addressId, customerId);
    }
}
