# WebSocket 및 채팅 API 문서

## 목차
1. [WebSocket 연결](#websocket-연결)
2. [인증 방식](#인증-방식)
3. [메시지 형식](#메시지-형식)
4. [REST API 엔드포인트](#rest-api-엔드포인트)
5. [사용 예시](#사용-예시)

---

## WebSocket 연결

### 엔드포인트

**WebSocket 경로**: `/ws/chat`

### 연결 URL 형식

#### 개발 환경 (HTTP)
```
ws://localhost:8080/ws/chat?username={사용자명}
```

#### 운영 환경 (HTTPS)
```
wss://devit.run/ws/chat?username={사용자명}
```

### 허용된 Origin
- `http://localhost:5173` (개발 환경)
- `https://devit.run` (운영 환경)

### 연결 예시 (JavaScript)

```javascript
const username = "user123"; // 실제 사용자명
const ws = new WebSocket(`ws://localhost:8080/ws/chat?username=${encodeURIComponent(username)}`);

ws.onopen = () => {
    console.log('WebSocket 연결 성공');
};

ws.onmessage = (event) => {
    const message = JSON.parse(event.data);
    console.log('메시지 수신:', message);
};

ws.onerror = (error) => {
    console.error('WebSocket 오류:', error);
};

ws.onclose = () => {
    console.log('WebSocket 연결 종료');
};
```

---

## 인증 방식

### 현재 구현

현재는 **JWT 인증 없이** 쿼리 파라미터로 `username`만 전달합니다.

- **인증 방법**: WebSocket 연결 URL의 쿼리 파라미터에 `username` 전달
- **보안**: `/ws/**` 경로는 `permitAll()` 설정으로 인증 없이 접근 가능
- **주의사항**: 운영 환경에서는 보안을 위해 JWT 기반 인증 추가 권장

### 사용자명 추출 방식

서버는 WebSocket 연결 시 URI 쿼리 파라미터에서 `username`을 추출합니다:

```
?username=사용자명
```

---

## 메시지 형식

### 전송 메시지 (클라이언트 → 서버)

WebSocket을 통해 전송하는 메시지는 JSON 형식입니다.

#### 필수 필드
- `content` (String, 필수): 메시지 내용 (최대 1000자)

#### 선택 필드
- `sender` (String, 선택): 발신자 사용자명 (쿼리 파라미터로 전달한 경우 생략 가능)
- `receiver` (String, 선택): 수신자 사용자명 (1:1 메시지인 경우)
- `roomId` (Long, 선택): 채팅방 ID (채팅방 메시지인 경우)
- `type` (String, 선택): 메시지 타입 (`ENTER`, `TALK`, `LEAVE`, 기본값: `TALK`)

#### 메시지 전송 규칙

1. **1:1 메시지**: `receiver` 필드에 수신자 사용자명 지정
2. **채팅방 메시지**: `roomId` 필드에 채팅방 ID 지정
3. **브로드캐스트**: `receiver`와 `roomId` 모두 없으면 모든 연결된 클라이언트에게 전송

#### JSON 스키마

```json
{
  "sender": "string (선택, 쿼리 파라미터로 전달 시 생략 가능)",
  "receiver": "string (선택, 1:1 메시지인 경우)",
  "content": "string (필수, 최대 1000자)",
  "roomId": "number (선택, 채팅방 메시지인 경우)",
  "type": "ENTER | TALK | LEAVE (선택, 기본값: TALK)"
}
```

### 수신 메시지 (서버 → 클라이언트)

서버에서 수신하는 메시지는 저장 후 다음 필드를 포함합니다:

```json
{
  "id": "number (메시지 ID)",
  "sender": "string (발신자 사용자명)",
  "receiver": "string | null (수신자 사용자명, 1:1 메시지인 경우)",
  "content": "string (메시지 내용)",
  "roomId": "number | null (채팅방 ID, 채팅방 메시지인 경우)",
  "type": "ENTER | TALK | LEAVE (메시지 타입)",
  "timestamp": "string (ISO 8601 형식의 타임스탬프)"
}
```

### 메시지 타입 (MessageType)

- `ENTER`: 채팅방 입장 메시지
- `TALK`: 일반 채팅 메시지 (기본값)
- `LEAVE`: 채팅방 퇴장 메시지

### 오류 메시지

메시지 형식이 올바르지 않거나 오류가 발생한 경우:

```json
{
  "error": "Invalid message format.",
  "details": "오류 상세 내용"
}
```

---

## REST API 엔드포인트

### Base URL
- 개발 환경: `http://localhost:8080`
- 운영 환경: `https://devit.run`

### 인증
- 채팅방 API (`/chat/rooms/**`): **인증 필요** (`authenticated()`)
- 채팅 메시지 API (`/chat/messages/**`): **인증 필요** (`authenticated()`)
- JWT 토큰을 `Authorization` 헤더에 포함해야 합니다: `Authorization: Bearer {token}`

### 채팅방 API

#### 1. 채팅방 생성
```http
POST /chat/rooms
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "name": "string (필수, 그룹 채팅방인 경우)",
  "description": "string (선택)",
  "type": "PRIVATE | GROUP",
  "memberIds": [1, 2, 3] // PRIVATE 타입: 1개 (상대방 ID), GROUP 타입: 여러 개 가능
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "채팅방 이름",
    "description": "채팅방 설명",
    "type": "PRIVATE | GROUP",
    "memberIds": [1, 2],
    "createdAt": "2024-01-01T00:00:00"
  },
  "message": "성공"
}
```

#### 2. 1:1 채팅방 조회/생성
```http
GET /chat/rooms/private/{memberId}
Authorization: Bearer {token}
```

기존 1:1 채팅방이 있으면 반환하고, 없으면 새로 생성합니다.

**Response:** 채팅방 생성 API와 동일

#### 3. 내 채팅방 목록 조회
```http
GET /chat/rooms/my-rooms
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "채팅방 이름",
      "description": "채팅방 설명",
      "type": "PRIVATE | GROUP",
      "memberIds": [1, 2],
      "createdAt": "2024-01-01T00:00:00"
    }
  ],
  "message": "성공"
}
```

#### 4. 채팅방 상세 조회
```http
GET /chat/rooms/{roomId}
Authorization: Bearer {token}
```

**Response:** 채팅방 생성 API와 동일

#### 5. 채팅방에 멤버 추가 (그룹 채팅방만 가능)
```http
POST /chat/rooms/{roomId}/members/{memberId}
Authorization: Bearer {token}
```

**Response:** 채팅방 생성 API와 동일

### 채팅 메시지 API

#### 1. 채팅방 메시지 조회
```http
GET /chat/messages/room/{roomId}?page=0&size=50
Authorization: Bearer {token}
```

**Query Parameters:**
- `page` (int, 기본값: 0): 페이지 번호
- `size` (int, 기본값: 50): 페이지 크기

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "sender": "user123",
      "receiver": null,
      "content": "메시지 내용",
      "roomId": 1,
      "type": "TALK",
      "timestamp": "2024-01-01T00:00:00"
    }
  ],
  "message": "성공"
}
```

#### 2. 1:1 채팅 메시지 조회
```http
GET /chat/messages/conversation/{username}
Authorization: Bearer {token}
```

**Response:** 채팅방 메시지 조회와 동일 (단, `roomId`가 null일 수 있음)

---

## 사용 예시

### 1. 1:1 채팅 구현

#### Step 1: 1:1 채팅방 생성/조회
```javascript
// REST API로 1:1 채팅방 생성/조회
const response = await fetch('http://localhost:8080/chat/rooms/private/2', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
const { data: room } = await response.json();
```

#### Step 2: WebSocket 연결
```javascript
const username = "user123";
const ws = new WebSocket(`ws://localhost:8080/ws/chat?username=${username}`);
```

#### Step 3: 메시지 전송 (1:1)
```javascript
const message = {
  receiver: "user456", // 상대방 사용자명
  content: "안녕하세요!",
  type: "TALK"
};
ws.send(JSON.stringify(message));
```

#### Step 4: 메시지 수신
```javascript
ws.onmessage = (event) => {
  const message = JSON.parse(event.data);
  if (message.receiver === username || message.sender === username) {
    // 1:1 메시지 처리
    console.log('1:1 메시지:', message);
  }
};
```

### 2. 채팅방 메시지 구현

#### Step 1: 채팅방 생성
```javascript
const response = await fetch('http://localhost:8080/chat/rooms', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({
    name: "그룹 채팅방",
    description: "설명",
    type: "GROUP",
    memberIds: [2, 3, 4]
  })
});
const { data: room } = await response.json();
```

#### Step 2: WebSocket 연결
```javascript
const username = "user123";
const ws = new WebSocket(`ws://localhost:8080/ws/chat?username=${username}`);
```

#### Step 3: 메시지 전송 (채팅방)
```javascript
const message = {
  roomId: 1, // 채팅방 ID
  content: "안녕하세요!",
  type: "TALK"
};
ws.send(JSON.stringify(message));
```

#### Step 4: 메시지 수신
```javascript
ws.onmessage = (event) => {
  const message = JSON.parse(event.data);
  if (message.roomId === currentRoomId) {
    // 채팅방 메시지 처리
    console.log('채팅방 메시지:', message);
  }
};
```

#### Step 5: 이전 메시지 조회
```javascript
const response = await fetch(`http://localhost:8080/chat/messages/room/${roomId}?page=0&size=50`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
const { data: messages } = await response.json();
```

---

## 주의사항

1. **보안**: 현재 WebSocket은 인증 없이 접근 가능합니다. 운영 환경에서는 JWT 기반 인증을 추가하는 것을 권장합니다.

2. **사용자명**: WebSocket 연결 시 쿼리 파라미터로 전달한 `username`과 메시지의 `sender`가 일치해야 합니다.

3. **채팅방 타입**:
   - `PRIVATE`: 1:1 채팅방 (최대 2명, 멤버 추가 불가)
   - `GROUP`: 그룹 채팅방 (여러 명 가능, 멤버 추가 가능)

4. **메시지 전송 우선순위**:
   - `receiver`가 있으면 → 1:1 메시지
   - `roomId`가 있으면 → 채팅방 메시지
   - 둘 다 없으면 → 브로드캐스트

5. **CORS**: 허용된 Origin에서만 접근 가능합니다.

---

## Swagger 문서

Swagger UI에서 모든 API 문서를 확인할 수 있습니다:

- 개발 환경: `http://localhost:8080/swagger-ui/index.html`
- 운영 환경: `https://devit.run/swagger-ui/index.html`

