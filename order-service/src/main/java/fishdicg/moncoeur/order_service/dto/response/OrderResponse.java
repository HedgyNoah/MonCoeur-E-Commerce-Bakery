package fishdicg.moncoeur.order_service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import fishdicg.moncoeur.order_service.constant.DateConstant;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String orderId;

    @JsonFormat(pattern =  DateConstant.DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(pattern = DateConstant.DATE_TIME_FORMAT)
    LocalDateTime orderDate;

    Double orderFee;
    Integer orderQuantity;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String productName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    ProductResponse product;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    CartResponse cart;
}
