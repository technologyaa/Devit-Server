# 새로 추가된 기능 요약

## ✅ 구현 완료된 기능

### 1. 유저 조회 API

#### 엔드포인트
- **GET `/auth/me`** - 현재 로그인한 유저 조회
- **GET `/auth/members`** - 모든 유저 조회

#### 구현 파일
- `MemberService.java` - `getCurrentMember()`, `getAllMembers()` 메서드 추가
- `MemberResponse.java` - 유저 정보 응답 DTO 생성
- `MemberController.java` - 엔드포인트 추가

#### 사용 예시
```bash
# 현재 유저 조회
curl -X GET "http://localhost:8080/auth/me" \
  -H "Authorization: Bearer <token>"

# 모든 유저 조회
curl -X GET "http://localhost:8080/auth/members" \
  -H "Authorization: Bearer <token>"
```

---

### 2. 참가한 프로젝트 조회 API (프로젝트와 업무 포함)

#### 엔드포인트
- **GET `/projects/my-projects`** - 현재 유저가 참가한 프로젝트 목록 조회 (각 프로젝트의 업무 포함)

#### 구현 내용
- `Project` 엔티티에 `Member`와의 `@ManyToMany` 관계 추가
- `ProjectRepository`에 `findByMemberId()` 메서드 추가
- `ProjectService`에 `getMyProjects()` 메서드 추가
- `ProjectWithTasksResponse` DTO 생성 (프로젝트 정보 + 업무 목록)

#### 응답 형식
```json
{
  "data": [
    {
      "projectId": 1,
      "title": "프로젝트 제목",
      "content": "프로젝트 내용",
      "major": "BACKEND",
      "profile": "프로필 이미지 경로",
      "tasks": [
        {
          "taskId": 1,
          "title": "업무 제목",
          "description": "업무 설명",
          "status": "TODO"
        }
      ]
    }
  ]
}
```

#### 사용 예시
```bash
curl -X GET "http://localhost:8080/projects/my-projects" \
  -H "Authorization: Bearer <token>"
```

---

### 3. 채팅하고 있는 유저들 조회 API

#### 엔드포인트
- **GET `/chat/users`** - 현재 유저와 채팅한 유저들의 목록 조회

#### 구현 내용
- `ChatMessageRepository`에 `findDistinctSenders()` 메서드 추가
- `ChatUserService` 생성 (채팅한 유저 목록 조회 로직)
- `ChatUserController` 생성
- 현재 유저를 제외한 채팅한 유저 목록 반환

#### 응답 형식
```json
{
  "data": [
    {
      "id": 1,
      "username": "user1",
      "email": "user1@example.com",
      "profile": "프로필 이미지 경로"
    }
  ]
}
```

#### 사용 예시
```bash
curl -X GET "http://localhost:8080/chat/users" \
  -H "Authorization: Bearer <token>"
```

---

## 📋 데이터베이스 변경사항

### Project 엔티티 변경
- `Member`와의 `@ManyToMany` 관계 추가
- `project_member` 중간 테이블 자동 생성

```java
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "project_member",
    joinColumns = @JoinColumn(name = "project_id"),
    inverseJoinColumns = @JoinColumn(name = "member_id")
)
private Set<Member> members = new HashSet<>();
```

---

## 🔒 보안

모든 새로 추가된 엔드포인트는:
- `SecurityContextHolder`를 통해 현재 로그인한 유저 정보를 가져옴
- JWT 인증이 필요함 (SecurityConfig에서 `/auth/**`와 `/projects/**`는 permitAll이지만, 실제 로직에서 인증된 유저만 사용 가능)

---

## 🧪 테스트

### 테스트 스크립트
`test-new-features.sh` 파일을 생성했습니다. 사용법:

```bash
./test-new-features.sh <base_url> <access_token>
```

예시:
```bash
./test-new-features.sh http://localhost:8080 eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 컴파일 확인
✅ 모든 코드가 정상적으로 컴파일됨
✅ 린터 오류 없음

---

## 📝 Swagger UI

모든 엔드포인트는 Swagger UI에서 확인 가능합니다:
- `http://localhost:8080/swagger-ui/index.html`

---

## ⚠️ 주의사항

1. **프로젝트 참가 기능**: 프로젝트에 멤버를 추가하는 API는 별도로 구현해야 합니다. 현재는 조회 기능만 구현되어 있습니다.

2. **채팅 유저 조회**: `ChatMessage`의 `sender` 필드를 기반으로 조회하므로, 실제 채팅 메시지가 있어야 유저 목록이 반환됩니다.

3. **데이터베이스 마이그레이션**: `Project` 엔티티 변경으로 인해 `project_member` 테이블이 자동 생성됩니다. (`ddl-auto: update` 설정 시)

