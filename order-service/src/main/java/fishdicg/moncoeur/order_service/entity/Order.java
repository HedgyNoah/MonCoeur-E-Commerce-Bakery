package fishdicg.moncoeur.order_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import fishdicg.moncoeur.order_service.constant.DateConstant;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "ORDERS")
public class Order extends AbstractMappedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ORDER_ID", unique = true, nullable = false, updatable = false)
    String orderId;

    @DateTimeFormat(pattern = DateConstant.DATE_TIME_FORMAT)
    @Column(name = "ORDER_DATE")
    LocalDateTime orderDate;

    @Column(name = "ORDER_QUANTITY")
    Integer orderQuantity;

    @Column(name = "ORDER_FEE")
    Double orderFee;

    @Column(name = "PRODUCT_ID", nullable = false)
    String productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CART_ID")
    @JsonBackReference
    Cart cart;
}
