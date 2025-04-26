package fishdicg.moncoeur.order_service.repository;

import fishdicg.moncoeur.order_service.entity.Cart;
import fishdicg.moncoeur.order_service.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByProductId(String productId);

    Page<Order> findAllByCart(Cart cart, Pageable pageable);
}
