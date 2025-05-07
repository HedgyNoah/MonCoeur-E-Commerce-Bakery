package fishdicg.moncoeur.payment_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fishdicg.moncoeur.payment_service.constant.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
    String paymentId;
    String userId;
    String cartId;
    String fullName;
    String phoneNumber;
    String city;
    String address;
    Instant updatedAt;

    @JsonProperty
    boolean payed;

    PaymentStatus paymentStatus;
}
