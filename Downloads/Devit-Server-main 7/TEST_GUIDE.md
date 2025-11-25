# 업무 및 파일 관리 API 테스트 가이드

## 서버 실행 전 확인사항

애플리케이션을 실행하기 전에 다음 환경 변수를 설정해야 합니다:

```bash
export DB_URL=jdbc:mysql://localhost:3306/your_database
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_key
export JWT_ACCESS_TOKEN_EXPIRATION=3600000
export JWT_REFRESH_TOKEN_EXPIRATION=86400000
export MAIL_USERNAME=your_email
export MAIL_PASSWORD=your_email_password
export REDIS_HOST=localhost
export REDIS_PORT=6379
export GOOGLE_CLIENT_ID=your_google_client_id
export GOOGLE_CLIENT_SECRET=your_google_client_secret
export SERVER_PORT=8080
export FILE_UPLOAD_DIR=./uploads
```

## 서버 실행

```bash
./gradlew bootRun
```

또는

```bash
java -jar build/libs/Devit-0.0.1-SNAPSHOT.jar
```

## API 테스트

### 1. 프로젝트 생성
```bash
curl -X POST http://localhost:8080/projects \
  -H "Content-Type: application/json" \
  -d '{
    "title": "테스트 프로젝트",
    "content": "테스트 내용",
    "major": "BACKEND"
  }'
```

### 2. 업무 생성
```bash
curl -X POST http://localhost:8080/projects/{projectId}/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "테스트 업무",
    "description": "업무 설명",
    "status": "TODO"
  }'
```

### 3. 파일 업로드
```bash
curl -X POST http://localhost:8080/projects/{projectId}/tasks/{taskId}/files \
  -F "file=@your-file.txt"
```

### 4. 파일 목록 조회
```bash
curl -X GET http://localhost:8080/projects/{projectId}/tasks/{taskId}/files
```

### 5. 파일 다운로드
```bash
curl -X GET http://localhost:8080/projects/{projectId}/tasks/{taskId}/files/{fileId} \
  -o downloaded-file.txt
```

### 6. 파일 삭제
```bash
curl -X DELETE http://localhost:8080/projects/{projectId}/tasks/{taskId}/files/{fileId}
```

### 7. 업무 수정
```bash
curl -X PUT http://localhost:8080/projects/{projectId}/tasks/{taskId} \
  -H "Content-Type: application/json" \
  -d '{
    "title": "수정된 업무",
    "description": "수정된 설명",
    "status": "IN_PROGRESS"
  }'
```

### 8. 업무 삭제
```bash
curl -X DELETE http://localhost:8080/projects/{projectId}/tasks/{taskId}
```

## 자동 테스트 스크립트 실행

서버가 실행된 후:

```bash
./test-task-file-api.sh
```

또는

```bash
./quick-test.sh
```

## Swagger UI 사용

서버 실행 후 브라우저에서 접속:
```
http://localhost:8080/swagger-ui.html
```

Swagger UI에서 모든 API를 테스트할 수 있습니다.

## 구현된 기능 요약

✅ **업무 CRUD**
- 생성, 조회(전체/단일), 수정, 삭제

✅ **파일 관리**
- 업로드, 목록 조회, 다운로드, 삭제

✅ **추가 기능**
- UUID 기반 고유 파일명
- 원본 파일명 저장 및 반환
- 파일 시스템과 DB 동기화


