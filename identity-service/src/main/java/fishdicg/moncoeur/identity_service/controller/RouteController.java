package fishdicg.moncoeur.identity_service.controller;

import fishdicg.moncoeur.identity_service.dto.response.NavigationResponse;
import fishdicg.moncoeur.identity_service.service.RouteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RouteController {
    RouteService routeService;

    @GetMapping
    ResponseEntity<List<NavigationResponse>> getRoutes() {
        return ResponseEntity.ok(routeService.getRoutes());
    }
}
