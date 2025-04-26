package fishdicg.moncoeur.identity_service.controller;

import fishdicg.moncoeur.identity_service.dto.request.RoleRequest;
import fishdicg.moncoeur.identity_service.dto.response.RoleResponse;
import fishdicg.moncoeur.identity_service.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest request) {
        return new ResponseEntity<>(roleService.create(request), HttpStatus.CREATED);
    }

    @GetMapping()
    ResponseEntity<List<RoleResponse>> getRole() {
        return new ResponseEntity<>(roleService.getAll(), HttpStatus.OK);
    }

    @DeleteMapping("/{name}")
    ResponseEntity<String> deleteRole(@PathVariable String name) {
        roleService.delete(name);
        return new ResponseEntity<>("Role deleted.", HttpStatus.OK);
    }
}
