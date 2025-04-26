package fishdicg.moncoeur.order_service.controller;

import fishdicg.moncoeur.order_service.dto.request.OrderRequest;
import fishdicg.moncoeur.order_service.dto.response.OrderResponse;
import fishdicg.moncoeur.order_service.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manage")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PostMapping
    ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    @GetMapping
    ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getAllOrder());
    }

    @GetMapping("/{id}")
    ResponseEntity<OrderResponse> getOrder(@PathVariable String id) {
        return new ResponseEntity<>(orderService
                .getOrder(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    ResponseEntity<OrderResponse> updateOrder(@PathVariable String id,
                                              @RequestBody OrderRequest request) {
        return new ResponseEntity<>(orderService
                .updateOrder(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return new ResponseEntity<>("Order has been removed.", HttpStatus.OK);
    }
}
