package fishdicg.moncoeur.order_service.repository;

import fishdicg.moncoeur.order_service.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, String> {
    Cart findByUserIdAndIsPayedFalse(String userId);

    Page<Cart> findAll(Pageable pageable);

    Page<Cart> findAllByUserIdContaining(String userId, Pageable pageable);
}
