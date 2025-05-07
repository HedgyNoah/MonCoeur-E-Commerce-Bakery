package fishdicg.moncoeur.product_service.service;

import fishdicg.moncoeur.product_service.dto.PageResponse;
import fishdicg.moncoeur.product_service.dto.request.ProductRequest;
import fishdicg.moncoeur.product_service.dto.response.ProductResponse;
import fishdicg.moncoeur.product_service.entity.Category;
import fishdicg.moncoeur.product_service.entity.Product;
import fishdicg.moncoeur.product_service.exception.AppException;
import fishdicg.moncoeur.product_service.exception.ErrorCode;
import fishdicg.moncoeur.product_service.mapper.ProductMapper;
import fishdicg.moncoeur.product_service.repository.CategoryRepository;
import fishdicg.moncoeur.product_service.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryRepository categoryRepository;
    KafkaProducerService kafkaProducerService;

    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toProduct(request);

        Category category = categoryRepository.findByCategoryTitle(request.getCategory())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        product.setCategory(category);
        product.setSlug(generateSlug(product.getProductName()));
        product = productRepository.save(product);

        kafkaProducerService.createProduct(product, request);
        return productMapper.toProductResponse(product);
    }

    public ProductResponse updateProduct(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        productMapper.updateProduct(product, request);

        Category category = categoryRepository.findByCategoryTitle(request.getCategory())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        product.setCategory(category);
        product.setSlug(generateSlug(product.getProductName()));

        product = productRepository.save(product);
        kafkaProducerService.updateProduct(product, request);

        return productMapper.toProductResponse(product);
    }

    public PageResponse<ProductResponse> getAllProducts(int page, int size, String sortBy,
                                                        String order, String search) {
        Sort sort = "asc".equalsIgnoreCase(order) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Product> pageData;
        if(search != null && !search.isEmpty()) {
            pageData = productRepository.findByProductNameContaining(search, pageable);
        } else {
            pageData = productRepository.findAll(pageable);
        }
        List<ProductResponse> productResponseList = pageData.getContent()
                .stream().map(productMapper::toProductResponse).toList();

        return PageResponse.<ProductResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPage(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .sortBy(sortBy)
                .order(order)
                .search(search)
                .data(productResponseList)
                .build();
    }

    public PageResponse<ProductResponse> getAllProductsByCategory(int page, int size, String category,
                                                                  String sortBy, String order, String search) {
        Sort sort = "asc".equalsIgnoreCase(order) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Product> pageData;
        if (search != null && !search.isBlank()) {
            pageData = productRepository
                    .findByCategory_CategoryTitleAndProductNameContainingIgnoreCase(category, search, pageable);
        } else {
            pageData = productRepository
                    .findByCategory_CategoryTitle(category, pageable);
        }
        List<ProductResponse> productResponseList = pageData.getContent()
                .stream().map(productMapper::toProductResponse).toList();

        return PageResponse.<ProductResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPage(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .sortBy(sortBy)
                .order(order)
                .search(search)
                .data(productResponseList)
                .build();
    }

    public ProductResponse getProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        return productMapper.toProductResponse(product);
    }

    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        kafkaProducerService.deleteItem(product.getProductId(), product.getImageName());
        productRepository.delete(product);
    }

    public Double getPrice(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        return product.getPrice();
    }

    public String getName(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        return product.getProductName();
    }

    public String getImageName(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        return product.getImageName();
    }

    public void syncProducts() {
        List<Product> products = productRepository.findAll();
        products.forEach(kafkaProducerService::timeStampSync);
    }

    private String generateSlug(String productName) {
        String baseSlug = productName.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-)|(-$)", "");
        String uniqueSlug = baseSlug;
        int suffix = 1;

        while (productRepository.existsBySlug(uniqueSlug)) {
            uniqueSlug = baseSlug + "-" + suffix;
            suffix++;
        }

        return uniqueSlug;
    }
}
