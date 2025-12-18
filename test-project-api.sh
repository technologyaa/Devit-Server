#!/bin/bash

# 프로젝트 및 업무 API 테스트 스크립트
# 새로 구현한 API들을 테스트합니다

BASE_URL="http://localhost:8080"
PROJECT_ID=""
TASK_ID=""

echo "=========================================="
echo "=== 프로젝트 및 업무 API 테스트 ==="
echo "=========================================="
echo ""

# 색상 정의
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. 프로젝트 목록 조회
echo "1️⃣ 프로젝트 목록 조회 (GET /projects)..."
PROJECTS_RESPONSE=$(curl -s -X GET "$BASE_URL/projects" \
  -H "Content-Type: application/json")
echo "응답:"
echo "$PROJECTS_RESPONSE" | jq '.' 2>/dev/null || echo "$PROJECTS_RESPONSE"
echo ""

# 2. 프로젝트 생성
echo "2️⃣ 프로젝트 생성 (POST /projects)..."
PROJECT_CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/projects" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "API 테스트 프로젝트",
    "content": "프로젝트 및 업무 API 테스트를 위한 프로젝트입니다."
  }')

echo "응답:"
echo "$PROJECT_CREATE_RESPONSE" | jq '.' 2>/dev/null || echo "$PROJECT_CREATE_RESPONSE"
PROJECT_ID=$(echo $PROJECT_CREATE_RESPONSE | grep -oE '[0-9]+' | head -1)
echo ""

if [ -z "$PROJECT_ID" ]; then
  echo -e "${RED}❌ 프로젝트 생성 실패${NC}"
  echo "서버가 실행 중인지 확인하세요: $BASE_URL"
  exit 1
fi

echo -e "${GREEN}✅ 프로젝트 생성 성공! 프로젝트 ID: $PROJECT_ID${NC}"
echo ""

# 3. 프로젝트 상세 조회 (새로 구현한 API)
echo "3️⃣ 프로젝트 상세 조회 (GET /projects/$PROJECT_ID)..."
PROJECT_DETAIL_RESPONSE=$(curl -s -X GET "$BASE_URL/projects/$PROJECT_ID" \
  -H "Content-Type: application/json")

echo "응답:"
echo "$PROJECT_DETAIL_RESPONSE" | jq '.' 2>/dev/null || echo "$PROJECT_DETAIL_RESPONSE"
echo ""

# 필수 필드 확인
if echo "$PROJECT_DETAIL_RESPONSE" | grep -q "projectId"; then
  echo -e "${GREEN}✅ 프로젝트 상세 조회 성공!${NC}"
  
  # 프론트엔드가 기대하는 필드 확인
  if echo "$PROJECT_DETAIL_RESPONSE" | grep -q "thumbnail"; then
    echo -e "${GREEN}  ✓ thumbnail 필드 존재${NC}"
  else
    echo -e "${YELLOW}  ⚠ thumbnail 필드 없음${NC}"
  fi
  
  if echo "$PROJECT_DETAIL_RESPONSE" | grep -q "major"; then
    echo -e "${GREEN}  ✓ major 필드 존재${NC}"
  else
    echo -e "${YELLOW}  ⚠ major 필드 없음 (author가 Developer가 아닐 수 있음)${NC}"
  fi
  
  if echo "$PROJECT_DETAIL_RESPONSE" | grep -q "owner"; then
    echo -e "${GREEN}  ✓ owner 필드 존재${NC}"
  else
    echo -e "${YELLOW}  ⚠ owner 필드 없음${NC}"
  fi
  
  if echo "$PROJECT_DETAIL_RESPONSE" | grep -q "createdAt"; then
    echo -e "${GREEN}  ✓ createdAt 필드 존재${NC}"
  else
    echo -e "${YELLOW}  ⚠ createdAt 필드 없음${NC}"
  fi
  
  if echo "$PROJECT_DETAIL_RESPONSE" | grep -q "updatedAt"; then
    echo -e "${GREEN}  ✓ updatedAt 필드 존재${NC}"
  else
    echo -e "${YELLOW}  ⚠ updatedAt 필드 없음${NC}"
  fi
else
  echo -e "${RED}❌ 프로젝트 상세 조회 실패${NC}"
fi
echo ""

# 4. 업무 목록 조회 (빈 목록)
echo "4️⃣ 업무 목록 조회 (GET /projects/$PROJECT_ID/tasks) - 빈 목록..."
TASKS_LIST_RESPONSE=$(curl -s -X GET "$BASE_URL/projects/$PROJECT_ID/tasks" \
  -H "Content-Type: application/json")

echo "응답:"
echo "$TASKS_LIST_RESPONSE" | jq '.' 2>/dev/null || echo "$TASKS_LIST_RESPONSE"
echo ""

if echo "$TASKS_LIST_RESPONSE" | grep -q "\[\]"; then
  echo -e "${GREEN}✅ 업무 목록 조회 성공! (빈 목록)${NC}"
else
  echo -e "${YELLOW}⚠ 업무 목록이 비어있지 않거나 응답 형식이 다를 수 있습니다${NC}"
fi
echo ""

# 5. 업무 생성 (새로 구현한 API)
echo "5️⃣ 업무 생성 (POST /projects/$PROJECT_ID/tasks)..."
TASK_CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/projects/$PROJECT_ID/tasks" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "테스트 업무 1",
    "description": "이것은 테스트 업무입니다."
  }')

echo "응답:"
echo "$TASK_CREATE_RESPONSE" | jq '.' 2>/dev/null || echo "$TASK_CREATE_RESPONSE"
TASK_ID=$(echo $TASK_CREATE_RESPONSE | grep -oE '"taskId":[0-9]+' | grep -oE '[0-9]+' | head -1)
echo ""

if [ -z "$TASK_ID" ]; then
  echo -e "${RED}❌ 업무 생성 실패${NC}"
else
  echo -e "${GREEN}✅ 업무 생성 성공! 업무 ID: $TASK_ID${NC}"
  
  # 프론트엔드가 기대하는 필드 확인
  if echo "$TASK_CREATE_RESPONSE" | grep -q "isDone"; then
    echo -e "${GREEN}  ✓ isDone 필드 존재${NC}"
  else
    echo -e "${YELLOW}  ⚠ isDone 필드 없음${NC}"
  fi
  
  if echo "$TASK_CREATE_RESPONSE" | grep -q "projectId"; then
    echo -e "${GREEN}  ✓ projectId 필드 존재${NC}"
  else
    echo -e "${YELLOW}  ⚠ projectId 필드 없음${NC}"
  fi
  
  if echo "$TASK_CREATE_RESPONSE" | grep -q "createdAt"; then
    echo -e "${GREEN}  ✓ createdAt 필드 존재${NC}"
  else
    echo -e "${YELLOW}  ⚠ createdAt 필드 없음${NC}"
  fi
  
  if echo "$TASK_CREATE_RESPONSE" | grep -q "updatedAt"; then
    echo -e "${GREEN}  ✓ updatedAt 필드 존재${NC}"
  else
    echo -e "${YELLOW}  ⚠ updatedAt 필드 없음${NC}"
  fi
fi
echo ""

# 6. 업무 목록 조회 (업무가 있는 경우)
echo "6️⃣ 업무 목록 조회 (GET /projects/$PROJECT_ID/tasks) - 업무 포함..."
TASKS_LIST_RESPONSE2=$(curl -s -X GET "$BASE_URL/projects/$PROJECT_ID/tasks" \
  -H "Content-Type: application/json")

echo "응답:"
echo "$TASKS_LIST_RESPONSE2" | jq '.' 2>/dev/null || echo "$TASKS_LIST_RESPONSE2"
echo ""

if echo "$TASKS_LIST_RESPONSE2" | grep -q "taskId"; then
  echo -e "${GREEN}✅ 업무 목록 조회 성공!${NC}"
else
  echo -e "${RED}❌ 업무 목록 조회 실패${NC}"
fi
echo ""

# 7. 업무 하나 더 생성
echo "7️⃣ 업무 추가 생성 (POST /projects/$PROJECT_ID/tasks)..."
TASK_CREATE_RESPONSE2=$(curl -s -X POST "$BASE_URL/projects/$PROJECT_ID/tasks" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "테스트 업무 2",
    "description": "두 번째 테스트 업무입니다.",
    "status": "IN_PROGRESS"
  }')

echo "응답:"
echo "$TASK_CREATE_RESPONSE2" | jq '.' 2>/dev/null || echo "$TASK_CREATE_RESPONSE2"
echo ""

if echo "$TASK_CREATE_RESPONSE2" | grep -q "taskId"; then
  echo -e "${GREEN}✅ 업무 추가 생성 성공!${NC}"
else
  echo -e "${RED}❌ 업무 추가 생성 실패${NC}"
fi
echo ""

# 8. 업무 수정 (선택사항)
if [ ! -z "$TASK_ID" ]; then
  echo "8️⃣ 업무 수정 (PUT /projects/$PROJECT_ID/tasks/$TASK_ID)..."
  TASK_UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL/projects/$PROJECT_ID/tasks/$TASK_ID" \
    -H "Content-Type: application/json" \
    -d '{
      "title": "수정된 업무 제목",
      "description": "수정된 업무 설명",
      "status": "DONE"
    }')
  
  echo "응답:"
  echo "$TASK_UPDATE_RESPONSE" | jq '.' 2>/dev/null || echo "$TASK_UPDATE_RESPONSE"
  echo ""
  
  if echo "$TASK_UPDATE_RESPONSE" | grep -q "taskId"; then
    echo -e "${GREEN}✅ 업무 수정 성공!${NC}"
    
    # isDone이 true인지 확인
    if echo "$TASK_UPDATE_RESPONSE" | grep -q '"isDone":true'; then
      echo -e "${GREEN}  ✓ isDone이 true로 설정됨 (status가 DONE)${NC}"
    fi
  else
    echo -e "${RED}❌ 업무 수정 실패${NC}"
  fi
  echo ""
fi

# 9. 업무 삭제 (선택사항)
if [ ! -z "$TASK_ID" ]; then
  echo "9️⃣ 업무 삭제 (DELETE /projects/$PROJECT_ID/tasks/$TASK_ID)..."
  DELETE_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE_URL/projects/$PROJECT_ID/tasks/$TASK_ID")
  
  if [ "$DELETE_STATUS" = "204" ] || [ "$DELETE_STATUS" = "200" ]; then
    echo -e "${GREEN}✅ 업무 삭제 성공! (HTTP $DELETE_STATUS)${NC}"
  else
    echo -e "${RED}❌ 업무 삭제 실패 (HTTP $DELETE_STATUS)${NC}"
  fi
  echo ""
fi

# 10. 최종 업무 목록 조회
echo "🔟 최종 업무 목록 조회..."
FINAL_TASKS=$(curl -s -X GET "$BASE_URL/projects/$PROJECT_ID/tasks" \
  -H "Content-Type: application/json")

echo "응답:"
echo "$FINAL_TASKS" | jq '.' 2>/dev/null || echo "$FINAL_TASKS"
echo ""

# 정리
echo "=========================================="
echo "=== 테스트 완료 ==="
echo "=========================================="
echo ""
echo "생성된 프로젝트 ID: $PROJECT_ID"
echo ""
echo "프로젝트를 삭제하려면 다음 명령을 실행하세요:"
echo "curl -X DELETE $BASE_URL/projects/$PROJECT_ID"
echo ""

