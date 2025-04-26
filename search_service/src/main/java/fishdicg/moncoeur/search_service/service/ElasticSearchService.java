package fishdicg.moncoeur.search_service.service;

import fishdicg.moncoeur.search_service.dto.PageResponse;
import fishdicg.moncoeur.search_service.dto.ProductInventoryResponse;
import fishdicg.moncoeur.search_service.entity.ProductInventoryDocument;
import fishdicg.moncoeur.search_service.exception.AppException;
import fishdicg.moncoeur.search_service.exception.ErrorCode;
import fishdicg.moncoeur.search_service.mapper.ProductInventoryMapper;
import fishdicg.moncoeur.search_service.repository.ProductInventoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ElasticSearchService {
    ProductInventoryRepository productInventoryRepository;
    ProductInventoryMapper productInventoryMapper;

    public ProductInventoryResponse getOne(String id) {
        ProductInventoryDocument productInventoryDocument = productInventoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        return  productInventoryMapper.toProductInventoryResponse(productInventoryDocument);
    }

    public PageResponse<ProductInventoryResponse> search(int page, int size, String category,
                                                                           String sortBy, String order, String search) {
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;

        if ("productName".equals(sortBy) || "category".equals(sortBy)) {
            sortBy = sortBy + ".keyword";
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));

        Page<ProductInventoryDocument> pageData = fetchData(category, search, pageable);

        List<ProductInventoryResponse> productInventoryResponseList = pageData.getContent()
                .stream().map(productInventoryMapper::toProductInventoryResponse).toList();

        return PageResponse.<ProductInventoryResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPage(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .sortBy(sortBy)
                .order(order)
                .search(search)
                .data(productInventoryResponseList)
                .build();
    }

    private Page<ProductInventoryDocument> fetchData(String category, String search, Pageable pageable) {
        boolean hasSearch = search != null && !search.isBlank();

        if (hasSearch && category != null && !category.isBlank()) {
            return productInventoryRepository
                    .findByCategoryAndProductNameContainingIgnoreCase(category, search, pageable);
        } else if (hasSearch) {
            return productInventoryRepository.findByProductNameContainingIgnoreCase(search, pageable);
        } else if (category != null && !category.isBlank()) {
            return productInventoryRepository.findByCategory(category, pageable);
        } else {
            return productInventoryRepository.findAll(pageable);
        }
    }
}
