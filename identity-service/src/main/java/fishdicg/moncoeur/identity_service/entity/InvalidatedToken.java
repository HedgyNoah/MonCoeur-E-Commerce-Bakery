package fishdicg.moncoeur.identity_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "INVALIDATED_TOKEN")
public class InvalidatedToken {
    @Id
    @Column(name = "TOKEN_ID")
    String id;

    @Column(name = "EXPIRY_TIME")
    Date expiryTime;
}
