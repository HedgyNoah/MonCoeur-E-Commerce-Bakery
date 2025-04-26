package fishdicg.moncoeur.search_service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInventoryResponse {
    String productId;
    String productName;
    String productDescription;
    String imageName;
    Double price;
    Integer quantity;
    Integer lowStockThreshold;
    String category;
}
