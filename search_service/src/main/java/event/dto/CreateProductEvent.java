package event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

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
    String slug;
    Double price;
    String imageName;
    Integer quantity;
    Integer lowStockThreshold;
    String category;
    Instant createdAt;
    Instant updatedAt;
}
