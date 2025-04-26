package fishdicg.moncoeur.inventory_service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryResponse {
    String inventoryId;
    String productId;
    String productName;
    Integer quantity;
    Integer lowStockThreshold;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant restockDate;
}
