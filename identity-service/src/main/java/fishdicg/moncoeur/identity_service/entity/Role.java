package fishdicg.moncoeur.identity_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "ROLE")
public class Role extends AbstractMappedEntity {
    @Id
    @Column(name = "ROLE_NAME", nullable = false, unique = true)
    String name;

    @Column(name = "ROLE_DESCRIPTION")
    String description;
}
