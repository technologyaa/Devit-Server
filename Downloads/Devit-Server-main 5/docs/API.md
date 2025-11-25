# Devit REST API

서버 기본 URL: `http://localhost:8080`  
모든 REST 엔드포인트는 `Content-Type: application/json`을 사용합니다.

## 인증 & 회원 (`/auth`)

### 회원가입
- `POST /auth/signup`
- 요청 예시
  ```json
  {
    "username": "devit",
    "email": "devit@example.com",
    "password": "Password123!"
  }
  ```
- 응답 (`ApiResponse`)
  ```json
  {
    "success": true,
    "status": 201,
    "message": "회원 가입이 완료되었습니다.",
    "data": {
      "memberId": 1,
      "username": "devit",
      "email": "devit@example.com"
    }
  }
  ```

### 로그인
- `POST /auth/signin`
- 요청 예시
  ```json
  {
    "email": "devit@example.com",
    "password": "Password123!"
  }
  ```
- 응답 예시
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "member": {
      "id": 1,
      "username": "devit",
      "email": "devit@example.com"
    }
  }
  ```
- 이후 보호된 API 호출 시 `Authorization: Bearer <accessToken>` 헤더를 사용합니다.

### 헬스 체크
- `GET /auth/test` → `"MemberController is working!"`

## 사용자 정보 (`/api/users`)

### 현재 로그인 사용자 조회
- `GET /api/users/me`
- 로그인 상태가 아니면
  ```json
  {
    "isAuthenticated": false
  }
  ```
- 로그인 상태라면
  ```json
  {
    "isAuthenticated": true,
    "id": 3,
    "email": "devit@example.com",
    "name": "Devit",
    "picture": "https://.../avatar.png",
    "provider": "google"
  }
  ```

## 홈 (`/api/home`)

### 바로가기 카드 조회
- `GET /api/home/quick-links`
- 응답(`ApiResponse<List<QuickLinkResponse>>`)
  ```json
  {
    "success": true,
    "status": 200,
    "message": "홈 바로가기 정보를 조회했습니다.",
    "data": [
      {
        "name": "프로젝트",
        "text": "새로운 프로젝트 시작",
        "url": "/projects",
        "logo": "/assets/white_folder.svg",
        "alt": "폴더 아이콘",
        "gradient": "linear-gradient(270deg, #9636EB 0.15%, #A651F5 99.87%)"
      },
      {
        "name": "개발자",
        "text": "개발자찾기",
        "url": "/offer/dev",
        "logo": "/assets/white-people.svg",
        "alt": "개발자 아이콘",
        "gradient": "linear-gradient(90deg, #C083FC 0%, #AA57F7 100%)"
      },
      {
        "name": "상점",
        "text": "코인 & 유료플랜",
        "url": "/shop",
        "logo": "/assets/white-shop.svg",
        "alt": "상점 아이콘",
        "gradient": "linear-gradient(90deg, #D6B0FF 0%, #BF86FC 100%)"
      }
    ]
  }
  ```

## 개발자 추천 (`/api/developers`)

### 추천 개발자 조회
- `GET /api/developers/recommended`
- 응답(`ApiResponse<List<DeveloperSummaryResponse>>`)
  ```json
  {
    "success": true,
    "status": 200,
    "message": "추천 개발자 목록을 조회했습니다.",
    "data": [
      {
        "name": "띠용인",
        "job": "iOS",
        "summary": "서비스 Devit의 홈 화면 디자인 작업 경험이 있습니다.",
        "email": "whattv@technologyaa.com",
        "profileImageUrl": "/assets/profile-icon.svg",
        "temperature": 36.5,
        "completedProjects": 2
      }
    ]
  }
  ```

## 프로필 (`/api/profile`)

### 기본 프로필 조회
- `GET /api/profile/me`
- 응답(`ApiResponse<ProfileResponse>`)
  ```json
  {
    "success": true,
    "status": 200,
    "message": "프로필 정보를 조회했습니다.",
    "data": {
      "name": "Devit",
      "role": "백엔드 개발자",
      "summary": "Devit 플랫폼 서버를 담당하고 있습니다.",
      "avatarUrl": "/assets/profile-icon.svg",
      "skills": ["Spring Boot", "MySQL", "WebSocket"]
    }
  }
  ```

## 프로젝트 (`/api/projects`)

### 전체 목록 조회
- `GET /api/projects`
- 응답(`ApiResponse<List<ProjectSummaryResponse>>`)

### 단일 조회
- `GET /api/projects/{projectId}`
- 응답(`ApiResponse<ProjectDetailResponse>`)

### 생성
- `POST /api/projects`
- 요청(`ProjectCreateRequest`)
  ```json
  {
    "title": "Devit",
    "description": "개발자와 기획자를 연결하는 Devit 플랫폼입니다.",
    "major": "WEB",
    "ownerName": "강장민",
    "thumbnailUrl": "/assets/dummy-thumbnail.svg",
    "creditBudget": 3000,
    "tasks": [
      {
        "title": "서비스 Devit의 홈 화면 디자인",
        "description": "홈 대시보드 UI 구현",
        "creditReward": 300,
        "sortOrder": 0
      }
    ]
  }
  ```
- 응답: 생성된 프로젝트 상세

### 수정
- `PUT /api/projects/{projectId}`
- 요청(`ProjectUpdateRequest`) → 제목, 설명, 예산 등 갱신

### 삭제
- `DELETE /api/projects/{projectId}`

### 업무 추가
- `POST /api/projects/{projectId}/tasks`
- 요청(`ProjectTaskCreateRequest`)

### 업무 상태 변경
- `POST /api/projects/{projectId}/tasks/{taskId}/status`
- 요청(`ProjectTaskStatusUpdateRequest`)  
  ```json
  {
    "done": true
  }
  ```

### 업무 삭제
- `DELETE /api/projects/{projectId}/tasks/{taskId}`

> 모든 프로젝트 관련 응답은 `ApiResponse` 래퍼를 사용하며 `data`에는 최신 프로젝트 정보가 포함되어 있습니다.

## WebSocket

- 엔드포인트: `ws://localhost:8080/ws/chat`
- 송신 메시지(JSON)
  ```json
  {
    "sender": "devit",
    "content": "안녕하세요!",
    "type": "TALK"
  }
  ```
- 서버는 메시지를 저장한 후 동일 구조로 브로드캐스트합니다. `type`은 `ENTER`, `TALK`, `LEAVE` 중 하나입니다.

## 이메일 인증 (비활성)

- `POST /email/send`, `POST /email/check` 엔드포인트가 정의되어 있으나 `EmailController`에 `@RestController` 어노테이션이 주석 처리되어 있어 현재는 사용되지 않습니다. 필요 시 주석을 해제하고 사용하세요.


