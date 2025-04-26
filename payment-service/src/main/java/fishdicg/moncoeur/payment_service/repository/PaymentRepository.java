package fishdicg.moncoeur.payment_service.repository;

import fishdicg.moncoeur.payment_service.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findAllByUserId(String userId);

    Page<Payment> findAll(Pageable pageable);

    Page<Payment> findByUserIdContaining(String userId, Pageable pageable);
}
