#!/bin/bash

# 프로젝트 → 업무 → 파일 API 테스트 스크립트

BASE_URL="http://localhost:8080"
PROJECT_ID=""
TASK_ID=""

echo "=== 프로젝트 → 업무 → 파일 API 테스트 ==="
echo ""

# 1. 프로젝트 생성
echo "1. 프로젝트 생성 중..."
PROJECT_RESPONSE=$(curl -s -X POST "$BASE_URL/projects" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "파일 테스트 프로젝트",
    "content": "업무 파일 업로드/다운로드 테스트용 프로젝트",
    "major": "BACKEND"
  }')

PROJECT_ID=$(echo $PROJECT_RESPONSE | grep -o '"ProjectId":[0-9]*' | grep -o '[0-9]*')
echo "생성된 프로젝트 ID: $PROJECT_ID"
echo "프로젝트 응답: $PROJECT_RESPONSE"
echo ""

if [ -z "$PROJECT_ID" ]; then
  echo "❌ 프로젝트 생성 실패"
  exit 1
fi

# 2. 업무 생성
echo "2. 업무 생성 중..."
TASK_RESPONSE=$(curl -s -X POST "$BASE_URL/projects/$PROJECT_ID/tasks" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "파일 업로드 테스트 업무",
    "description": "이 업무에 파일을 업로드합니다",
    "status": "TODO"
  }')

TASK_ID=$(echo $TASK_RESPONSE | grep -o '"taskId":[0-9]*' | grep -o '[0-9]*')
echo "생성된 업무 ID: $TASK_ID"
echo "업무 응답: $TASK_RESPONSE"
echo ""

if [ -z "$TASK_ID" ]; then
  echo "❌ 업무 생성 실패"
  exit 1
fi

# 3. 테스트 파일 생성
echo "3. 테스트 파일 생성 중..."
echo "이것은 업무 테스트 파일입니다." > task-test-file.txt
echo "테스트 파일이 생성되었습니다: task-test-file.txt"
echo ""

# 4. 파일 업로드
echo "4. 파일 업로드 중..."
UPLOAD_RESPONSE=$(curl -s -X POST "$BASE_URL/projects/$PROJECT_ID/tasks/$TASK_ID/files" \
  -F "file=@task-test-file.txt")

echo "업로드 응답: $UPLOAD_RESPONSE"
echo ""

# 5. 파일 목록 조회
echo "5. 파일 목록 조회 중..."
FILE_LIST=$(curl -s -X GET "$BASE_URL/projects/$PROJECT_ID/tasks/$TASK_ID/files")
echo "파일 목록: $FILE_LIST"
echo ""

# 파일 ID 추출
FILE_ID=$(echo $FILE_LIST | grep -o '"fileId":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "첫 번째 파일 ID: $FILE_ID"
echo ""

if [ -z "$FILE_ID" ]; then
  echo "❌ 파일 목록 조회 실패 또는 파일이 없습니다"
  exit 1
fi

# 6. 파일 다운로드
echo "6. 파일 다운로드 중..."
curl -s -X GET "$BASE_URL/projects/$PROJECT_ID/tasks/$TASK_ID/files/$FILE_ID" \
  -o downloaded-task-file.txt

if [ -f "downloaded-task-file.txt" ]; then
  echo "✅ 파일 다운로드 성공: downloaded-task-file.txt"
  echo "다운로드된 파일 내용:"
  cat downloaded-task-file.txt
  echo ""
else
  echo "❌ 파일 다운로드 실패"
fi

# 7. 업무 목록 조회
echo "7. 프로젝트의 모든 업무 조회 중..."
TASK_LIST=$(curl -s -X GET "$BASE_URL/projects/$PROJECT_ID/tasks")
echo "업무 목록: $TASK_LIST"
echo ""

# 8. 파일 삭제
echo "8. 파일 삭제 중..."
DELETE_RESPONSE=$(curl -s -X DELETE "$BASE_URL/projects/$PROJECT_ID/tasks/$TASK_ID/files/$FILE_ID")
echo "삭제 응답: $DELETE_RESPONSE"
echo ""

# 9. 업무 삭제
echo "9. 업무 삭제 중..."
curl -s -X DELETE "$BASE_URL/projects/$PROJECT_ID/tasks/$TASK_ID"
echo "업무 삭제 완료"
echo ""

# 10. 프로젝트 삭제
echo "10. 프로젝트 삭제 중..."
curl -s -X DELETE "$BASE_URL/projects/$PROJECT_ID"
echo "프로젝트 삭제 완료"
echo ""

# 정리
rm -f task-test-file.txt downloaded-task-file.txt
echo "=== 테스트 완료 ==="


