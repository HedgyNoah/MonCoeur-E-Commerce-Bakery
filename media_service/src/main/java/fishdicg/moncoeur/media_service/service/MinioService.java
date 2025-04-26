package fishdicg.moncoeur.media_service.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MinioService {
    MinioClient minioClient;
    KafkaProducerService kafkaProducerService;

    @Value("${minio.bucket}")
    @NonFinal
    String bucket;

    public String upload(String productId, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                    .build());

            kafkaProducerService.uploadImage(productId, fileName);
            return fileName;
        } catch (Exception e) {
            return "Error uploading file: {}" + e.getMessage();
        }
    }

    public byte[] downloadFile(String fileName) {
        try(InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(fileName)
                        .build()
        )) {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            );
            log.info("response type = {}", stat.contentType());
            return stream.readAllBytes();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            );
            log.info("Deleted file: {}", fileName);
        } catch (Exception e) {
            log.error("Error deleting file: {}", fileName, e);
        }
    }

    public String getPreSignedUrl(String fileName) {
        try{
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .method(Method.GET)
                            .build()
            );
        } catch (Exception e) {
            return "Failed to fetch Image Url";
        }
    }

    public String determineContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        return switch (extension) {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            default -> "application/octet-stream";  // Fallback for unknown types
        };
    }
}
