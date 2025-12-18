# 프론트엔드 WebSocket 메시지 수신 가이드

## 1. WebSocket 연결 설정

### 연결 URL 형식
```
ws://localhost:8080/ws/chat?username=사용자명
```
또는
```
wss://devit.run/ws/chat?username=사용자명
```

**중요**: `?username=사용자명` 쿼리 파라미터를 반드시 포함해야 합니다. 이는 서버가 사용자 세션을 추적하기 위해 필요합니다.

### 연결 예제 (JavaScript)
```javascript
const username = 'user123'; // 로그인한 사용자명
const wsUrl = `ws://localhost:8080/ws/chat?username=${encodeURIComponent(username)}`;
const websocket = new WebSocket(wsUrl);
```

## 2. 메시지 수신 처리 (onmessage)

서버에서 전송하는 메시지는 다음 JSON 형식입니다:

```json
{
  "id": 1,
  "sender": "user123",
  "receiver": null,
  "content": "안녕하세요!",
  "roomId": 3,
  "type": "TALK",
  "timestamp": "2024-12-18T14:30:00"
}
```

### 필드 설명
- `id`: 메시지 ID (Long)
- `sender`: 발신자 사용자명 (String, 필수)
- `receiver`: 수신자 사용자명 (String, null 가능 - 채팅방 메시지는 null)
- `content`: 메시지 내용 (String, 필수)
- `roomId`: 채팅방 ID (Long, null 가능 - 1:1 메시지는 null)
- `type`: 메시지 타입 (String) - `ENTER`, `TALK`, `LEAVE`
- `timestamp`: 메시지 생성 시간 (String, ISO 8601 형식)

### 메시지 타입
- `ENTER`: 사용자가 채팅방에 입장했을 때
- `TALK`: 일반 대화 메시지
- `LEAVE`: 사용자가 채팅방에서 나갔을 때

## 3. 구현 예제

### React 예제
```typescript
import { useEffect, useRef, useState } from 'react';

interface ChatMessage {
  id: number;
  sender: string;
  receiver: string | null;
  content: string;
  roomId: number | null;
  type: 'ENTER' | 'TALK' | 'LEAVE';
  timestamp: string;
}

function ChatComponent({ username, roomId }: { username: string; roomId: number }) {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const wsRef = useRef<WebSocket | null>(null);

  useEffect(() => {
    // WebSocket 연결
    const wsUrl = `ws://localhost:8080/ws/chat?username=${encodeURIComponent(username)}`;
    const ws = new WebSocket(wsUrl);
    wsRef.current = ws;

    // 연결 성공
    ws.onopen = () => {
      console.log('WebSocket 연결 성공');
    };

    // 메시지 수신
    ws.onmessage = (event: MessageEvent) => {
      try {
        const message: ChatMessage = JSON.parse(event.data);
        
        // 오류 메시지 처리
        if ('error' in message) {
          console.error('서버 오류:', message.error);
          return;
        }

        // 채팅방 메시지인 경우에만 처리 (roomId가 일치하는 경우)
        if (message.roomId === roomId) {
          setMessages(prev => [...prev, message]);
        }
      } catch (error) {
        console.error('메시지 파싱 오류:', error);
      }
    };

    // 연결 종료
    ws.onclose = () => {
      console.log('WebSocket 연결 종료');
    };

    // 오류 처리
    ws.onerror = (error) => {
      console.error('WebSocket 오류:', error);
    };

    // 정리 함수
    return () => {
      ws.close();
    };
  }, [username, roomId]);

  // 메시지 전송 함수
  const sendMessage = (content: string) => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      const message = {
        sender: username,
        content: content,
        roomId: roomId,
        type: 'TALK' as const
      };
      wsRef.current.send(JSON.stringify(message));
    }
  };

  return (
    <div>
      {/* 메시지 목록 렌더링 */}
      {messages.map(msg => (
        <div key={msg.id}>
          <strong>{msg.sender}</strong>: {msg.content}
        </div>
      ))}
      
      {/* 메시지 입력 및 전송 UI */}
      {/* ... */}
    </div>
  );
}
```

### Vanilla JavaScript 예제
```javascript
// WebSocket 연결 설정
const username = 'user123'; // 실제 사용자명으로 변경
const roomId = 3; // 실제 채팅방 ID로 변경

const wsUrl = `ws://localhost:8080/ws/chat?username=${encodeURIComponent(username)}`;
const websocket = new WebSocket(wsUrl);

// 메시지 수신 처리
websocket.onmessage = function(event) {
  const payload = event.data;
  console.log('📨 메시지 수신:', payload);

  try {
    // JSON 문자열을 객체로 변환
    const message = JSON.parse(payload);

    // 오류 메시지 처리
    if (message.error) {
      console.error('서버 오류:', message.error);
      alert(`오류: ${message.error}`);
      return;
    }

    // ChatMessage 객체 구조
    // {
    //   id: 1,
    //   sender: "user123",
    //   receiver: null,
    //   content: "안녕하세요!",
    //   roomId: 3,
    //   type: "TALK",
    //   timestamp: "2024-12-18T14:30:00"
    // }

    // 채팅방 메시지인 경우에만 처리
    if (message.roomId === roomId) {
      displayMessage(message);
    } else {
      console.log('다른 채팅방 메시지입니다. 무시합니다.');
    }

  } catch (error) {
    console.error('❌ 수신 메시지 파싱 오류:', error);
    console.error('문제가 된 원본 데이터:', payload);
  }
};

// 메시지를 화면에 표시하는 함수
function displayMessage(message) {
  const chatContainer = document.getElementById('chat-messages');
  
  const messageDiv = document.createElement('div');
  messageDiv.className = 'message';
  
  // 메시지 타입에 따른 처리
  if (message.type === 'ENTER' || message.type === 'LEAVE') {
    messageDiv.className += ' system-message';
    messageDiv.textContent = `${message.sender}님이 ${message.type === 'ENTER' ? '입장' : '퇴장'}하셨습니다.`;
  } else {
    messageDiv.className += ' chat-message';
    const isMyMessage = message.sender === username;
    messageDiv.className += isMyMessage ? ' my-message' : ' other-message';
    
    messageDiv.innerHTML = `
      <div class="message-sender">${message.sender}</div>
      <div class="message-content">${message.content}</div>
      <div class="message-time">${formatTimestamp(message.timestamp)}</div>
    `;
  }
  
  chatContainer.appendChild(messageDiv);
  chatContainer.scrollTop = chatContainer.scrollHeight; // 자동 스크롤
}

// 타임스탬프 포맷팅 함수
function formatTimestamp(timestamp) {
  const date = new Date(timestamp);
  return date.toLocaleTimeString('ko-KR', { 
    hour: '2-digit', 
    minute: '2-digit' 
  });
}

// 연결 성공
websocket.onopen = function() {
  console.log('✅ WebSocket 연결 성공');
};

// 연결 종료
websocket.onclose = function(event) {
  console.log('❌ WebSocket 연결 종료:', event.code, event.reason);
};

// 오류 발생
websocket.onerror = function(error) {
  console.error('❌ WebSocket 오류:', error);
};

// 메시지 전송 함수
function sendMessage(content) {
  if (websocket.readyState === WebSocket.OPEN) {
    const message = {
      sender: username,
      content: content,
      roomId: roomId,
      type: 'TALK'
    };
    websocket.send(JSON.stringify(message));
  } else {
    console.error('WebSocket이 열려있지 않습니다.');
  }
}
```

## 4. 메시지 전송 형식

### 채팅방 메시지 전송
```javascript
const message = {
  sender: "user123",    // 발신자 사용자명 (필수)
  content: "안녕하세요!", // 메시지 내용 (필수)
  roomId: 3,            // 채팅방 ID (필수)
  type: "TALK"          // 메시지 타입 (기본값: "TALK")
};

websocket.send(JSON.stringify(message));
```

### 1:1 메시지 전송 (선택사항)
```javascript
const message = {
  sender: "user123",
  content: "안녕하세요!",
  receiver: "user456",  // 수신자 사용자명
  type: "TALK"
  // roomId는 없음
};

websocket.send(JSON.stringify(message));
```

## 5. 주의사항

### 1. username 쿼리 파라미터 필수
- WebSocket 연결 시 `?username=사용자명` 쿼리 파라미터를 반드시 포함해야 합니다.
- 없으면 서버가 사용자 세션을 추적할 수 없어 메시지가 전달되지 않습니다.

### 2. sender와 username 일치
- 메시지의 `sender` 필드가 WebSocket 연결 시 사용한 `username`과 일치해야 합니다.
- 일치하지 않으면 메시지 전송은 되지만, 다른 사용자에게 메시지가 전달되지 않을 수 있습니다.

### 3. roomId 확인
- 수신한 메시지의 `roomId`를 확인하여 현재 채팅방 메시지만 처리하세요.
- 다른 채팅방의 메시지는 무시해야 합니다.

### 4. 연결 상태 확인
- 메시지 전송 전에 `websocket.readyState === WebSocket.OPEN`을 확인하세요.
- 연결이 열려있지 않으면 메시지가 전송되지 않습니다.

### 5. 에러 처리
- 서버에서 오류가 발생하면 `{ error: "오류 메시지", details: "상세 정보" }` 형식으로 반환됩니다.
- `onmessage` 핸들러에서 오류를 처리하세요.

## 6. 디버깅 팁

### 서버 로그 확인
서버 로그에서 다음 메시지를 확인할 수 있습니다:
- `"새 세션 연결됨: ... 사용자: XXX"` → 연결 성공
- `"메시지 전송 시작. roomId: X, sender: Y"` → 메시지 전송 시작
- `"사용자 'XXX'에 대한 세션을 찾을 수 없음"` → username 매칭 실패
- `"메시지 전송 완료. 성공: N"` → 전송 성공

### 클라이언트 콘솔 확인
- 브라우저 개발자 도구 콘솔에서 WebSocket 메시지를 확인할 수 있습니다.
- `console.log`를 사용하여 수신한 메시지를 확인하세요.



