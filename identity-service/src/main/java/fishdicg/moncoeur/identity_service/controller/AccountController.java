package fishdicg.moncoeur.identity_service.controller;

import fishdicg.moncoeur.identity_service.dto.request.SendVerificationRequest;
import fishdicg.moncoeur.identity_service.dto.request.UserCreationRequest;
import fishdicg.moncoeur.identity_service.dto.request.VerificationRequest;
import fishdicg.moncoeur.identity_service.dto.response.UserResponse;
import fishdicg.moncoeur.identity_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {
    UserService userService;

    @PostMapping("/registration")
    ResponseEntity<UserResponse> createUser(@RequestBody UserCreationRequest request) {
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.OK);
    }

    @PostMapping("/verify")
    ResponseEntity<String> verify(@RequestBody VerificationRequest request) {
        userService.verifyUser(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body("User has been verified.");
    }

    @PostMapping("/send-code")
    ResponseEntity<String> sendCode(@RequestBody SendVerificationRequest request) {
        userService.sendCode(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Verification code has been sent.");
    }
}
