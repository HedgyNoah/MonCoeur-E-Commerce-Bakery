package fishdicg.moncoeur.identity_service.dto.request;

import fishdicg.moncoeur.identity_service.validator.PhoneNumberConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileUpdateRequest {
    @PhoneNumberConstraint
    String phoneNumber;

    String fullName;
    String city;
    String address;
}
