package fishdicg.moncoeur.payment_service.entity;

import fishdicg.moncoeur.payment_service.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "PAYMENT")
public class Payment extends AbstractMappedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PAYMENT_ID", unique = true, updatable = false, nullable = false)
    String paymentId;

    @Column(name = "CART_ID", nullable = false)
    String cartId;

    @Column(name = "USER_ID", nullable = false)
    String userId;

    @Column(name = "IS_PAYED")
    boolean payed;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_STATUS")
    PaymentStatus paymentStatus;
}
