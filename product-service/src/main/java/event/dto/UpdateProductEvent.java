package event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductEvent {
    String productId;
    String productName;
    String productDescription;
    Double price;
    Integer quantity;
    Integer lowStockThreshold;
    String category;
}
