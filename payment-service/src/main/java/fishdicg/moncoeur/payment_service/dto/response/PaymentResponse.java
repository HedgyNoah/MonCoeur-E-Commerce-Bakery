package fishdicg.moncoeur.payment_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import fishdicg.moncoeur.payment_service.constant.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    String paymentId;
    String userId;
    String cartId;

    @JsonProperty
    boolean payed;

    PaymentStatus paymentStatus;
}
