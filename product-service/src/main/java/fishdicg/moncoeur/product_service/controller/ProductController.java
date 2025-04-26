package fishdicg.moncoeur.product_service.controller;

import fishdicg.moncoeur.product_service.dto.PageResponse;
import fishdicg.moncoeur.product_service.dto.request.ProductRequest;
import fishdicg.moncoeur.product_service.dto.response.ProductResponse;
import fishdicg.moncoeur.product_service.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping
    ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        return new ResponseEntity<>(productService.createProduct(request), HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "productName") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.getAllProducts(page, size, sortBy, order, search));
    }

    @GetMapping("/test")
    ResponseEntity<PageResponse<ProductResponse>> getAllProductsByCategory(
            @RequestParam(value = "category", required = false, defaultValue = "") String category,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "productName") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.getAllProductsByCategory(page, size, category, sortBy, order, search));
    }

    @GetMapping("/{id}")
    ResponseEntity<ProductResponse> getProduct(@PathVariable String id) {
        return new ResponseEntity<>(productService.getProduct(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @RequestBody ProductRequest request) {
        return new ResponseEntity<>(productService.updateProduct(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>("Product deleted", HttpStatus.OK);
    }
}
