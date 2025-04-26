package fishdicg.moncoeur.identity_service.dto.request;

import fishdicg.moncoeur.identity_service.validator.PhoneNumberConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;

    @Email(message = "EMAIL_INVALID")
    String email;

    @PhoneNumberConstraint
    String phoneNumber;
}
