package com.climbx.climbx.common.service;

import com.climbx.climbx.common.enums.ErrorCode;
import com.climbx.climbx.common.exception.BusinessException;
import com.climbx.climbx.video.exception.AwsBucketNameNotConfiguredException;
import com.climbx.climbx.video.exception.AwsBucketNotFoundException;
import com.climbx.climbx.video.exception.AwsCloudFrontDomainNotConfiguredException;
import com.climbx.climbx.video.exception.FileExtensionNotExistsException;
import com.github.benmanes.caffeine.cache.Cache;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
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

    @Value("${aws.s3.videos-media-bucket-name}")
    private String videosMediaBucketName;

    @Value("${aws.s3.profile-image-bucket-name}")
    private String profileImageBucketName;

    @Value("${aws.s3.presigned-url-expiration}")
    private long presignedUrlExpiration;

    @Value("${aws.cloudfront.domain:}")
    private String cloudfrontDomain;

    public String generateVideoUploadPresignedUrl(UUID videoId, String fileExtension) {
        log.info("Generating video upload presigned URL for videoId: {}, fileExtension: {}",
            videoId, fileExtension);

        // 입력값 검증
        validateInputParameters(videoId, fileExtension);

        // 버킷 이름이 설정되어 있지 않으면 예외 발생
        if (videosMediaBucketName == null || videosMediaBucketName.isEmpty()) {
            log.error("S3 video media bucket name is not configured");
            throw new AwsBucketNameNotConfiguredException(
                ErrorCode.S3_BUCKET_NAME_NOT_CONFIGURED,
                "S3 video media bucket name is not configured."
            );
        }

        // 버킷 이름이 캐시에 없으면 S3에서 버킷 존재 여부 확인 후 캐싱
        ensureBucketExists(videosMediaBucketName);

        // S3 키 생성 (videoId.mp4)
        fileExtension = ensureDotPrefix(fileExtension);
        String s3Key = videoId + fileExtension;

        // S3 presigned URL 생성
        String presignedUrl = createPresignedUrl(s3Key, videosMediaBucketName);

        log.info("Successfully generated presigned URL for videoId: {}", videoId);
        return presignedUrl;
    }

    /**
     * 프로필 이미지를 S3에 업로드하고 CDN URL을 반환합니다.
     */
    public String uploadProfileImage(Long userId, MultipartFile profileImage) {
        log.info("Uploading profile image for userId: {}, fileName: {}",
            userId, profileImage.getOriginalFilename());

        // 입력값 검증
        validateProfileImageParameters(userId, profileImage);

        // 버킷 이름이 설정되어 있지 않으면 예외 발생
        if (profileImageBucketName == null || profileImageBucketName.isEmpty()) {
            log.error("S3 bucket name is not configured");
            throw new AwsBucketNameNotConfiguredException(
                ErrorCode.S3_BUCKET_NAME_NOT_CONFIGURED,
                "Profile image S3 bucket name is not configured."
            );
        }

        // 버킷 존재 여부 확인
        ensureBucketExists(profileImageBucketName);

        // 파일 확장자 추출
        String fileExtension = extractFileExtension(profileImage.getOriginalFilename());

        // S3 키 생성 (userId/userId-yyyy-MM-dd-HH-mm-ss-SSS.extension)
        String s3Key = generateProfileImageKey(userId, fileExtension);

        try {
            // S3에 파일 업로드
            uploadFileToS3(profileImageBucketName, s3Key, profileImage);

            // CDN URL 생성
            String cdnUrl = generateCdnUrl(s3Key);

            log.info("Successfully uploaded profile image for userId: {}, CDN URL: {}",
                userId, cdnUrl);

            return cdnUrl;

        } catch (IOException e) {
            log.error("Failed to read profile image file for userId: {}", userId, e);
            throw new BusinessException(
                ErrorCode.INTERNAL_ERROR, "Failed to read profile image file");
        }
    }

    private void validateProfileImageParameters(Long userId, MultipartFile profileImage) {
        if (userId == null) {
            log.error("UserId cannot be null");
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "UserId cannot be null");
        }

        if (profileImage == null || profileImage.isEmpty()) {
            log.error("Profile image file cannot be null or empty");
            throw new BusinessException(
                ErrorCode.INVALID_REQUEST, "Profile image file cannot be null or empty");
        }

        // 파일 크기 검증 (예: 5MB 제한)
        long maxFileSize = 5 * 1024 * 1024; // 5MB
        if (profileImage.getSize() > maxFileSize) {
            log.error("Profile image file size exceeds limit: {} bytes", profileImage.getSize());
            throw new BusinessException(
                ErrorCode.INVALID_REQUEST, "Profile image file size must be less than 5MB");
        }

        // Content Type 검증
        String contentType = profileImage.getContentType();
        final Set<String> allowedContentTypes = Set.of(
            "image/jpeg", "image/png", "image/jpg", "image/gif", "image/webp", "image/bmp");

        if (contentType == null || !allowedContentTypes.contains(contentType)) {
            log.error("Invalid content type for profile image: {}", contentType);
            throw new BusinessException(
                ErrorCode.INVALID_REQUEST,
                "Profile image must be an image file (JPEG, PNG, GIF, WEBP, BMP)"
            );
        }

        log.debug("Profile image parameters validated successfully");
    }

    private void ensureBucketExists(String bucketName) {
        if (existingBuckets.getIfPresent(bucketName) == null) {
            log.debug("Bucket '{}' not found in cache, checking existence", bucketName);
            if (doesBucketExist(bucketName)) {
                existingBuckets.put(bucketName, true);
                log.info("Bucket '{}' verified and cached", bucketName);
            } else {
                log.error("S3 bucket does not exist: {}", bucketName);
                throw new AwsBucketNotFoundException(
                    ErrorCode.S3_BUCKET_NOT_FOUND, "Bucket does not exist: " + bucketName);
            }
        } else {
            log.debug("Using cached bucket: {}", bucketName);
        }
    }

    private String extractFileExtension(String originalFileName) {
        if (originalFileName == null || !originalFileName.contains(".")) {
            throw new FileExtensionNotExistsException(
                ErrorCode.FILE_EXTENSION_NOT_EXISTS, "File extension is required.");
        }

        return originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    private String generateProfileImageKey(Long userId, String fileExtension) {
        String now = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS"));
        return String.format("%d/%d-%s%s", userId, userId, now, fileExtension);
    }

    private void uploadFileToS3(String bucketName, String s3Key, MultipartFile file)
        throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .contentType(file.getContentType())
            .contentLength(file.getSize())
            .build();

        RequestBody requestBody = RequestBody.fromInputStream(
            file.getInputStream(), file.getSize());

        s3Client.putObject(putObjectRequest, requestBody);
        log.debug("File uploaded to S3 bucket {} successfully: {}", bucketName, s3Key);
    }

    private String generateCdnUrl(String s3Key) {
        if (cloudfrontDomain == null || cloudfrontDomain.isEmpty()) {
            log.warn("CloudFront domain is not configured, returning S3 URL");
            throw new AwsCloudFrontDomainNotConfiguredException(
                ErrorCode.CLOUDFRONT_DOMAIN_NOT_CONFIGURED, "CloudFront domain is not configured."
            );
        }

        // CloudFront CDN URL 생성
        String cdnUrl = String.format("https://%s/%s", cloudfrontDomain, s3Key);
        log.debug("Generated CDN URL: {}", cdnUrl);
        return cdnUrl;
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

    private String createPresignedUrl(String s3Key, String bucketName) {
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