package fishdicg.moncoeur.order_service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String orderId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String productId;

    Double orderFee;
    Integer orderQuantity;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String imageName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String orderName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    ProductResponse product;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    CartResponse cart;
}
