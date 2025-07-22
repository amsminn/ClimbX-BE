package com.climbx.climbx.video.service;

import com.climbx.climbx.common.error.ErrorCode;
import com.climbx.climbx.video.exception.AwsBucketNotFoundException;
import com.climbx.climbx.video.exception.FileExtensionNotExistsException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    private final S3Client s3Client;

    private final Set<String> existingBuckets = new HashSet<>();

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.presigned-url-expiration}")
    private long presignedUrlExpiration;

    public String generatePresignedUrl(UUID videoId, String fileExtension) {

        // 버킷 이름이 설정되어 있지 않으면 예외 발생
        if (bucketName == null || bucketName.isEmpty()) {
            throw new AwsBucketNotFoundException(
                ErrorCode.S3_BUCKET_NOT_FOUND, "Bucket name is not configured."
            );
        }

        if (!existingBuckets.contains(bucketName)) {
            if (doesBucketExist(bucketName)) {
                existingBuckets.add(bucketName);
            } else {    // 버킷이 존재하지 않으면 예외 발생
                throw new AwsBucketNotFoundException(
                    ErrorCode.S3_BUCKET_NOT_FOUND, "Bucket does not exist: " + bucketName
                );
            }
        }

        // S3 키 생성 (videoId.mp4)
        fileExtension = ensureDotPrefix(fileExtension);
        String s3Key = videoId + fileExtension;

        // S3 presigned URL 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
            .putObjectRequest(putObjectRequest)
            .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String presignedUrl = presignedRequest.url().toString();

        log.debug("presignedUrl: {}", presignedUrl);

        return presignedUrl;
    }

    private String ensureDotPrefix(String fileExtension) {
        if (fileExtension == null) {
            throw new FileExtensionNotExistsException(
                ErrorCode.FILE_EXTENSION_NOT_EXISTS, "File extension is required."
            );
        } else if (!fileExtension.startsWith(".")) {
            fileExtension = "." + fileExtension; // 확장자 앞에 점 추가
        }
        return fileExtension;
    }

    private boolean doesBucketExist(String bucketName) {
        try {
            s3Client.getBucketAcl(r -> r.bucket(bucketName));
            return true;
        } catch (AwsServiceException ase) {
            // A redirect error or an AccessDenied exception means the bucket exists but it's not in this region
            // or we don't have permissions to it.
            if ((ase.statusCode() == HttpStatusCode.MOVED_PERMANENTLY) || "AccessDenied".equals(
                ase.awsErrorDetails().errorCode())) {
                return true;
            }
            if (ase.statusCode() == HttpStatusCode.NOT_FOUND) {
                return false;
            }
            throw new AwsBucketNotFoundException(ErrorCode.S3_BUCKET_NOT_FOUND, ase.getMessage());
        }
    }
} 