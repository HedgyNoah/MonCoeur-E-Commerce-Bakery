package fishdicg.moncoeur.payment_service.controller;

import fishdicg.moncoeur.payment_service.constant.PaymentStatus;
import fishdicg.moncoeur.payment_service.dto.PageResponse;
import fishdicg.moncoeur.payment_service.dto.request.PaymentRequest;
import fishdicg.moncoeur.payment_service.dto.request.ShippingUpdateRequest;
import fishdicg.moncoeur.payment_service.dto.response.PaymentResponse;
import fishdicg.moncoeur.payment_service.service.PaymentService;
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
public class PaymentController {
    PaymentService paymentService;

    @PostMapping
    ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.create(request));
    }

    @GetMapping
    ResponseEntity<PageResponse<PaymentResponse>> getAllPayments(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "paymentId") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.getAll(page, size, sortBy, order, search));
    }

    @GetMapping("/shipping")
    ResponseEntity<PageResponse<PaymentResponse>> getMyPayments(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "search", required = false, defaultValue = "") String search
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.getMyShipping(page, size, sortBy, order, search));
    }

    @GetMapping("/{id}")
    ResponseEntity<PaymentResponse> getPayment(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.getOne(id));
    }

    @PutMapping("/shipping/{id}")
    ResponseEntity<PaymentResponse> updatePayment(@PathVariable String id,
                                                  @RequestBody ShippingUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.updateShipping(id, request));
    }

    @PutMapping("/update/{id}")
    ResponseEntity<PaymentResponse> updatePayment(@PathVariable String id,
                                                  @RequestBody PaymentStatus paymentStatus) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.update(id, paymentStatus));
    }

    @PutMapping("/complete/{id}")
    ResponseEntity<PaymentResponse> completePayment(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.complete(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deletePayment(@PathVariable String id) {
        paymentService.delete(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Payment deleted");
    }
}
