package fishdicg.moncoeur.product_service.service;

import fishdicg.moncoeur.product_service.dto.PageResponse;
import fishdicg.moncoeur.product_service.dto.request.CategoryRequest;
import fishdicg.moncoeur.product_service.dto.response.CategoryResponse;
import fishdicg.moncoeur.product_service.dto.response.ProductResponse;
import fishdicg.moncoeur.product_service.entity.Category;
import fishdicg.moncoeur.product_service.exception.AppException;
import fishdicg.moncoeur.product_service.exception.ErrorCode;
import fishdicg.moncoeur.product_service.mapper.CategoryMapper;
import fishdicg.moncoeur.product_service.repository.CategoryRepository;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = categoryMapper.toCategory(request);
        category = categoryRepository.save(category);

        var categoryResponse = categoryMapper.toCategoryResponse(category);
        categoryResponse.setTotalProducts(category.getProducts() == null ? 0
                : category.getProducts().size());

        return categoryResponse;
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> {
                    var categoryResponse = categoryMapper.toCategoryResponse(category);
                    categoryResponse.setTotalProducts(category.getProducts() == null ? 0
                            : category.getProducts().size());
                    return categoryResponse;
                }).toList();
    }

    public PageResponse<CategoryResponse> getAllCategoriesWithPagination(int page, int size, String sortBy,
                                                                         String order, String search) {
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));

        Page<Category> pageData;
        if (search != null && !search.isBlank()) {
            pageData = categoryRepository
                    .findByCategoryTitleContainingIgnoreCase(search, pageable);
        } else {
            pageData = categoryRepository.findAll(pageable);
        }
        List<CategoryResponse> categoryResponseList = pageData.getContent().stream()
                .map(category -> {
                    CategoryResponse categoryResponse = categoryMapper.toCategoryResponse(category);
                    categoryResponse.setTotalProducts(category.getProducts() == null ? 0
                            : category.getProducts().size());
                    return categoryResponse;
                }).toList();
        return PageResponse.<CategoryResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPage(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .sortBy(sortBy)
                .order(order)
                .search(search)
                .data(categoryResponseList)
                .build();
    }

    public CategoryResponse getCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        var categoryResponse = categoryMapper.toCategoryResponse(category);

        categoryResponse.setTotalProducts(category.getProducts() == null ? 0
                : category.getProducts().size());
        return categoryResponse;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(String categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        categoryMapper.updateCategory(category, request);

        var categoryResponse = categoryMapper.toCategoryResponse(category);

        categoryResponse.setTotalProducts(category.getProducts() == null ? 0
                : category.getProducts().size());
        return categoryResponse;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        category.getProducts().size();
        categoryRepository.delete(category);
    }
}
