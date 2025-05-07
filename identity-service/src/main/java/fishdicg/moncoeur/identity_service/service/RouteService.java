package fishdicg.moncoeur.identity_service.service;

import fishdicg.moncoeur.identity_service.constant.Navigation;
import fishdicg.moncoeur.identity_service.dto.response.NavigationResponse;
import fishdicg.moncoeur.identity_service.entity.User;
import fishdicg.moncoeur.identity_service.exception.AppException;
import fishdicg.moncoeur.identity_service.exception.ErrorCode;
import fishdicg.moncoeur.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RouteService {
    UserRepository userRepository;

    public List<NavigationResponse> getRoutes() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()));

        List<NavigationResponse> adminRoutes = Arrays.asList(
                Navigation.PRODUCTS_ROUTE,
                Navigation.CATEGORIES_ROUTE,
                Navigation.USERS_ROUTE,
                Navigation.SALES_ROUTE
        );
        List<NavigationResponse> userRoutes = Arrays.asList(
                Navigation.PROFILE_ROUTE,
                Navigation.HISTORY_ROUTE,
                Navigation.SHIPPING_ROUTE
        );
        return isAdmin ? adminRoutes : userRoutes;
    }
}
