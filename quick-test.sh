#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== 빠른 API 테스트 ==="
echo ""

# 1. 프로젝트 생성
echo "1️⃣ 프로젝트 생성..."
PROJECT=$(curl -s -X POST "$BASE_URL/projects" \
  -H "Content-Type: application/json" \
  -d '{"title":"테스트 프로젝트","content":"테스트","major":"BACKEND"}')

echo "응답: $PROJECT"
PROJECT_ID=$(echo $PROJECT | grep -o '"ProjectId":[0-9]*' | grep -o '[0-9]*')
echo "프로젝트 ID: $PROJECT_ID"
echo ""

if [ -z "$PROJECT_ID" ]; then
  echo "❌ 프로젝트 생성 실패 - 서버가 실행 중인지 확인하세요"
  exit 1
fi

# 2. 업무 생성
echo "2️⃣ 업무 생성..."
TASK=$(curl -s -X POST "$BASE_URL/projects/$PROJECT_ID/tasks" \
  -H "Content-Type: application/json" \
  -d '{"title":"테스트 업무","description":"테스트 업무 설명","status":"TODO"}')

echo "응답: $TASK"
TASK_ID=$(echo $TASK | grep -o '"taskId":[0-9]*' | grep -o '[0-9]*')
echo "업무 ID: $TASK_ID"
echo ""

if [ -z "$TASK_ID" ]; then
  echo "❌ 업무 생성 실패"
  exit 1
fi

# 3. 파일 업로드
echo "3️⃣ 파일 업로드..."
echo "테스트 파일 내용" > test-file.txt
UPLOAD=$(curl -s -X POST "$BASE_URL/projects/$PROJECT_ID/tasks/$TASK_ID/files" \
  -F "file=@test-file.txt")

echo "응답: $UPLOAD"
echo ""

# 4. 파일 목록 조회
echo "4️⃣ 파일 목록 조회..."
FILES=$(curl -s -X GET "$BASE_URL/projects/$PROJECT_ID/tasks/$TASK_ID/files")
echo "응답: $FILES"
FILE_ID=$(echo $FILES | grep -o '"fileId":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "파일 ID: $FILE_ID"
echo ""

# 5. 파일 다운로드
if [ ! -z "$FILE_ID" ]; then
  echo "5️⃣ 파일 다운로드..."
  curl -s -X GET "$BASE_URL/projects/$PROJECT_ID/tasks/$TASK_ID/files/$FILE_ID" -o downloaded.txt
  if [ -f "downloaded.txt" ]; then
    echo "✅ 다운로드 성공!"
    echo "내용: $(cat downloaded.txt)"
    rm downloaded.txt
  fi
  echo ""
fi

# 6. 업무 목록 조회
echo "6️⃣ 프로젝트의 모든 업무 조회..."
TASKS=$(curl -s -X GET "$BASE_URL/projects/$PROJECT_ID/tasks")
echo "응답: $TASKS"
echo ""

echo "✅ 모든 테스트 완료!"


