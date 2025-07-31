package com.climbx.climbx.video.service;

import com.climbx.climbx.common.enums.ErrorCode;
import com.climbx.climbx.common.exception.BusinessException;
import com.climbx.climbx.video.exception.AwsBucketNameNotConfiguredException;
import com.climbx.climbx.video.exception.AwsBucketNotFoundException;
import com.climbx.climbx.video.exception.FileExtensionNotExistsException;
import com.github.benmanes.caffeine.cache.Cache;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkException;
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

    private final Cache<String, Boolean> existingBuckets;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.presigned-url-expiration}")
    private long presignedUrlExpiration;

    public String generatePresignedUrl(UUID videoId, String fileExtension) {
        log.info("Generating presigned URL for videoId: {}, fileExtension: {}", videoId,
            fileExtension);

        // 입력값 검증
        validateInputParameters(videoId, fileExtension);

        // 버킷 이름이 설정되어 있지 않으면 예외 발생
        if (bucketName == null || bucketName.isEmpty()) {
            log.error("S3 bucket name is not configured");
            throw new AwsBucketNameNotConfiguredException(
                ErrorCode.S3_BUCKET_NAME_NOT_CONFIGURED, "S3 bucket name is not configured."
            );
        }

        // 버킷 이름이 캐시에 없으면 S3에서 버킷 존재 여부 확인 후 캐싱
        if (existingBuckets.getIfPresent(bucketName) == null) {
            log.debug("Bucket '{}' not found in cache, checking existence", bucketName);
            if (doesBucketExist(bucketName)) {
                existingBuckets.put(bucketName, true);
                log.info("Bucket '{}' verified and cached", bucketName);
            } else {    // 버킷이 존재하지 않으면 예외 발생
                log.error("S3 bucket does not exist: {}", bucketName);
                throw new AwsBucketNotFoundException(
                    ErrorCode.S3_BUCKET_NOT_FOUND, "Bucket does not exist: " + bucketName
                );
            }
        } else {
            log.debug("Using cached bucket: {}", bucketName);
        }

        // S3 키 생성 (videoId.mp4)
        fileExtension = ensureDotPrefix(fileExtension);
        String s3Key = videoId + fileExtension;

        // S3 presigned URL 생성
        String presignedUrl = createPresignedUrl(s3Key);

        log.info("Successfully generated presigned URL for videoId: {}", videoId);
        return presignedUrl;
    }

    private void validateInputParameters(UUID videoId, String fileExtension) {
        if (videoId == null) {
            log.error("VideoId cannot be null");
            throw new IllegalArgumentException("VideoId cannot be null");
        }

        if (fileExtension == null || fileExtension.trim().isEmpty()) {
            log.warn("File extension cannot be null or empty");
            throw new FileExtensionNotExistsException(
                ErrorCode.FILE_EXTENSION_NOT_EXISTS, "File extension is required."
            );
        }

        log.debug("Input parameters validated successfully");
    }

    private String createPresignedUrl(String s3Key) {
        try {
            log.debug("Creating presigned URL for S3 key: {}", s3Key);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                .putObjectRequest(putObjectRequest)
                .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
                presignRequest);
            String presignedUrl = presignedRequest.url().toString();

            log.debug("Presigned URL created successfully, expiration: {} seconds",
                presignedUrlExpiration);
            return presignedUrl;

        } catch (AwsServiceException ase) {
            log.error(
                "AWS service error while creating presigned URL for key: {}, statusCode: {}, errorCode: {}, message: {}",
                s3Key, ase.statusCode(), ase.awsErrorDetails().errorCode(), ase.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_ERROR,
                "AWS service error: " + ase.getMessage());
        } catch (SdkException se) {
            log.error("AWS SDK error while creating presigned URL for key: {}, message: {}",
                s3Key, se.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_ERROR,
                "AWS SDK error: " + se.getMessage());
        }
    }

    private String ensureDotPrefix(String fileExtension) {
        log.debug("Ensuring dot prefix for file extension: {}", fileExtension);

        if (fileExtension == null) {
            log.warn("File extension is null");
            throw new FileExtensionNotExistsException(
                ErrorCode.FILE_EXTENSION_NOT_EXISTS, "File extension is required."
            );
        }

        if (!fileExtension.startsWith(".")) {
            String originalExtension = fileExtension;
            fileExtension = "." + fileExtension; // 확장자 앞에 점 추가
            log.debug("Added dot prefix to file extension: {} -> {}", originalExtension,
                fileExtension);
        } else {
            log.debug("File extension already has dot prefix: {}", fileExtension);
        }

        return fileExtension;
    }

    private boolean doesBucketExist(String bucketName) {
        log.debug("Checking if bucket exists: {}", bucketName);

        try {
            s3Client.getBucketAcl(r -> r.bucket(bucketName));
            log.debug("Bucket exists and accessible: {}", bucketName);
            return true;
        } catch (AwsServiceException ase) {
            // A redirect error or an AccessDenied exception means the bucket exists but it's not in this region
            // or we don't have permissions to it.
            if ((ase.statusCode() == HttpStatusCode.MOVED_PERMANENTLY) || "AccessDenied".equals(
                ase.awsErrorDetails().errorCode())) {
                log.warn(
                    "Bucket exists but moved permanently or access denied - bucketName: {}, statusCode: {}, errorCode: {}",
                    bucketName, ase.statusCode(), ase.awsErrorDetails().errorCode());
                return true;
            }
            if (ase.statusCode() == HttpStatusCode.NOT_FOUND) {
                log.warn("Bucket not found - bucketName: {}, statusCode: {}",
                    bucketName, ase.statusCode());
                return false;
            }

            log.error(
                "Unexpected AWS service error while checking bucket existence - bucketName: {}, statusCode: {}, errorCode: {}, message: {}",
                bucketName, ase.statusCode(), ase.awsErrorDetails().errorCode(), ase.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_ERROR,
                "Failed to check bucket existence: " + ase.getMessage());
        } catch (Exception e) {
            log.error(
                "Unexpected error while checking bucket existence - bucketName: {}, error: {}",
                bucketName, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR,
                "Failed to check bucket existence: " + e.getMessage());
        }
    }
} 