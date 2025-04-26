package fishdicg.moncoeur.media_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    ORDER_NOT_EXISTED(1003, "Order not existed", HttpStatus.NOT_FOUND),
    CART_NOT_EXISTED(1004, "Cart not existed", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_EXISTED(1005, "Category not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    PRODUCT_NOT_EXISTED(1008, "Product not existed", HttpStatus.NOT_FOUND),
    INVALID_DOB(1010, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.statusCode = statusCode;
        this.message = message;
    }
}
