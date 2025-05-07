package fishdicg.moncoeur.order_service.repository;

import fishdicg.moncoeur.order_service.entity.Cart;
import fishdicg.moncoeur.order_service.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByProductId(String productId);

    Page<Order> findAllByCart(Cart cart, Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "JOIN o.cart c " +
            "WHERE c.isPayed = true AND c.userId = :userId")
    Page<Order> findPaidOrdersByUserId(@Param("userId") String userId, Pageable pageable);
}
