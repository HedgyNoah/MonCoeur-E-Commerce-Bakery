package fishdicg.moncoeur.inventory_service.controller;

import fishdicg.moncoeur.inventory_service.dto.PageResponse;
import fishdicg.moncoeur.inventory_service.dto.request.InventoryRequest;
import fishdicg.moncoeur.inventory_service.dto.response.InventoryResponse;
import fishdicg.moncoeur.inventory_service.service.InventoryService;
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
public class InventoryController {
    InventoryService inventoryService;

    @PostMapping
    ResponseEntity<InventoryResponse> createInventory(@RequestBody InventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.create(request));
    }

    @GetMapping
    ResponseEntity<PageResponse<InventoryResponse>> getAllInventories(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "restockDate") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(inventoryService.getAll(page, size, sortBy, order, search));
    }

    @GetMapping("/{id}")
    ResponseEntity<InventoryResponse> getInventory(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(inventoryService.getOne(id));
    }

    @PutMapping("/{productId}")
    ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable String productId,
            @RequestBody InventoryRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(inventoryService.update(productId, request));
    }

    @DeleteMapping("/{productId}")
    ResponseEntity<String> deleteInventory(@PathVariable String productId) {
        inventoryService.delete(productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Inventory deleted.");
    }
}
