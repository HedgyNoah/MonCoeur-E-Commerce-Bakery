package fishdicg.moncoeur.identity_service.controller;

import com.nimbusds.jose.JOSEException;
import fishdicg.moncoeur.identity_service.dto.request.authentication.AuthenticationRequest;
import fishdicg.moncoeur.identity_service.dto.request.authentication.IntrospectRequest;
import fishdicg.moncoeur.identity_service.dto.request.authentication.LogoutRequest;
import fishdicg.moncoeur.identity_service.dto.response.AuthenticationResponse;
import fishdicg.moncoeur.identity_service.dto.response.IntrospectResponse;
import fishdicg.moncoeur.identity_service.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return new ResponseEntity<>(authenticationService.authenticate(request), HttpStatus.OK);
    }

    @PostMapping("/logout")
    ResponseEntity<String> logout(@RequestBody LogoutRequest request) {
        authenticationService.logout(request);
        return new ResponseEntity<>("Logged out successfully.", HttpStatus.OK);
    }

    @PostMapping("/introspect")
    ResponseEntity<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
        throws JOSEException, ParseException {
        return new ResponseEntity<>(authenticationService.introspect(request), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody String token) {
        return new ResponseEntity<>(authenticationService
                .refreshAccessToken(token), HttpStatus.OK);
    }
}