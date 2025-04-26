package fishdicg.moncoeur.favourite_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "FAVOURITE")
public class Favourite extends AbstractMappedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "FAVOURITE_ID", unique = true, nullable = false, updatable = false)
    String favouriteId;

    @Column(name = "USER_ID", nullable = false)
    String userId;

    @Column(name = "PRODUCT_ID", nullable = false)
    String productId;
}
