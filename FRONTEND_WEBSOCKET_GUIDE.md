# 프로젝트 API 500 에러 디버깅 가이드

## 🔴 현재 발생 중인 문제

프로젝트 관련 API에서 **500 Internal Server Error**가 계속 발생하고 있습니다.

### 발생 위치
1. `GET /projects` - 프로젝트 목록 조회
2. `POST /projects` - 프로젝트 생성 (예상)

---

## 📋 프론트엔드에서 확인할 수 있는 정보

브라우저 콘솔에서 다음 정보를 확인할 수 있습니다:

### GET /projects 요청 정보
```
=== FETCH PROJECTS REQUEST ===
URL: https://devit.run/projects
Headers: {
  Accept: "application/json",
  Authorization: "Bearer {token}"
}
```

### 에러 응답 정보
```
Error status: 500
Error data: {
  status: 500,
  message: "...",
  data: { ... }
}
```

---

## 🔍 백엔드에서 확인해야 할 사항

### 1. 서버 로그 확인 (최우선)

**확인할 로그:**
- 애플리케이션 로그 (Spring Boot 로그)
- 데이터베이스 쿼리 로그
- 예외 스택 트레이스

**예상되는 에러:**
```
java.sql.SQLException: ...
org.springframework.dao.DataAccessException: ...
NullPointerException: ...
```

### 2. GET /projects 엔드포인트 확인

**확인 사항:**
- [ ] 컨트롤러 메서드가 올바르게 매핑되어 있는가?
- [ ] 인증/권한 체크가 올바른가?
- [ ] 데이터베이스 쿼리가 올바른가?
- [ ] 예외 처리가 되어 있는가?

**예상되는 문제:**
```java
// 1. 데이터베이스 연결 오류
@GetMapping("/projects")
public ResponseEntity<?> getProjects(@AuthenticationPrincipal UserDetails userDetails) {
    // DB 연결 실패 시 500 에러
}

// 2. NullPointerException
@GetMapping("/projects")
public ResponseEntity<?> getProjects(@AuthenticationPrincipal UserDetails userDetails) {
    Long userId = userDetails.getId(); // userDetails가 null일 수 있음
    // ...
}

// 3. SQL 쿼리 오류
@GetMapping("/projects")
public ResponseEntity<?> getProjects(@AuthenticationPrincipal UserDetails userDetails) {
    // 잘못된 컬럼명, 테이블명 등
    return projectRepository.findByUserId(userId); // 컬럼명 오류
}
```

### 3. 데이터베이스 확인

**확인 사항:**
- [ ] 프로젝트 테이블이 존재하는가?
- [ ] 테이블 이름이 정확한가? (`project`, `projects`, `Project` 등)
- [ ] 컬럼명이 정확한가?
  - `project_id` vs `id`
  - `user_id` vs `owner_id` vs `creator_id`
- [ ] 외래키 관계가 올바른가?
- [ ] 데이터베이스 연결이 정상인가?

**SQL 확인:**
```sql
-- 테이블 존재 확인
SHOW TABLES LIKE '%project%';

-- 테이블 구조 확인
DESCRIBE projects;
-- 또는
SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'projects';

-- 데이터 확인
SELECT * FROM projects LIMIT 10;
```

### 4. 인증/권한 확인

**확인 사항:**
- [ ] `@AuthenticationPrincipal`이 null이 아닌가?
- [ ] 토큰 검증이 올바른가?
- [ ] 사용자 ID 추출이 올바른가?

**예상되는 문제:**
```java
@GetMapping("/projects")
public ResponseEntity<?> getProjects(@AuthenticationPrincipal UserDetails userDetails) {
    // userDetails가 null이면 NullPointerException 발생
    Long userId = userDetails.getId(); // 500 에러 발생 가능
}
```

### 5. 응답 형식 확인

**현재 프론트엔드가 기대하는 형식:**
```json
// 옵션 1: 배열 직접 반환
[
  {
    "projectId": 1,
    "title": "프로젝트 이름",
    "content": "설명",
    "major": "BACKEND"
  }
]

// 옵션 2: 래핑된 형식
{
  "status": 200,
  "data": [
    {
      "projectId": 1,
      "title": "프로젝트 이름",
      "content": "설명",
      "major": "BACKEND"
    }
  ]
}
```

---

## 🛠️ 백엔드 수정 체크리스트

### GET /projects 엔드포인트

```java
@GetMapping("/projects")
public ResponseEntity<?> getProjects(
    @AuthenticationPrincipal UserDetails userDetails
) {
    try {
        // 1. 인증 확인
        if (userDetails == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse(401, "인증이 필요합니다."));
        }
        
        // 2. 사용자 ID 추출 (안전하게)
        Long userId = null;
        try {
            userId = Long.parseLong(userDetails.getUsername());
            // 또는 userDetails에서 직접 가져오는 방법
        } catch (Exception e) {
            return ResponseEntity.status(400)
                .body(new ErrorResponse(400, "유효하지 않은 사용자 정보입니다."));
        }
        
        // 3. 데이터베이스 조회
        List<Project> projects = projectService.getProjectsByUserId(userId);
        
        // 4. 응답 형식 통일
        return ResponseEntity.ok()
            .body(new ApiResponse<>(200, projects));
            
    } catch (DataAccessException e) {
        // 데이터베이스 오류
        log.error("Database error while fetching projects", e);
        return ResponseEntity.status(500)
            .body(new ErrorResponse(500, "데이터베이스 오류가 발생했습니다."));
            
    } catch (Exception e) {
        // 기타 예외
        log.error("Unexpected error while fetching projects", e);
        return ResponseEntity.status(500)
            .body(new ErrorResponse(500, "서버 내부 오류가 발생했습니다."));
    }
}
```

### POST /projects 엔드포인트

```java
@PostMapping("/projects")
public ResponseEntity<?> createProject(
    @RequestBody CreateProjectRequest request,
    @AuthenticationPrincipal UserDetails userDetails
) {
    try {
        // 1. 인증 확인
        if (userDetails == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse(401, "인증이 필요합니다."));
        }
        
        // 2. 요청 검증
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ResponseEntity.status(400)
                .body(new ErrorResponse(400, "프로젝트 이름을 입력해주세요."));
        }
        
        // 3. 사용자 ID 추출
        Long userId = Long.parseLong(userDetails.getUsername());
        
        // 4. 프로젝트 생성
        Project project = projectService.createProject(
            userId,
            request.getTitle(),
            request.getContent(),
            request.getMajor()
        );
        
        // 5. 응답
        return ResponseEntity.status(201)
            .body(new ApiResponse<>(201, project));
            
    } catch (DataAccessException e) {
        log.error("Database error while creating project", e);
        return ResponseEntity.status(500)
            .body(new ErrorResponse(500, "데이터베이스 오류가 발생했습니다."));
            
    } catch (Exception e) {
        log.error("Unexpected error while creating project", e);
        return ResponseEntity.status(500)
            .body(new ErrorResponse(500, "서버 내부 오류가 발생했습니다."));
    }
}
```

---

## 📝 에러 로그 예시

백엔드에서 확인해야 할 로그 형식:

```
ERROR [ProjectController] - Failed to fetch projects
java.sql.SQLException: Table 'database.projects' doesn't exist
    at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:129)
    at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:97)
    ...
```

또는

```
ERROR [ProjectController] - Failed to fetch projects
java.lang.NullPointerException
    at com.example.controller.ProjectController.getProjects(ProjectController.java:45)
    ...
```

---

## ✅ 해결 후 확인 사항

1. **프론트엔드 콘솔 확인**
   - 에러가 사라졌는지 확인
   - 프로젝트 목록이 정상적으로 표시되는지 확인

2. **네트워크 탭 확인**
   - `GET /projects` 요청이 200 OK로 응답하는지 확인
   - 응답 데이터 형식 확인

3. **다른 API와 일관성 확인**
   - `/developers`, `/profile` 등 다른 API와 응답 형식이 일치하는지 확인

---

## 🚨 긴급 확인 사항

백엔드 개발자가 **즉시 확인**해야 할 사항:

1. ✅ **서버 로그 파일 확인** - 가장 중요!
2. ✅ **데이터베이스 연결 상태 확인**
3. ✅ **프로젝트 테이블 존재 여부 확인**
4. ✅ **컨트롤러 메서드에 예외 처리 추가**
5. ✅ **@AuthenticationPrincipal null 체크 추가**

---

## 📞 추가 정보

프론트엔드에서 더 자세한 정보가 필요하면:
- 브라우저 개발자 도구 → Network 탭에서 요청/응답 확인
- 브라우저 콘솔에서 상세 에러 로그 확인
- 위의 "프론트엔드에서 확인할 수 있는 정보" 섹션 참고
