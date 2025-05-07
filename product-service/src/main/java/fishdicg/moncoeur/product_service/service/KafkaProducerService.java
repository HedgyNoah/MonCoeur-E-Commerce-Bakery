package fishdicg.moncoeur.product_service.service;

import event.dto.CreateProductEvent;
import event.dto.DeleteItemEvent;
import event.dto.TimeStampSyncEvent;
import event.dto.UpdateProductEvent;
import fishdicg.moncoeur.product_service.dto.request.ProductRequest;
import fishdicg.moncoeur.product_service.entity.Product;
import fishdicg.moncoeur.product_service.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerService {
    ProductRepository productRepository;
    KafkaTemplate<String, DeleteItemEvent> deleteTemplate;
    KafkaTemplate<String, CreateProductEvent> createTemplate;
    KafkaTemplate<String, UpdateProductEvent> updateTemplate;
    KafkaTemplate<String, TimeStampSyncEvent> syncTemplate;

    public void createProduct(Product product, ProductRequest request) {
        CreateProductEvent createProductEvent = new CreateProductEvent(
                product.getProductId(), request.getProductName(), request.getProductDescription(),
                generateSlug(request.getProductName()),
                request.getPrice(), request.getQuantity(), request.getLowStockThreshold(),
                request.getCategory(), product.getCreatedAt(), product.getUpdatedAt());
        createTemplate.send("create-product-topic", createProductEvent);
    }

    public void updateProduct(Product product, ProductRequest request) {
        UpdateProductEvent updateProductEvent = new UpdateProductEvent(
                        product.getProductId(), request.getProductName(), request.getProductDescription(),
                generateSlug(request.getProductName()),
                        request.getPrice(), request.getQuantity(), request.getLowStockThreshold(),
                        request.getCategory(), product.getCreatedAt(), product.getUpdatedAt());
        updateTemplate.send("update-product-topic", updateProductEvent);
    }

    public void deleteItem(String productId, String fileName) {
        DeleteItemEvent deleteItemEvent = new DeleteItemEvent(productId, fileName);
        deleteTemplate.send("delete-item-topic", deleteItemEvent);
    }

    public void timeStampSync(Product product) {
        TimeStampSyncEvent timeStampSyncEvent = new TimeStampSyncEvent(product.getProductId(),
                product.getCreatedAt(), product.getUpdatedAt());
        syncTemplate.send("time-stamp-sync-topic", timeStampSyncEvent);
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
