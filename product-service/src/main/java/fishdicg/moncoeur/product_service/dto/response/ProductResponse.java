package fishdicg.moncoeur.product_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String productId;
    String productName;
    String productDescription;
    String imageName;
    Double price;
    String category;
}
