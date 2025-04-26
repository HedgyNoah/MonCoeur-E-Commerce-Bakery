package fishdicg.moncoeur.order_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import fishdicg.moncoeur.order_service.dto.PageResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    String cartId;
    String userId;
    boolean payed;
    Double totalCost;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    PageResponse<OrderResponse> orders;
}
