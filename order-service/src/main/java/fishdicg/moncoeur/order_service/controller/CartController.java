package fishdicg.moncoeur.order_service.controller;

import fishdicg.moncoeur.order_service.dto.PageResponse;
import fishdicg.moncoeur.order_service.dto.request.CartRequest;
import fishdicg.moncoeur.order_service.dto.response.CartResponse;
import fishdicg.moncoeur.order_service.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @GetMapping("/my-cart")
    ResponseEntity<CartResponse> getMyCart(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "orderDate") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order) {
        return ResponseEntity.ok(cartService.getMyCart(page, size, sortBy, order));
    }

    @PutMapping("/{id}")
    ResponseEntity<CartResponse> updateCart(@PathVariable String id,
                                            @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.updateCart(id, request));
    }

    @GetMapping
    ResponseEntity<PageResponse<CartResponse>> getAllCarts(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "totalCost") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        return ResponseEntity.ok(cartService.getAllCarts(page, size, sortBy, order, search));
    }

    @GetMapping("/{id}")
    ResponseEntity<CartResponse> getCart(
            @PathVariable String id,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "totalCost") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order) {
        return ResponseEntity.ok(cartService.getCart(id, page, size, sortBy, order));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteCart(@PathVariable String id) {
        cartService.deleteCart(id);
        return ResponseEntity.ok("Cart has been removed.");
    }
}
