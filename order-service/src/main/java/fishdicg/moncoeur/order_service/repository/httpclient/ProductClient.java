package fishdicg.moncoeur.order_service.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-client", url = "${app.product-service.url}")
public interface ProductClient {

    @GetMapping(value = "/internal/price/{productId}")
    ResponseEntity<Double> getPrice(@PathVariable String productId);

    @GetMapping(value = "/internal/name/{productId}")
    ResponseEntity<String> getName(@PathVariable String productId);
}
