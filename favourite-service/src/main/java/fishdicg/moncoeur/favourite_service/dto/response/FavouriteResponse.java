package fishdicg.moncoeur.favourite_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FavouriteResponse {
    String favouriteId;
    String userId;
    String productId;
}
