package kr.or.kosa.cmsplusmain.domain.simpconsent;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/simple-consent")
@RequiredArgsConstructor
public class FileUploadController {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.consent-bucket}")
    private String consentBucket;

    @PostMapping(value = "/sign", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadSignature(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, bucket, "signature-");
    }

    @PostMapping(value = "/consent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadConsentFile(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, consentBucket, "consent-");
    }

    private ResponseEntity<?> uploadFile(MultipartFile file, String bucketName, String prefix) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("파일이 비어있습니다.");
            }

            String fileName = prefix + UUID.randomUUID() + "-" + file.getOriginalFilename();
            String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);
            return ResponseEntity.ok().body(new FileUrlResponse(fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 중 IO 오류 발생: " + e.getMessage());
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Amazon S3 서비스 오류: " + e.getMessage());
        } catch (SdkClientException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Amazon S3 클라이언트 오류: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("예상치 못한 오류 발생: " + e.getMessage());
        }
    }

    @Getter
    private static class FileUrlResponse {
        private String fileUrl;

        public FileUrlResponse(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }
}