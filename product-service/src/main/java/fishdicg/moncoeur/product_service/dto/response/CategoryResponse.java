package fishdicg.moncoeur.product_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    String categoryId;
    String categoryTitle;
    Integer totalProducts;
    Instant createdAt;
    Instant updatedAt;
}
