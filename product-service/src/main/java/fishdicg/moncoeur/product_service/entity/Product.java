package fishdicg.moncoeur.product_service.entity;

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
@Table(name = "PRODUCT")
public class Product extends AbstractMappedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PRODUCT_ID", unique = true, nullable = false, updatable = false)
    String productId;

    @Column(name = "PRODUCT_NAME")
    String productName;

    @Column(name = "PRODUCT_DESCRIPTION")
    String productDescription;

    @Column(name = "IMAGE_NAME")
    String imageName;

    @Column(name = "SLUG")
    String slug;

    @Column(name = "PRICE")
    Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    @JsonBackReference
    Category category;
}
