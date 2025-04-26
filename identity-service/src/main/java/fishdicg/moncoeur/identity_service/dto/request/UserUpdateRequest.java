package fishdicg.moncoeur.identity_service.dto.request;

import fishdicg.moncoeur.identity_service.validator.PhoneNumberConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;

    @PhoneNumberConstraint
    String phoneNumber;

    List<String> roles;
}
