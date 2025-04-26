package fishdicg.moncoeur.inventory_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryRequest {
    String productId;
    Integer quantity;
    Integer lowStockThreshold;
}
