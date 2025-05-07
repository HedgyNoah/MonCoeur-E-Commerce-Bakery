package fishdicg.moncoeur.identity_service.constant;

import fishdicg.moncoeur.identity_service.dto.response.NavigationResponse;

public class Navigation {
    public static final NavigationResponse PRODUCTS_ROUTE = NavigationResponse.builder()
            .name("Products")
            .route("/admin/control/products")
            .build();
    public static final NavigationResponse USERS_ROUTE = NavigationResponse.builder()
            .name("Users")
            .route("/admin/control/users")
            .build();
    public static final NavigationResponse CATEGORIES_ROUTE = NavigationResponse.builder()
            .name("Categories")
            .route("/admin/control/categories")
            .build();
    public static final NavigationResponse SALES_ROUTE = NavigationResponse.builder()
            .name("Sales")
            .route("/admin/control/sales")
            .build();
    public static final NavigationResponse HOME_ROUTE = NavigationResponse.builder()
            .name("Home")
            .route("/home")
            .build();
    public static final NavigationResponse PROFILE_ROUTE = NavigationResponse.builder()
            .name("Profile")
            .route("/user/profile")
            .build();
    public static final NavigationResponse HISTORY_ROUTE = NavigationResponse.builder()
            .name("Purchased")
            .route("/user/purchased")
            .build();
    public static final NavigationResponse SHIPPING_ROUTE = NavigationResponse.builder()
            .name("Shipping")
            .route("/user/shipping")
            .build();

    private Navigation() {}
}
