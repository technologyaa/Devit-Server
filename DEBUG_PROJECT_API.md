# 프로젝트 API 디버깅 가이드

## 현재 상태
❌ **500 Internal Server Error 발생 중**

## 문제 해결 단계

### 1. 서버 재시작 (필수)
코드를 수정했으므로 **반드시 서버를 재시작**해야 합니다.

```bash
# 서버 중지 후 재시작
./gradlew bootRun
```

### 2. 서버 로그 확인
서버 로그에서 실제 에러 메시지를 확인하세요:
- `GlobalExceptionHandler`에서 `log.error("Unexpected error occurred: ", e);`로 로그가 기록됩니다
- 스택 트레이스를 확인하면 정확한 원인을 알 수 있습니다

### 3. 데이터베이스 스키마 확인
새로 추가한 `updatedAt` 컬럼이 데이터베이스에 있는지 확인:

```sql
-- MySQL에서 확인
DESCRIBE project;
DESCRIBE task;
```

만약 컬럼이 없다면:
- `ddl-auto: update` 설정이면 자동으로 추가되어야 합니다
- 수동으로 추가하거나 서버를 재시작하면 자동 생성됩니다

### 4. 간단한 테스트

서버 재시작 후:

```bash
# 1. 프로젝트 목록 조회
curl http://localhost:8080/projects

# 2. 프로젝트 생성 (인증 필요할 수 있음)
curl -X POST http://localhost:8080/projects \
  -H "Content-Type: application/json" \
  -d '{"title":"테스트","content":"테스트"}'
```

### 5. 가능한 원인들

1. **데이터베이스 연결 문제**
   - `application.yml`의 데이터베이스 설정 확인
   - 데이터베이스가 실행 중인지 확인

2. **엔티티 필드 불일치**
   - `Project` 엔티티의 `updatedAt` 필드
   - `Task` 엔티티의 `createdAt`, `updatedAt` 필드

3. **LazyInitializationException**
   - `findAllWithAuthor()` 쿼리에서 `JOIN FETCH`를 사용하므로 문제 없어야 함
   - 하지만 다른 곳에서 발생할 수 있음

4. **NullPointerException**
   - `ProjectResponse.from()`에서 null 체크 추가했지만 다른 곳에서 발생할 수 있음

## 해결 후 확인사항

서버 재시작 후 다음을 확인하세요:

✅ `GET /projects` - 200 OK
✅ `GET /projects/{id}` - 200 OK (프로젝트가 있는 경우)
✅ `GET /projects/{id}/tasks` - 200 OK (빈 배열 또는 업무 목록)
✅ `POST /projects/{id}/tasks` - 201 Created

## 추가 디버깅

더 자세한 로그를 보려면 `application.yml`에 다음을 추가:

```yaml
logging:
  level:
    technologyaa.Devit.domain.project: DEBUG
    org.hibernate.SQL: DEBUG
```




