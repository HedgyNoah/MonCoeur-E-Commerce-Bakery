package fishdicg.moncoeur.search_service.configuration;

import fishdicg.moncoeur.search_service.entity.ProductInventoryDocument;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ElasticSearchInitializer implements CommandLineRunner {
    ElasticsearchOperations operations;

    @Override
    public void run(String... args) throws Exception {
        Class<ProductInventoryDocument> clazz = ProductInventoryDocument.class;
        if (!operations.indexOps(clazz).exists()) {
            operations.indexOps(clazz).create();
            operations.indexOps(clazz).putMapping();
        }
    }
}
