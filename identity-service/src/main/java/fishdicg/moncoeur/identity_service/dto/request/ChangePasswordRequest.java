package fishdicg.moncoeur.identity_service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {
    String oldPassword;

    @Size(min = 8, message = "PASSWORD_INVALID")
    String newPassword;
}
