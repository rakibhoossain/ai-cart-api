package store.aicart.order;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import store.aicart.order.entity.Cart;
import store.aicart.order.entity.Order;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order>  {

    @PersistenceContext
    EntityManager em;










}
