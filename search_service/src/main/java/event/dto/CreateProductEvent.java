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
public class CreateProductEvent {
    String productId;
    String productName;
    String productDescription;
    Double price;
    String imageName;
    Integer quantity;
    Integer lowStockThreshold;
    String category;
}
