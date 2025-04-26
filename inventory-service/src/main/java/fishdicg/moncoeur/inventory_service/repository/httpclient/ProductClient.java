package fishdicg.moncoeur.inventory_service.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-client", url = "${app.product-service.url}")
public interface ProductClient {
    @GetMapping(value = "/internal/name/{productId}")
    ResponseEntity<String> getProductNameById(@PathVariable String productId);
}
