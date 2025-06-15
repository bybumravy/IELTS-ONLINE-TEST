package web.ielts.FileUpload;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${aws.access.key.id}")
    private String accessKey;

    @Value("${aws.secret.access.key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucketName;


    private S3Client s3Client;

    public FileUploadService() {
    }

    public FileUploadService(String accessKey, String secretKey, String region, String bucketName, S3Client s3Client) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucketName = bucketName;
        this.s3Client = s3Client;
    }

    @PostConstruct
    public void init() {
        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String filePath = generateFilePathWithTimestamp(folder, file.getOriginalFilename());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + filePath;
    }

    private String generateFilePathWithTimestamp(String folder, String originalFilename) {
        // Tách phần tên và phần mở rộng
        String baseName = originalFilename.contains(".") ?
                originalFilename.substring(0, originalFilename.lastIndexOf(".")) :
                originalFilename;

        String extension = originalFilename.contains(".") ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) :
                "";

        long timestamp = Instant.now().toEpochMilli();

        return String.format("%s/test/%s_%d%s", folder, baseName, timestamp, extension);
    }
}
