package fishdicg.moncoeur.search_service.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentStatus {
    NOT_STARTED("not_started"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    SHIPPING("shipping"),
    CANCELED("canceled");

    private final String status;
}
