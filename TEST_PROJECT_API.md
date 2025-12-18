# 프로젝트 API 테스트 가이드

## 📋 테스트 전 준비사항

1. **서버 실행 확인**
   ```bash
   # 서버가 실행 중인지 확인
   curl http://localhost:8080/projects
   ```

2. **서버 재시작 (코드 변경 후 필수)**
   - 코드를 수정했으므로 서버를 재시작해야 합니다
   - IDE에서 서버를 재시작하거나
   - `./gradlew bootRun` 명령으로 재시작

3. **데이터베이스 연결 확인**
   - `application.yml`의 데이터베이스 설정 확인
   - 데이터베이스가 실행 중인지 확인

## 🧪 테스트 실행 방법

### 방법 1: 테스트 스크립트 사용 (권장)

```bash
cd Devit-Server
./test-project-api.sh
```

### 방법 2: 수동 테스트

#### 1. 프로젝트 목록 조회
```bash
curl -X GET http://localhost:8080/projects \
  -H "Content-Type: application/json" | jq '.'
```

#### 2. 프로젝트 생성
```bash
curl -X POST http://localhost:8080/projects \
  -H "Content-Type: application/json" \
  -d '{
    "title": "테스트 프로젝트",
    "content": "프로젝트 설명"
  }'
```

#### 3. 프로젝트 상세 조회
```bash
# 위에서 생성한 프로젝트 ID를 사용
PROJECT_ID=1
curl -X GET http://localhost:8080/projects/$PROJECT_ID \
  -H "Content-Type: application/json" | jq '.'
```

#### 4. 업무 목록 조회
```bash
curl -X GET http://localhost:8080/projects/$PROJECT_ID/tasks \
  -H "Content-Type: application/json" | jq '.'
```

#### 5. 업무 생성
```bash
curl -X POST http://localhost:8080/projects/$PROJECT_ID/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "테스트 업무",
    "description": "업무 설명"
  }' | jq '.'
```

#### 6. 업무 수정
```bash
TASK_ID=1
curl -X PUT http://localhost:8080/projects/$PROJECT_ID/tasks/$TASK_ID \
  -H "Content-Type: application/json" \
  -d '{
    "title": "수정된 업무",
    "description": "수정된 설명",
    "status": "DONE"
  }' | jq '.'
```

#### 7. 업무 삭제
```bash
curl -X DELETE http://localhost:8080/projects/$PROJECT_ID/tasks/$TASK_ID
```

## ✅ 예상 응답 형식

### GET /projects/{projectId}
```json
{
  "projectId": 1,
  "title": "프로젝트 이름",
  "content": "프로젝트 설명",
  "thumbnail": "/uploads/project-thumbnail.jpg",
  "major": "BACKEND",
  "owner": "사용자 이름",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  ...
}
```

### GET /projects/{projectId}/tasks
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

### POST /projects/{projectId}/tasks
```json
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
```

## 🔍 문제 해결

### 500 Internal Server Error
- 서버 로그 확인
- 데이터베이스 연결 확인
- 엔티티 필드와 데이터베이스 스키마 일치 확인

### 404 Not Found
- 프로젝트 ID가 존재하는지 확인
- URL 경로 확인

### 인증 오류
- `/projects/**`는 `permitAll()`로 설정되어 있어 인증이 필요 없습니다
- 다른 엔드포인트는 인증이 필요할 수 있습니다

## 📝 참고사항

- 모든 API는 인증 없이 접근 가능합니다 (`/projects/**`는 `permitAll()`)
- `major` 필드는 author가 Developer로 등록되어 있을 때만 값이 있습니다
- `isDone` 필드는 `status`가 `DONE`일 때 `true`입니다
- `updatedAt` 필드는 기존 데이터의 경우 `createdAt`과 동일할 수 있습니다




