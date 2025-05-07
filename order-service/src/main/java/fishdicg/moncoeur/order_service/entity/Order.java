package fishdicg.moncoeur.order_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @Column(name = "ORDER_QUANTITY")
    Integer orderQuantity;

    @Column(name = "ORDER_FEE")
    Double orderFee;

    @Column(name = "IMAGE_NAME")
    String imageName;

    @Column(name = "ORDER_NAME")
    String orderName;

    @Column(name = "PRODUCT_ID", nullable = false)
    String productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CART_ID")
    @JsonBackReference
    Cart cart;
}
