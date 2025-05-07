package fishdicg.moncoeur.identity_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    String id;
    String username;
    String email;
    String phoneNumber;
    String fullName;
    String city;
    String address;
    Boolean verified;
    Set<RoleResponse> roles;
}
