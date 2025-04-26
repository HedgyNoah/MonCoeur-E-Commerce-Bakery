package fishdicg.moncoeur.order_service.mapper;

import fishdicg.moncoeur.order_service.dto.request.CartRequest;
import fishdicg.moncoeur.order_service.dto.response.CartResponse;
import fishdicg.moncoeur.order_service.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "orders", ignore = true)
    Cart toCart(CartRequest request);

    @Mapping(target = "orders", ignore = true)
    CartResponse toCartResponse(Cart cart);

    @Mapping(target = "orders", ignore = true)
    void updateCart(@MappingTarget Cart cart, CartRequest request);
}
