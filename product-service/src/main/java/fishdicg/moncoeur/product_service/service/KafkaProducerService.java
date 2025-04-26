package fishdicg.moncoeur.product_service.service;

import event.dto.CreateProductEvent;
import event.dto.DeleteItemEvent;
import event.dto.UpdateProductEvent;
import fishdicg.moncoeur.product_service.dto.request.ProductRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerService {
    KafkaTemplate<String, DeleteItemEvent> deleteTemplate;
    KafkaTemplate<String, CreateProductEvent> createTemplate;
    KafkaTemplate<String, UpdateProductEvent> updateTemplate;

    public void createProduct(String productId, ProductRequest request) {
        CreateProductEvent createProductEvent = new CreateProductEvent(
                productId, request.getProductName(), request.getProductDescription(),
                request.getPrice(), request.getQuantity(), request.getLowStockThreshold(),
                request.getCategory());
        createTemplate.send("create-product-topic", createProductEvent);
    }

    public void updateProduct(String productId, ProductRequest request) {
        UpdateProductEvent updateProductEvent = new UpdateProductEvent(
                        productId, request.getProductName(), request.getProductDescription(),
                        request.getPrice(), request.getQuantity(), request.getLowStockThreshold(),
                        request.getCategory());
        updateTemplate.send("update-product-topic", updateProductEvent);
    }

    public void deleteItem(String productId, String fileName) {
        DeleteItemEvent deleteItemEvent = new DeleteItemEvent(productId, fileName);
        deleteTemplate.send("delete-item-topic", deleteItemEvent);
    }
}
