package org.aicart.store.customer;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.aicart.store.customer.entity.Customer;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {
    public Optional<Customer> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public List<Customer> search(String query) {
        return find("lower(firstName) like lower(?1) or lower(lastName) like lower(?1)", "%" + query + "%").list();
    }
}
