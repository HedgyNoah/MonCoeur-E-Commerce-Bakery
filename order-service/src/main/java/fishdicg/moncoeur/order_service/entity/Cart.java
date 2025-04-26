package fishdicg.moncoeur.order_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "CART")
public class Cart extends AbstractMappedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "CART_ID", unique = true, nullable = false, updatable = false)
    String cartId;

    @Column(name = "USER_ID")
    String userId;

    @Column(name ="IS_PAYED")
    boolean payed;

    @Column(name = "TOTAL_COST")
    Double totalCost;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    Set<Order> orders;
}
