package fishdicg.moncoeur.identity_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "USER")
public class User extends AbstractMappedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "USERNAME", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;

    @Column(name = "PASSWORD", nullable = false)
    String password;

    @Column(name = "EMAIL")
    String email;

    @Column(name = "PHONE_NUMBER")
    String phoneNumber;

    @Column(name = "VERIFIED", nullable = false)
    Boolean verified;

    @Column(name = "VERIFICATION_CODE")
    String verificationCode;

    @Column(name = "VERIFICATION_EXPIRY")
    LocalDateTime verificationExpiry;

    @ManyToMany(fetch = FetchType.LAZY)
    Set<Role> roles;
}
