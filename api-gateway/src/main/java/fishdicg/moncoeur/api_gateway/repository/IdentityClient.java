package fishdicg.moncoeur.api_gateway.repository;

import fishdicg.moncoeur.api_gateway.dto.request.IntrospectRequest;
import fishdicg.moncoeur.api_gateway.dto.response.IntrospectResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

public interface IdentityClient {
    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request);
}
