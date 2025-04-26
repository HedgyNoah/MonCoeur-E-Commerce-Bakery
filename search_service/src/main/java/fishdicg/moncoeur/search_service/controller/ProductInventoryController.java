package fishdicg.moncoeur.search_service.controller;

import fishdicg.moncoeur.search_service.dto.PageResponse;
import fishdicg.moncoeur.search_service.dto.ProductInventoryResponse;
import fishdicg.moncoeur.search_service.service.ElasticSearchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/result")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductInventoryController {
    ElasticSearchService elasticSearchService;

    @GetMapping
    ResponseEntity<PageResponse<ProductInventoryResponse>> search(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "category", required = false, defaultValue = "") String category,
            @RequestParam(value = "sortBy", required = false, defaultValue = "productName") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        return ResponseEntity.ok().body(
                elasticSearchService.search(page, size, category, sortBy, order, search));
    }

    @GetMapping("/{id}")
    ResponseEntity<ProductInventoryResponse> getOne(@PathVariable String id) {
        return ResponseEntity.ok().body(
                elasticSearchService.getOne(id));
    }
}   
