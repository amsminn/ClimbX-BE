# ClimbX Backend

## 필수 요구사항

- Java 21
- Gradle 8.x
- Docker 및 Docker Compose

## 환경 변수 설정

```properties
# JWT 설정
JWT_SECRET=your-jwt-secret-key
# 데이터베이스 설정
DB_USER=your-db-username
DB_PASSWORD=your-db-password
# AWS S3 설정
AWS_ACCESS_KEY_ID=your-aws-access-key
AWS_SECRET_ACCESS_KEY=your-aws-secret-key
AWS_S3_VIDEOS_MEDIA_BUCKET_NAME=your-s3-bucket-name
AWS_S3_PROFILE_IMAGE_BUCKET_NAME=your-s3-profile-image-bucket-name
AWS_S3_PROBLEM_IMAGE_BUCKET_NAME=your-s3-problem-image-bucket-name
AWS_CLOUDFRONT_DOMAIN=your-cloudfront-domain
AWS_S3_PRESIGNED_URL_EXPIRATION=180
# OAuth2 설정
KAKAO_NATIVE_APP_KEY=your-kakao-app-key
GOOGLE_ANDROID_APP_KEY=your-google-android-key
GOOGLE_IOS_APP_KEY=your-google-ios-key
APPLE_ANDROID_APP_KEY=your-apple-android-key
APPLE_IOS_APP_KEY=your-apple-ios-key
```

## 데이터베이스 실행

```bash
# MySQL Docker 컨테이너 시작
cd docker/dev/mysql
docker-compose up -d
```

## 애플리케이션 실행

```bash
# Gradle로 실행
./gradlew bootRun
```

## 확인

- 애플리케이션: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

## 테스트

```bash
# 전체 테스트 실행
./gradlew test
```
