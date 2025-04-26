package fishdicg.moncoeur.identity_service.mapper;

import fishdicg.moncoeur.identity_service.dto.request.RoleRequest;
import fishdicg.moncoeur.identity_service.dto.response.RoleResponse;
import fishdicg.moncoeur.identity_service.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
