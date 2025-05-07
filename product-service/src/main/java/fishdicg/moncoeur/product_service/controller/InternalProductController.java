package fishdicg.moncoeur.product_service.controller;

import fishdicg.moncoeur.product_service.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InternalProductController {
    ProductService productService;

    @GetMapping("/price/{id}")
    ResponseEntity<Double> getPrice(@PathVariable String id) {
        return ResponseEntity.ok(productService.getPrice(id));
    }

    @GetMapping("/name/{id}")
    ResponseEntity<String> getName(@PathVariable String id) {
        return ResponseEntity.ok(productService.getName(id));
    }

    @GetMapping("/image/{id}")
    ResponseEntity<String> getImageName(@PathVariable String id) {
        return  ResponseEntity.ok(productService.getImageName(id));
    }
}
