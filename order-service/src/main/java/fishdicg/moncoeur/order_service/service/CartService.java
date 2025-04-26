package fishdicg.moncoeur.order_service.service;

import fishdicg.moncoeur.order_service.dto.PageResponse;
import fishdicg.moncoeur.order_service.dto.request.CartRequest;
import fishdicg.moncoeur.order_service.dto.response.CartResponse;
import fishdicg.moncoeur.order_service.dto.response.OrderResponse;
import fishdicg.moncoeur.order_service.entity.Cart;
import fishdicg.moncoeur.order_service.entity.Order;
import fishdicg.moncoeur.order_service.exception.AppException;
import fishdicg.moncoeur.order_service.exception.ErrorCode;
import fishdicg.moncoeur.order_service.mapper.CartMapper;
import fishdicg.moncoeur.order_service.mapper.OrderMapper;
import fishdicg.moncoeur.order_service.repository.CartRepository;
import fishdicg.moncoeur.order_service.repository.OrderRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartRepository cartRepository;
    CartMapper cartMapper;
    OrderMapper orderMapper;
    OrderRepository orderRepository;

    public Cart generateCart(CartRequest request) {
        Cart cart = cartMapper.toCart(request);

        Set<Order> orders = new HashSet<>();
        Double totalCost = 0.0;

        for (String orderId : request.getOrders()) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

            orders.add(order);
            totalCost += order.getOrderFee();
        }
        cart.setTotalCost(totalCost);
        cart.setOrders(orders);

        return cartRepository.save(cart);
    }

    public void updateCartTotal(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));
        Double totalCost = 0.0;
        for (Order order : cart.getOrders()) {
            totalCost += order.getOrderFee();
        }
        cart.setTotalCost(totalCost);
        cartMapper.toCartResponse(cartRepository.save(cart));
    }

    public CartResponse updateCart(String cartId, CartRequest request) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));
        cartMapper.updateCart(cart, request);

        Set<Order> orders = new HashSet<>();
        Double totalCost = 0.0;

        for (String orderId : request.getOrders()) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

            orders.add(order);
            totalCost += order.getOrderFee();
        }
        cart.setTotalCost(totalCost);
        cart.setOrders(orders);

        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    public void deleteCart(String cartId) {
        cartRepository.deleteById(cartId);
    }

    public CartResponse getCart(String cartId, int page, int size, String sortBy, String order) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));
        CartResponse cartResponse = cartMapper.toCartResponse(cart);
        var orders = getOrdersWithPagination(cart, page, size, sortBy, order);
        cartResponse.setOrders(orders);

        return cartResponse;
    }

    public PageResponse<CartResponse> getAllCarts(int page, int size,
                                                  String sortBy, String order, String search) {
        Sort sort = "asc".equalsIgnoreCase(order) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Cart> pageData;
        if(search != null && !search.isEmpty()) {
            pageData = cartRepository.findAllByUserIdContaining(search, pageable);
        } else {
            pageData = cartRepository.findAll(pageable);
        }
        List<CartResponse> cartResponseList = pageData.getContent().stream()
                .map(cartMapper::toCartResponse).toList();

        return PageResponse.<CartResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPage(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .sortBy(sortBy)
                .order(order)
                .search(search)
                .data(cartResponseList)
                .build();
    }

    public CartResponse getMyCart(int page, int size, String sortBy, String order) {
        var userId = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Cart cart = cartRepository.findByUserIdAndIsPayedFalse(userId);

        var orderResponses = getOrdersWithPagination(cart, page, size, sortBy, order);

        var cartResponse = cartMapper.toCartResponse(cart);
        log.info("Orders: {}", orderResponses);
        cartResponse.setOrders(orderResponses);

        return cartResponse;
    }

    public boolean cartExisted(String userId) {
        Cart cart = cartRepository.findByUserIdAndIsPayedFalse(userId);
        return cart != null;
    }

    private PageResponse<OrderResponse> getOrdersWithPagination(
            Cart cart, int page, int size, String sortBy, String order) {
        Sort sort = "asc".equalsIgnoreCase(order) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Order> pageData = orderRepository.findAllByCart(cart, pageable);

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
}
