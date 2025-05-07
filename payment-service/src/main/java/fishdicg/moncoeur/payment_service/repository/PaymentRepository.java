package fishdicg.moncoeur.payment_service.repository;

import fishdicg.moncoeur.payment_service.constant.PaymentStatus;
import fishdicg.moncoeur.payment_service.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findAllByUserId(String userId);

    Page<Payment> findAll(Pageable pageable);

    Optional<Payment> findByCartId(String cartId);

    Page<Payment> findByUserIdContaining(String userId, Pageable pageable);

    Page<Payment> findByUserIdAndPaymentStatusAndPaymentIdContaining(
            String userId,
            PaymentStatus paymentStatus,
            String paymentId,
            Pageable pageable
    );

    Page<Payment> findByUserIdAndPaymentStatus(String userId, PaymentStatus paymentStatus, Pageable pageable);
}
