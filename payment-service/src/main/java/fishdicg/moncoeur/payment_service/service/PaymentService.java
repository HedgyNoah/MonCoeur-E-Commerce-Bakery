package fishdicg.moncoeur.payment_service.service;

import fishdicg.moncoeur.payment_service.constant.PaymentStatus;
import fishdicg.moncoeur.payment_service.dto.PageResponse;
import fishdicg.moncoeur.payment_service.dto.request.PaymentRequest;
import fishdicg.moncoeur.payment_service.dto.request.ShippingUpdateRequest;
import fishdicg.moncoeur.payment_service.dto.response.PaymentResponse;
import fishdicg.moncoeur.payment_service.entity.Payment;
import fishdicg.moncoeur.payment_service.exception.AppException;
import fishdicg.moncoeur.payment_service.exception.ErrorCode;
import fishdicg.moncoeur.payment_service.mapper.PaymentMapper;
import fishdicg.moncoeur.payment_service.repository.PaymentRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentService {
    PaymentRepository paymentRepository;
    PaymentMapper paymentMapper;
    KafkaProducerService kafkaProducerService;

    public PaymentResponse create(PaymentRequest request) {
        Payment payment = paymentRepository
                .findByCartId(request.getCartId()).orElse(paymentMapper.toPayment(request));
        payment.setPayed(false);
        payment.setPaymentStatus(PaymentStatus.NOT_STARTED);

        return paymentMapper.toPaymentResponse(paymentRepository.save(payment));
    }

    public PaymentResponse updateShipping(String id, ShippingUpdateRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        paymentMapper.updatePayment(payment, request);

        return paymentMapper.toPaymentResponse(
                paymentRepository.save(payment));
    }

    public PaymentResponse update(String id, PaymentStatus paymentStatus) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        log.info("payment status: {}", paymentStatus);
        log.info("be payment status: {}", PaymentStatus.SHIPPING);
        if(PaymentStatus.SHIPPING.equals(paymentStatus)) {
            payment.setPayed(true);
            kafkaProducerService.paymentCompleted(payment.getCartId());
        }
        payment.setPaymentStatus(paymentStatus);

        return paymentMapper.toPaymentResponse(
                paymentRepository.save(payment));
    }

    public PaymentResponse complete(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        payment.setPayed(true);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);

        kafkaProducerService.paymentCompleted(payment.getCartId());

        return paymentMapper.toPaymentResponse(
                paymentRepository.save(payment));
    }

    public PageResponse<PaymentResponse> getAll(int page, int size, String sortBy,
                                                String order, String search) {
        Sort sort = "asc".equalsIgnoreCase(order) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Payment> pageData;
        if(search != null && !search.isEmpty()) {
            pageData = paymentRepository.findByUserIdContaining(search, pageable);
        } else {
            pageData = paymentRepository.findAll(pageable);
        }
        List<PaymentResponse> paymentResponseList = pageData.getContent().stream()
                .map(paymentMapper::toPaymentResponse).toList();

        return PageResponse.<PaymentResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPage(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .sortBy(sortBy)
                .order(order)
                .search(search)
                .data(paymentResponseList)
                .build();
    }

    public PageResponse<PaymentResponse> getMyShipping(int page, int size, String sortBy,
                                               String order, String search) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Sort sort = "asc".equalsIgnoreCase(order) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Payment> pageData;
        if(search != null && !search.isEmpty()) {
            pageData = paymentRepository
                    .findByUserIdAndPaymentStatusAndPaymentIdContaining(
                            userId, PaymentStatus.SHIPPING, search, pageable);
        } else {
            pageData = paymentRepository.findByUserIdAndPaymentStatus(userId, PaymentStatus.SHIPPING, pageable);
        }
        List<PaymentResponse> paymentResponseList = pageData.getContent().stream()
                .map(paymentMapper::toPaymentResponse).toList();

        return PageResponse.<PaymentResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPage(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .sortBy(sortBy)
                .order(order)
                .search(search)
                .data(paymentResponseList)
                .build();
    }

    public PaymentResponse getOne(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        return paymentMapper.toPaymentResponse(payment);
    }

    public void delete(String id) {
        paymentRepository.deleteById(id);
    }
}
