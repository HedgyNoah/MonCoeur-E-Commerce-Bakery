package fishdicg.moncoeur.search_service.service;

import event.dto.*;
import fishdicg.moncoeur.search_service.entity.ProductInventoryDocument;
import fishdicg.moncoeur.search_service.exception.AppException;
import fishdicg.moncoeur.search_service.exception.ErrorCode;
import fishdicg.moncoeur.search_service.mapper.ProductInventoryMapper;
import fishdicg.moncoeur.search_service.repository.ProductInventoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KafkaConsumerService {
    ProductInventoryRepository productInventoryRepository;
    ProductInventoryMapper productInventoryMapper;

    @KafkaListener(topics = "create-product-topic")
    public void createProduct(CreateProductEvent event) {
        try {
            ProductInventoryDocument productInventoryDocument =
                    productInventoryMapper.toProductInventoryDocument(event);
            productInventoryRepository.save(productInventoryDocument);
            log.info("product with id {} successfully saved", productInventoryDocument.getProductId());
        } catch (Exception e) {
            log.error("Failed to create product: {}", event.getProductId(), e);
        }
    }

    @KafkaListener(topics = "update-product-topic")
    public void updateProduct(UpdateProductEvent event) {
        try {
            log.info("updating product with id {}", event.getProductId());
            ProductInventoryDocument productInventoryDocument = productInventoryRepository
                    .findById(event.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
            productInventoryMapper.updateProductInventoryDocument(productInventoryDocument, event);

            productInventoryRepository.save(productInventoryDocument);
            log.info("product with id {} successfully updated", productInventoryDocument.getProductId());
        } catch (Exception e) {
            log.error("Failed to update product: {}", event.getProductId(), e);
        }
    }

    @KafkaListener(topics = "delete-item-topic")
    public void deleteProduct(DeleteItemEvent event) {
        try {
            ProductInventoryDocument productInventoryDocument = productInventoryRepository
                    .findById(event.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
            productInventoryRepository.delete(productInventoryDocument);
            log.info("product successfully deleted");
        } catch (Exception e) {
            log.error("Failed to delete product: {}", event.getProductId(), e);
        }
    }

    @KafkaListener(topics = "decrease-stock-topic")
    public void decreaseStock(DecreaseStockEvent event) {
        try {
            ProductInventoryDocument inventory = productInventoryRepository.findById(event.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
            inventory.setQuantity(inventory.getQuantity() - event.getQuantity());
            productInventoryRepository.save(inventory);
            log.info("Quantity after get decreased: {}", inventory.getQuantity() - event.getQuantity());
        } catch (Exception e) {
            log.error("Error decrease stock", e);
        }
    }

    @KafkaListener(topics = "upload-image-topic")
    public void uploadImage(UploadImageEvent event) {
        try {
            ProductInventoryDocument productInventoryDocument = productInventoryRepository.findById(event.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
            productInventoryDocument.setImageName(event.getImageName());
            productInventoryRepository.save(productInventoryDocument);
            log.info("image name has been saved");
        } catch (Exception e) {
            log.error("Failed to upload image product: {}", event.getProductId(), e);
        }
    }

    @KafkaListener(topics = "time-stamp-sync-topic")
    public void timeStampSync(TimeStampSyncEvent event) {
        try {
            ProductInventoryDocument productInventoryDocument = productInventoryRepository.findById(event.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
            productInventoryDocument.setCreatedAt(event.getCreatedAt());
            productInventoryDocument.setUpdatedAt(event.getUpdatedAt());
            productInventoryRepository.save(productInventoryDocument);
            log.info("Product {} time stamp has been synced", event.getProductId());
        } catch (Exception e) {
            log.error("Failed to sync product: {}", event.getProductId(), e);
        }
    }
}
