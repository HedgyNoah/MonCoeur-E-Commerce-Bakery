package fishdicg.moncoeur.product_service.controller;

import fishdicg.moncoeur.product_service.dto.PageResponse;
import fishdicg.moncoeur.product_service.dto.request.CategoryRequest;
import fishdicg.moncoeur.product_service.dto.response.CategoryResponse;
import fishdicg.moncoeur.product_service.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        return new ResponseEntity<>
                (categoryService.createCategory(request), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return new ResponseEntity<>
                (categoryService.getAllCategories(), HttpStatus.OK);
    }

    @GetMapping
    ResponseEntity<PageResponse<CategoryResponse>> getAllCategoriesWithPagination(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "categoryTitle") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        return new ResponseEntity<>
                (categoryService.getAllCategoriesWithPagination(page, size, sortBy, order, search), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<CategoryResponse> getCategory(@PathVariable String id) {
        return new ResponseEntity<>
                (categoryService.getCategory(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable String id, @RequestBody CategoryRequest request) {
        return new ResponseEntity<>
                (categoryService.updateCategory(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>("Category deleted.", HttpStatus.OK);
    }
}
