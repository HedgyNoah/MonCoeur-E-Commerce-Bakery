package fishdicg.moncoeur.order_service.service;

import fishdicg.moncoeur.order_service.dto.PageResponse;
import fishdicg.moncoeur.order_service.dto.request.CartRequest;
import fishdicg.moncoeur.order_service.dto.request.OrderRequest;
import fishdicg.moncoeur.order_service.dto.response.OrderResponse;
import fishdicg.moncoeur.order_service.entity.Cart;
import fishdicg.moncoeur.order_service.entity.Order;
import fishdicg.moncoeur.order_service.exception.AppException;
import fishdicg.moncoeur.order_service.exception.ErrorCode;
import fishdicg.moncoeur.order_service.mapper.OrderMapper;
import fishdicg.moncoeur.order_service.repository.CartRepository;
import fishdicg.moncoeur.order_service.repository.OrderRepository;
import fishdicg.moncoeur.order_service.repository.httpclient.ProductClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    CartService cartService;
    CartRepository cartRepository;
    ProductClient productClient;

    public OrderResponse createOrder(OrderRequest request) {
        var userId = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        log.info("USER ID: {}", userId);
        Order order = orderMapper.toOrder(request);

        var imageName = productClient.getImageName(request.getProductId()).getBody();
        if(imageName != null) {
            order.setImageName(imageName);
        } else throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);

        var productName = productClient.getName(
                request.getProductId()).getBody();
        if(productName != null) {
            order.setOrderName(productName);
        } else throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);

        var productPrice = productClient
                .getPrice(request.getProductId()).getBody();
        if(productPrice != null) {
            Double fee = order.getOrderQuantity() * productPrice;
            order.setOrderFee(fee);
        } else throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);

        if(cartService.cartExisted(userId)) {
            Cart existingCart = cartRepository.findByUserIdAndIsPayedFalse(userId);
            order.setCart(existingCart);

            cartRepository.save(existingCart);
            cartService.updateCartTotal(existingCart.getCartId());

            orderRepository.save(order);
            log.info("Cart existed!");
        } else {
            order = orderRepository.save(order);
            HashSet<String> orders = new HashSet<>();
            orders.add(order.getOrderId());

            order.setCart(cartService.generateCart(CartRequest.builder()
                    .userId(userId)
                    .orders(orders)
                    .build()));
            orderRepository.save(order);
        }

        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse updateOrder(String orderId, OrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        orderMapper.updateOrder(order, request);

        var productPrice = productClient
                .getPrice(order.getProductId()).getBody();
        if(productPrice != null) {
            Double fee = order.getOrderQuantity() * productPrice;
            order.setOrderFee(fee);
        } else throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);

        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

    public OrderResponse getOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        return orderMapper.toOrderResponse(order);
    }

    public List<OrderResponse> getAllOrder() {
        return orderRepository.findAll().stream().map(orderMapper::toOrderResponse).toList();
    }

    public PageResponse<OrderResponse> getPurchased(int page, int size,
                                                  String sortBy, String order) {
        Sort sort = "asc".equalsIgnoreCase(order) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var userId = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Page<Order> pageData = orderRepository.findPaidOrdersByUserId(userId, pageable);

        List<OrderResponse> orderResponseList = pageData.getContent().stream()
                .map(orderMapper::toOrderResponse).toList();

        return PageResponse.<OrderResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPage(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .sortBy(sortBy)
                .order(order)
                .data(orderResponseList)
                .build();
    }

    public void deleteOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        String cartId = order.getCart().getCartId();
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        orderRepository.deleteById(orderId);

        cartService.updateCartTotal(cart.getCartId());
        if(ObjectUtils.isEmpty(cart.getOrders())) cartService.deleteCart(cartId);
    }

    public void deleteOrderByProductId(String productId) {
        Order order = orderRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        String cartId = order.getCart().getCartId();
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        orderRepository.delete(order);

        cartService.updateCartTotal(cart.getCartId());
        if(ObjectUtils.isEmpty(cart.getOrders())) cartService.deleteCart(cartId);
    }
}
