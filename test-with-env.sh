#!/bin/bash

# 환경변수로 서버를 실행하고 테스트하는 스크립트

cd "$(dirname "$0")"

echo "=========================================="
echo "=== 환경변수로 프로젝트 API 테스트 ==="
echo "=========================================="
echo ""

# 환경변수 확인
echo "📋 환경변수 확인 중..."
REQUIRED_VARS=("DB_URL" "DB_USERNAME" "DB_PASSWORD" "JWT_SECRET" "SERVER_PORT")
MISSING_VARS=()

for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var}" ]; then
        MISSING_VARS+=("$var")
    else
        echo "  ✓ $var 설정됨"
    fi
done

if [ ${#MISSING_VARS[@]} -gt 0 ]; then
    echo ""
    echo "❌ 필수 환경변수가 설정되지 않았습니다:"
    for var in "${MISSING_VARS[@]}"; do
        echo "  - $var"
    done
    echo ""
    echo "환경변수를 설정하고 다시 실행하세요."
    echo "예: export DB_URL=jdbc:mysql://localhost:3306/devit"
    exit 1
fi

echo ""
echo "✅ 필수 환경변수 모두 설정됨"
echo ""

# 서버 포트 확인
SERVER_PORT=${SERVER_PORT:-8080}
BASE_URL="http://localhost:${SERVER_PORT}"

echo "서버 URL: $BASE_URL"
echo ""

# 서버가 실행 중인지 확인
echo "🔍 서버 상태 확인 중..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/projects" 2>/dev/null || echo "000")

if [ "$HTTP_CODE" = "000" ]; then
    echo "❌ 서버가 실행 중이 아닙니다."
    echo ""
    echo "서버를 먼저 실행하세요:"
    echo "  ./gradlew bootRun"
    echo ""
    echo "또는 환경변수를 설정하고 서버를 실행하세요:"
    echo "  export DB_URL=..."
    echo "  export DB_USERNAME=..."
    echo "  export DB_PASSWORD=..."
    echo "  export JWT_SECRET=..."
    echo "  export SERVER_PORT=8080"
    echo "  ./gradlew bootRun"
    exit 1
fi

echo "✅ 서버 실행 중 (HTTP $HTTP_CODE)"
echo ""

# 테스트 시작
echo "=========================================="
echo "=== API 테스트 시작 ==="
echo "=========================================="
echo ""

# 색상 정의
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 1. 프로젝트 목록 조회
echo "1️⃣ 프로젝트 목록 조회 (GET /projects)..."
RESPONSE=$(curl -s -X GET "$BASE_URL/projects" \
  -H "Content-Type: application/json")

HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X GET "$BASE_URL/projects")

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ 성공 (HTTP $HTTP_CODE)${NC}"
    echo "응답:"
    echo "$RESPONSE" | jq '.' 2>/dev/null || echo "$RESPONSE" | head -10
else
    echo -e "${RED}❌ 실패 (HTTP $HTTP_CODE)${NC}"
    echo "응답: $RESPONSE"
fi
echo ""

# 2. 프로젝트 생성
echo "2️⃣ 프로젝트 생성 (POST /projects)..."
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/projects" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "환경변수 테스트 프로젝트",
    "content": "환경변수로 실행한 테스트 프로젝트입니다."
  }')

HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/projects" \
  -H "Content-Type: application/json" \
  -d '{"title":"테스트","content":"테스트"}')

if [ "$HTTP_CODE" = "201" ] || [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ 성공 (HTTP $HTTP_CODE)${NC}"
    echo "응답:"
    echo "$CREATE_RESPONSE" | jq '.' 2>/dev/null || echo "$CREATE_RESPONSE"
    
    # 프로젝트 ID 추출
    PROJECT_ID=$(echo "$CREATE_RESPONSE" | grep -oE '[0-9]+' | head -1)
    if [ ! -z "$PROJECT_ID" ]; then
        echo -e "${GREEN}생성된 프로젝트 ID: $PROJECT_ID${NC}"
        
        # 3. 프로젝트 상세 조회
        echo ""
        echo "3️⃣ 프로젝트 상세 조회 (GET /projects/$PROJECT_ID)..."
        DETAIL_RESPONSE=$(curl -s -X GET "$BASE_URL/projects/$PROJECT_ID" \
          -H "Content-Type: application/json")
        
        DETAIL_HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X GET "$BASE_URL/projects/$PROJECT_ID")
        
        if [ "$DETAIL_HTTP_CODE" = "200" ]; then
            echo -e "${GREEN}✅ 성공 (HTTP $DETAIL_HTTP_CODE)${NC}"
            echo "응답:"
            echo "$DETAIL_RESPONSE" | jq '.' 2>/dev/null || echo "$DETAIL_RESPONSE" | head -20
            
            # 필수 필드 확인
            if echo "$DETAIL_RESPONSE" | grep -q "projectId"; then
                echo -e "${GREEN}  ✓ projectId 필드 존재${NC}"
            fi
            if echo "$DETAIL_RESPONSE" | grep -q "thumbnail"; then
                echo -e "${GREEN}  ✓ thumbnail 필드 존재${NC}"
            fi
            if echo "$DETAIL_RESPONSE" | grep -q "owner"; then
                echo -e "${GREEN}  ✓ owner 필드 존재${NC}"
            fi
            if echo "$DETAIL_RESPONSE" | grep -q "createdAt"; then
                echo -e "${GREEN}  ✓ createdAt 필드 존재${NC}"
            fi
        else
            echo -e "${RED}❌ 실패 (HTTP $DETAIL_HTTP_CODE)${NC}"
            echo "응답: $DETAIL_RESPONSE"
        fi
        
        # 4. 업무 목록 조회
        echo ""
        echo "4️⃣ 업무 목록 조회 (GET /projects/$PROJECT_ID/tasks)..."
        TASKS_RESPONSE=$(curl -s -X GET "$BASE_URL/projects/$PROJECT_ID/tasks" \
          -H "Content-Type: application/json")
        
        TASKS_HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X GET "$BASE_URL/projects/$PROJECT_ID/tasks")
        
        if [ "$TASKS_HTTP_CODE" = "200" ]; then
            echo -e "${GREEN}✅ 성공 (HTTP $TASKS_HTTP_CODE)${NC}"
            echo "응답:"
            echo "$TASKS_RESPONSE" | jq '.' 2>/dev/null || echo "$TASKS_RESPONSE"
        else
            echo -e "${RED}❌ 실패 (HTTP $TASKS_HTTP_CODE)${NC}"
            echo "응답: $TASKS_RESPONSE"
        fi
        
        # 5. 업무 생성
        echo ""
        echo "5️⃣ 업무 생성 (POST /projects/$PROJECT_ID/tasks)..."
        TASK_CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/projects/$PROJECT_ID/tasks" \
          -H "Content-Type: application/json" \
          -d '{
            "title": "테스트 업무",
            "description": "환경변수 테스트용 업무입니다."
          }')
        
        TASK_CREATE_HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/projects/$PROJECT_ID/tasks" \
          -H "Content-Type: application/json" \
          -d '{"title":"테스트","description":"테스트"}')
        
        if [ "$TASK_CREATE_HTTP_CODE" = "201" ] || [ "$TASK_CREATE_HTTP_CODE" = "200" ]; then
            echo -e "${GREEN}✅ 성공 (HTTP $TASK_CREATE_HTTP_CODE)${NC}"
            echo "응답:"
            echo "$TASK_CREATE_RESPONSE" | jq '.' 2>/dev/null || echo "$TASK_CREATE_RESPONSE"
            
            # 업무 ID 추출
            TASK_ID=$(echo "$TASK_CREATE_RESPONSE" | grep -oE '"taskId":[0-9]+' | grep -oE '[0-9]+' | head -1)
            if [ ! -z "$TASK_ID" ]; then
                echo -e "${GREEN}생성된 업무 ID: $TASK_ID${NC}"
                
                # 필수 필드 확인
                if echo "$TASK_CREATE_RESPONSE" | grep -q "isDone"; then
                    echo -e "${GREEN}  ✓ isDone 필드 존재${NC}"
                fi
                if echo "$TASK_CREATE_RESPONSE" | grep -q "projectId"; then
                    echo -e "${GREEN}  ✓ projectId 필드 존재${NC}"
                fi
                if echo "$TASK_CREATE_RESPONSE" | grep -q "createdAt"; then
                    echo -e "${GREEN}  ✓ createdAt 필드 존재${NC}"
                fi
            fi
        else
            echo -e "${RED}❌ 실패 (HTTP $TASK_CREATE_HTTP_CODE)${NC}"
            echo "응답: $TASK_CREATE_RESPONSE"
        fi
    fi
else
    echo -e "${RED}❌ 실패 (HTTP $HTTP_CODE)${NC}"
    echo "응답: $CREATE_RESPONSE"
fi

echo ""
echo "=========================================="
echo "=== 테스트 완료 ==="
echo "=========================================="


