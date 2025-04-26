package fishdicg.moncoeur.identity_service.controller;

import fishdicg.moncoeur.identity_service.dto.PageResponse;
import fishdicg.moncoeur.identity_service.dto.request.UserUpdateRequest;
import fishdicg.moncoeur.identity_service.dto.response.UserResponse;
import fishdicg.moncoeur.identity_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping("/all")
    ResponseEntity<List<UserResponse>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping
    ResponseEntity<PageResponse<UserResponse>> getAllUsersWithPagination(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "username") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "search", required = false, defaultValue = "") String search
    ) {
        return new ResponseEntity<>(userService
                .getAllUsersWithPagination(page, size, sortBy, order, search), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    ResponseEntity<UserResponse> updateUser(
            @PathVariable String id, @RequestBody UserUpdateRequest request) {
        return new ResponseEntity<>(userService.updateUser(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("User deleted.", HttpStatus.OK);
    }

    @GetMapping("/myInfo")
    ResponseEntity<UserResponse> getMyInfo() {
        return new ResponseEntity<>(userService.getMyInfo(), HttpStatus.OK);
    }

    @PutMapping("/myInfo")
    ResponseEntity<UserResponse> updateMyInfo(@RequestBody UserUpdateRequest request) {
        return new ResponseEntity<>(userService.updateMyInfo(request), HttpStatus.OK);
    }
}
