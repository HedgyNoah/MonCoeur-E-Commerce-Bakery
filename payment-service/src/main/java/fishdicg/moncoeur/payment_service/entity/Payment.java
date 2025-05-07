package fishdicg.moncoeur.payment_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(name = "CART_ID", nullable = false, unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String cartId;

    @Column(name = "USER_ID", nullable = false)
    String userId;

    @Builder.Default
    @Column(name = "FULL_NAME")
    String fullName = "";

    @Builder.Default
    @Column(name = "PHONE_NUMBER")
    String phoneNumber = "";

    @Builder.Default
    @Column(name = "CITY")
    String city = "";

    @Builder.Default
    @Column(name = "ADDRESS")
    String address = "";

    @Column(name = "IS_PAYED")
    boolean payed;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_STATUS")
    PaymentStatus paymentStatus;
}
