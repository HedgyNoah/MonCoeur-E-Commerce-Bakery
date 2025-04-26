package fishdicg.moncoeur.api_gateway.service;

import fishdicg.moncoeur.api_gateway.dto.request.IntrospectRequest;
import fishdicg.moncoeur.api_gateway.dto.response.IntrospectResponse;
import fishdicg.moncoeur.api_gateway.repository.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;

    public Mono<ResponseEntity<IntrospectResponse>> introspect(String token) {
        return identityClient.introspect(IntrospectRequest.builder()
                        .token(token)
                .build());
    }
}
