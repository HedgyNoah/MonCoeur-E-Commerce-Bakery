package fishdicg.moncoeur.order_service.mapper;

import fishdicg.moncoeur.order_service.dto.request.OrderRequest;
import fishdicg.moncoeur.order_service.dto.response.OrderResponse;
import fishdicg.moncoeur.order_service.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "cart", ignore = true)
    Order toOrder(OrderRequest request);

    @Mapping(target = "cart", ignore = true)
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "cart", ignore = true)
    void updateOrder(@MappingTarget Order order, OrderRequest request);
}
