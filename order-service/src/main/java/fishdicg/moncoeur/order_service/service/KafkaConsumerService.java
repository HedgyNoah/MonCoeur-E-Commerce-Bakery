package fishdicg.moncoeur.order_service.service;

import event.dto.CartPayedEvent;
import event.dto.DeleteItemEvent;
import fishdicg.moncoeur.order_service.entity.Cart;
import fishdicg.moncoeur.order_service.entity.Order;
import fishdicg.moncoeur.order_service.exception.AppException;
import fishdicg.moncoeur.order_service.exception.ErrorCode;
import fishdicg.moncoeur.order_service.repository.CartRepository;
import fishdicg.moncoeur.order_service.repository.OrderRepository;
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

    @KafkaListener(topics = "delete-item-topic", groupId = "order-service-group")
    public void deleteItem(DeleteItemEvent event) {
        Order order = orderRepository.findByProductId(event.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));
        orderRepository.delete(order);
    }

    @KafkaListener(topics = "payment-completed-topic", groupId = "order-service-group")
    public void cartPayed(CartPayedEvent cartPayedEvent) {
        Cart cart = cartRepository.findById(cartPayedEvent.getCartId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        Set<Order> orders = cart.getOrders();
        orders.forEach(order -> kafkaProducerService
                .decreaseInventoryStock(order.getProductId(), order.getOrderQuantity()));
        log.info("decrease from productClient called");
        cart.setPayed(true);
    }
}
