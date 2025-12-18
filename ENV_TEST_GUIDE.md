# 환경변수로 프로젝트 API 테스트 가이드

## 현재 상태
❌ **500 Internal Server Error 발생 중**

서버가 재시작되지 않아 변경사항이 반영되지 않았거나, 다른 문제가 있을 수 있습니다.

## 환경변수 설정 및 테스트 방법

### 1. 환경변수 설정

```bash
# 필수 환경변수
export DB_URL=jdbc:mysql://localhost:3306/devit
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_key
export SERVER_PORT=8080

# 선택적 환경변수
export REDIS_HOST=localhost
export REDIS_PORT=6379
export MAIL_USERNAME=your_email
export MAIL_PASSWORD=your_password
export GOOGLE_CLIENT_ID=your_google_client_id
export GOOGLE_CLIENT_SECRET=your_google_client_secret
```

### 2. 서버 실행

```bash
cd Devit-Server
./gradlew bootRun
```

### 3. 테스트 실행

다른 터미널에서:

```bash
cd Devit-Server
./test-with-env.sh
```

또는 수동으로:

```bash
# 1. 프로젝트 목록 조회
curl http://localhost:8080/projects

# 2. 프로젝트 생성
curl -X POST http://localhost:8080/projects \
  -H "Content-Type: application/json" \
  -d '{"title":"테스트","content":"테스트 내용"}'

# 3. 프로젝트 상세 조회 (위에서 생성한 프로젝트 ID 사용)
curl http://localhost:8080/projects/1

# 4. 업무 목록 조회
curl http://localhost:8080/projects/1/tasks

# 5. 업무 생성
curl -X POST http://localhost:8080/projects/1/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"테스트 업무","description":"업무 설명"}'
```

## 문제 해결

### 500 에러가 계속 발생하는 경우

1. **서버 재시작 확인**
   - 코드를 수정했으므로 반드시 서버를 재시작해야 합니다
   - IDE에서 서버를 재시작하거나 `./gradlew bootRun` 실행

2. **서버 로그 확인**
   - 서버 로그에서 실제 에러 메시지 확인
   - `GlobalExceptionHandler`의 로그에서 스택 트레이스 확인

3. **데이터베이스 확인**
   - 데이터베이스가 실행 중인지 확인
   - 연결 정보가 올바른지 확인
   - 스키마가 업데이트되었는지 확인 (`ddl-auto: update`)

4. **환경변수 확인**
   - 모든 필수 환경변수가 설정되었는지 확인
   - 값이 올바른지 확인

## 예상 성공 응답

### GET /projects
```json
[
  {
    "projectId": 1,
    "title": "프로젝트 이름",
    "content": "프로젝트 설명",
    "thumbnail": "/uploads/...",
    "major": "BACKEND",
    "owner": "사용자 이름",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
]
```

### GET /projects/{id}
```json
{
  "projectId": 1,
  "title": "프로젝트 이름",
  "content": "프로젝트 설명",
  "thumbnail": "/uploads/...",
  "major": "BACKEND",
  "owner": "사용자 이름",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### GET /projects/{id}/tasks
```json
[
  {
    "taskId": 1,
    "title": "업무 이름",
    "description": "업무 설명",
    "isDone": false,
    "projectId": 1,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "status": "TODO"
  }
]
```

