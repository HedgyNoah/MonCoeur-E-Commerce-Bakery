package fishdicg.moncoeur.payment_service.mapper;

import fishdicg.moncoeur.payment_service.dto.request.PaymentRequest;
import fishdicg.moncoeur.payment_service.dto.response.PaymentResponse;
import fishdicg.moncoeur.payment_service.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toPayment(PaymentRequest request);

    PaymentResponse toPaymentResponse(Payment payment);

    void updatePayment(@MappingTarget Payment payment, PaymentRequest request);
}
