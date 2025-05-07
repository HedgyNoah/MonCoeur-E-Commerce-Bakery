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
public class UpdateProductEvent {
    String productId;
    String productName;
    String productDescription;
    String slug;
    Double price;
    Integer quantity;
    Integer lowStockThreshold;
    String category;
    Instant createdAt;
    Instant updatedAt;
}
