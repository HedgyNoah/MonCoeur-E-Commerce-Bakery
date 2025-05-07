package fishdicg.moncoeur.inventory_service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "INVENTORY")
public class Inventory extends AbstractMappedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "INVENTORY_ID", unique = true, nullable = false, updatable = false)
    String inventoryId;

    @Column(name = "PRODUCT_ID", nullable = false)
    String productId;

    @Column(name = "PRODUCT_QUANTITY", nullable = false)
    Integer quantity;

    @Builder.Default
    @Column(name = "SOLD_QUANTITY", nullable = false)
    Integer sold = 0;

    @Column(name = "LOW_STOCK_THRESHOLD", nullable = false)
    Integer lowStockThreshold;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "RESTOCK_DATE", nullable = false)
    Instant restockDate;
}
