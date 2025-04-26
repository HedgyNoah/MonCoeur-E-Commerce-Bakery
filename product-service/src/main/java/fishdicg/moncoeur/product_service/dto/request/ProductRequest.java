package fishdicg.moncoeur.product_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    String productName;
    String productDescription;
    Double price;
    String category;
    Integer quantity;
    Integer lowStockThreshold;
}
