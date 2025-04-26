package fishdicg.moncoeur.search_service.service;

import event.dto.CreateProductEvent;
import event.dto.DeleteItemEvent;
import event.dto.UpdateProductEvent;
import event.dto.UploadImageEvent;
import fishdicg.moncoeur.search_service.entity.ProductInventoryDocument;
import fishdicg.moncoeur.search_service.exception.AppException;
import fishdicg.moncoeur.search_service.exception.ErrorCode;
import fishdicg.moncoeur.search_service.mapper.ProductInventoryMapper;
import fishdicg.moncoeur.search_service.repository.ProductInventoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KafkaConsumerService {
    ProductInventoryRepository productInventoryRepository;
    ProductInventoryMapper productInventoryMapper;

    @KafkaListener(topics = "create-product-topic", groupId = "search-service-group")
    public void createProduct(CreateProductEvent event) {
        ProductInventoryDocument productInventoryDocument =
                productInventoryMapper.toProductInventoryDocument(event);
        productInventoryRepository.save(productInventoryDocument);
        log.info("product with id {} successfully saved", productInventoryDocument.getProductId());
    }

    @KafkaListener(topics = "update-product-topic", groupId = "search-service-group")
    public void updateProduct(UpdateProductEvent event) {
        ProductInventoryDocument productInventoryDocument = productInventoryRepository
                .findById(event.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        productInventoryMapper.updateProductInventoryDocument(productInventoryDocument, event);

        productInventoryRepository.save(productInventoryDocument);
        log.info("product with id {} successfully updated", productInventoryDocument.getProductId());
    }

    @KafkaListener(topics = "delete-item-topic", groupId = "search-service-group")
    public void deleteProduct(DeleteItemEvent event) {
        ProductInventoryDocument productInventoryDocument = productInventoryRepository
                .findById(event.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        productInventoryRepository.delete(productInventoryDocument);
        log.info("product successfully deleted");
    }

    @KafkaListener(topics = "upload-image-topic", groupId = "search-service-group")
    public void uploadImage(UploadImageEvent event) {
        ProductInventoryDocument productInventoryDocument = productInventoryRepository.findById(event.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        productInventoryDocument.setImageName(event.getImageName());
        productInventoryRepository.save(productInventoryDocument);
        log.info("image name has been saved");
    }
}
