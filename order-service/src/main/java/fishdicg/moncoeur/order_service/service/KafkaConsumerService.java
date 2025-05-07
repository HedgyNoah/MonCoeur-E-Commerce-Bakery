package fishdicg.moncoeur.order_service.service;

import event.dto.CartPayedEvent;
import event.dto.DeleteItemEvent;
import event.dto.UpdateProductEvent;
import event.dto.UploadImageEvent;
import fishdicg.moncoeur.order_service.entity.Cart;
import fishdicg.moncoeur.order_service.entity.Order;
import fishdicg.moncoeur.order_service.exception.AppException;
import fishdicg.moncoeur.order_service.exception.ErrorCode;
import fishdicg.moncoeur.order_service.repository.CartRepository;
import fishdicg.moncoeur.order_service.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KafkaConsumerService {
    OrderRepository orderRepository;
    CartRepository cartRepository;
    KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = "delete-item-topic")
    public void deleteItem(DeleteItemEvent event) {
        try {
            Order order = orderRepository.findByProductId(event.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));
            orderRepository.delete(order);
        } catch (Exception e) {
            log.error("Failed to delete item in kafka", e);
        }
    }

    @Transactional
    @KafkaListener(topics = "payment-completed-topic")
    public void cartPayed(CartPayedEvent cartPayedEvent) {
        try {
            Cart cart = cartRepository.findById(cartPayedEvent.getCartId())
                    .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

            Set<Order> orders = cart.getOrders();
            orders.forEach(order -> kafkaProducerService
                    .decreaseInventoryStock(order.getProductId(), order.getOrderQuantity()));
            log.info("decrease from productClient called");
            cart.setPayed(true);
        } catch (Exception e) {
            log.error("Failed to set payed in kafka", e);
        }
    }

    @KafkaListener(topics = "upload-image-topic")
    public void uploadImage(UploadImageEvent event) {
        try {
            Order order = orderRepository.findByProductId(event.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
            order.setImageName(event.getImageName());
            orderRepository.save(order);
            log.info("image name has been saved");
        } catch (Exception e) {
            log.error("Failed to set image in kafka");
        }
    }

    @KafkaListener(topics = "update-product-topic")
    public void updateProduct(UpdateProductEvent event) {
        try {
            Order order = orderRepository.findByProductId(event.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
            order.setOrderName(event.getProductName());
            orderRepository.save(order);
            log.info("Product name has been saved");
        } catch (Exception e) {
            log.error("Failed to set product name in kafka");
        }
    }
}
