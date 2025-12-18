# 프론트엔드 채팅방 메시지 조회 가이드

## API 엔드포인트

### 채팅방 메시지 조회
```
GET /chat/messages/room/{roomId}
```

**파라미터:**
- `roomId` (path): 채팅방 ID (필수)
- `page` (query, 선택): 페이지 번호 (기본값: 0)
- `size` (query, 선택): 페이지 크기 (기본값: 50)

**인증:** 
- JWT 토큰 필요 (Authorization 헤더)
- 요청한 사용자가 해당 채팅방의 멤버여야 함

**응답 형식:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "sender": "username",
      "content": "메시지 내용",
      "roomId": 1,
      "type": "TALK",
      "timestamp": "2024-12-18T12:00:00"
    }
  ]
}
```

## 프론트엔드 구현 예제

### React/TypeScript 예제

```typescript
// types/chat.ts
export interface ChatMessage {
  id?: number;
  sender: string;
  content: string;
  roomId?: number | null;
  receiver?: string | null;
  type: 'ENTER' | 'TALK' | 'LEAVE';
  timestamp: string;
}

export interface APIResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}

// services/chatService.ts
import { APIResponse, ChatMessage } from '../types/chat';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const chatMessageService = {
  /**
   * 채팅방 메시지 조회
   * @param roomId 채팅방 ID
   * @param page 페이지 번호 (기본값: 0)
   * @param size 페이지 크기 (기본값: 50)
   * @returns 메시지 목록
   */
  async getRoomMessages(
    roomId: number,
    page: number = 0,
    size: number = 50
  ): Promise<ChatMessage[]> {
    const token = localStorage.getItem('accessToken'); // 또는 사용하는 인증 방식에 맞게
    
    const response = await fetch(
      `${API_BASE_URL}/chat/messages/room/${roomId}?page=${page}&size=${size}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`, // JWT 토큰 사용 시
        },
        credentials: 'include', // 쿠키 사용 시
      }
    );

    if (!response.ok) {
      throw new Error(`메시지 조회 실패: ${response.statusText}`);
    }

    const result: APIResponse<ChatMessage[]> = await response.json();
    return result.data;
  },
};
```

### React 컴포넌트 예제

```typescript
// components/ChatRoom.tsx
import React, { useEffect, useState } from 'react';
import { chatMessageService } from '../services/chatService';
import { ChatMessage } from '../types/chat';

interface ChatRoomProps {
  roomId: number;
}

export const ChatRoom: React.FC<ChatRoomProps> = ({ roomId }) => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 채팅방 메시지 로드
  const loadMessages = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const loadedMessages = await chatMessageService.getRoomMessages(roomId);
      setMessages(loadedMessages);
    } catch (err) {
      setError(err instanceof Error ? err.message : '메시지를 불러올 수 없습니다.');
      console.error('메시지 조회 오류:', err);
    } finally {
      setLoading(false);
    }
  };

  // 컴포넌트 마운트 시 또는 roomId 변경 시 메시지 로드
  useEffect(() => {
    if (roomId) {
      loadMessages();
    }
  }, [roomId]);

  // 새로고침 버튼
  const handleRefresh = () => {
    loadMessages();
  };

  return (
    <div className="chat-room">
      <div className="chat-header">
        <h2>채팅방 #{roomId}</h2>
        <button onClick={handleRefresh} disabled={loading}>
          {loading ? '로딩 중...' : '새로고침'}
        </button>
      </div>

      {error && <div className="error">{error}</div>}

      <div className="chat-messages">
        {messages.map((message) => (
          <div key={message.id} className="message">
            <div className="message-sender">{message.sender}</div>
            <div className="message-content">{message.content}</div>
            <div className="message-time">
              {new Date(message.timestamp).toLocaleString()}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
```

### JavaScript/Vanilla JS 예제

```javascript
// chatService.js
const API_BASE_URL = 'http://localhost:8080';

/**
 * 채팅방 메시지 조회
 * @param {number} roomId - 채팅방 ID
 * @param {number} page - 페이지 번호 (기본값: 0)
 * @param {number} size - 페이지 크기 (기본값: 50)
 * @returns {Promise<Array>} 메시지 목록
 */
async function getRoomMessages(roomId, page = 0, size = 50) {
  const token = localStorage.getItem('accessToken'); // 또는 사용하는 인증 방식에 맞게

  const response = await fetch(
    `${API_BASE_URL}/chat/messages/room/${roomId}?page=${page}&size=${size}`,
    {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`, // JWT 토큰 사용 시
      },
      credentials: 'include', // 쿠키 사용 시
    }
  );

  if (!response.ok) {
    throw new Error(`메시지 조회 실패: ${response.statusText}`);
  }

  const result = await response.json();
  return result.data; // result.data는 메시지 배열
}

// 사용 예제
async function loadChatMessages(roomId) {
  try {
    const messages = await getRoomMessages(roomId);
    console.log('로드된 메시지:', messages);
    
    // 메시지를 화면에 표시
    messages.forEach(message => {
      console.log(`${message.sender}: ${message.content}`);
      console.log(`시간: ${new Date(message.timestamp).toLocaleString()}`);
    });
  } catch (error) {
    console.error('메시지 로드 오류:', error);
  }
}

// 페이지 로드 시 또는 채팅방 입장 시 호출
// loadChatMessages(1); // roomId가 1인 채팅방
```

### Axios를 사용하는 경우

```typescript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  withCredentials: true, // 쿠키 자동 포함
});

// 요청 인터셉터로 토큰 추가
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 채팅방 메시지 조회
export const getRoomMessages = async (
  roomId: number,
  page: number = 0,
  size: number = 50
) => {
  const response = await fetch(
    `/chat/messages/room/${roomId}?page=${page}&size=${size}`
  );
  return response.data.data; // APIResponse의 data 필드
};
```

## WebSocket과 함께 사용하는 방법

1. **페이지 로드 시**: REST API로 기존 메시지 조회
2. **WebSocket 연결 후**: 실시간으로 새 메시지 수신

```typescript
// 컴포넌트에서
useEffect(() => {
  // 1. 기존 메시지 로드
  loadMessages();

  // 2. WebSocket 연결
  const ws = new WebSocket(`ws://localhost:8080/ws/chat?username=${username}`);
  
  ws.onmessage = (event) => {
    const newMessage = JSON.parse(event.data);
    // 새 메시지를 목록에 추가
    setMessages(prev => [...prev, newMessage]);
  };

  return () => {
    ws.close();
  };
}, [roomId]);
```

## 주의사항

1. **인증**: JWT 토큰 또는 쿠키 인증이 필요합니다.
2. **채팅방 멤버**: 요청한 사용자가 해당 채팅방의 멤버여야 합니다.
3. **정렬**: 응답된 메시지는 오래된 순으로 정렬됩니다 (timestamp 오름차순).
4. **페이징**: 기본값으로 최대 50개씩 조회됩니다. 더 많은 메시지가 필요하면 `page` 파라미터를 증가시켜 조회하세요.

## 에러 처리

- **404**: 채팅방을 찾을 수 없음
- **403**: 채팅방 멤버가 아님
- **401**: 인증되지 않음
- **500**: 서버 오류

```typescript
try {
  const messages = await chatMessageService.getRoomMessages(roomId);
} catch (error) {
  if (error.response?.status === 403) {
    alert('채팅방 멤버가 아닙니다.');
  } else if (error.response?.status === 404) {
    alert('채팅방을 찾을 수 없습니다.');
  } else {
    alert('메시지를 불러올 수 없습니다.');
  }
}
```









