package fishdicg.moncoeur.media_service.controller;

import fishdicg.moncoeur.media_service.service.MinioService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinioController {
    MinioService minioService;

    @PostMapping("/upload")
    ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam("product") String productId) {
        return ResponseEntity.ok(minioService.upload(productId, file));
    }

    @GetMapping("/download")
    ResponseEntity<byte[]> downloadFile(@RequestParam("fileName") String fileName) {
        String contentType = minioService.determineContentType(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName)
                .body(minioService.downloadFile(fileName));
    }

    @GetMapping("/url")
    ResponseEntity<String> getPreSignedUrl(@RequestParam("fileName") String fileName) {
        return ResponseEntity.ok(minioService.getPreSignedUrl(fileName));
    }

    @DeleteMapping
    ResponseEntity<String> deleteFile(@RequestParam("fileName") String fileName) {
        minioService.deleteFile(fileName);
        return ResponseEntity.ok().body("File has been deleted");
    }
}
