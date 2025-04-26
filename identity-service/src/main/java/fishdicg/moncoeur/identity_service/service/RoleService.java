package fishdicg.moncoeur.identity_service.service;

import fishdicg.moncoeur.identity_service.dto.request.RoleRequest;
import fishdicg.moncoeur.identity_service.dto.response.RoleResponse;
import fishdicg.moncoeur.identity_service.mapper.RoleMapper;
import fishdicg.moncoeur.identity_service.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    public void delete(String name) {
        roleRepository.deleteById(name);
    }
}
