package fishdicg.moncoeur.payment_service.service;

import fishdicg.moncoeur.payment_service.constant.PaymentStatus;
import fishdicg.moncoeur.payment_service.dto.PageResponse;
import fishdicg.moncoeur.payment_service.dto.request.PaymentRequest;
import fishdicg.moncoeur.payment_service.dto.response.PaymentResponse;
import fishdicg.moncoeur.payment_service.entity.Payment;
import fishdicg.moncoeur.payment_service.exception.AppException;
import fishdicg.moncoeur.payment_service.exception.ErrorCode;
import fishdicg.moncoeur.payment_service.mapper.PaymentMapper;
import fishdicg.moncoeur.payment_service.repository.PaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
public class PaymentService {
    PaymentRepository paymentRepository;
    PaymentMapper paymentMapper;
    KafkaProducerService kafkaProducerService;

    public PaymentResponse create(PaymentRequest request) {
        Payment payment = paymentMapper.toPayment(request);
        payment.setPayed(false);
        payment.setPaymentStatus(PaymentStatus.NOT_STARTED);

        return paymentMapper.toPaymentResponse(
                paymentRepository.save(payment));
    }

    public PaymentResponse update(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        payment.setPayed(false);
        payment.setPaymentStatus(PaymentStatus.IN_PROGRESS);

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

    public List<PaymentResponse> getMyPayments() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return paymentRepository.findAllByUserId(userId).stream()
                .map(paymentMapper::toPaymentResponse).toList();
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
