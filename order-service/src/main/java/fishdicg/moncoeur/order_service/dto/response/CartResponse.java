package fishdicg.moncoeur.order_service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import fishdicg.moncoeur.order_service.dto.PageResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    PageResponse<OrderResponse> orders;
}
