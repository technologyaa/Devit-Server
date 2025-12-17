#!/bin/bash

# 새로운 기능 테스트 스크립트
# 사용법: ./test-new-features.sh <base_url> <access_token>

BASE_URL=${1:-"http://localhost:8080"}
TOKEN=${2:-""}

echo "=========================================="
echo "새로운 기능 테스트 시작"
echo "Base URL: $BASE_URL"
echo "=========================================="
echo ""

# 1. 현재 유저 조회 테스트
echo "1. 현재 유저 조회 테스트 (GET /auth/me)"
if [ -z "$TOKEN" ]; then
    echo "   ⚠️  토큰이 없어서 스킵합니다. 먼저 로그인하여 토큰을 받아주세요."
else
    curl -X GET "$BASE_URL/auth/me" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -w "\nHTTP Status: %{http_code}\n" \
        -s | jq '.' || echo "   ❌ 실패"
fi
echo ""

# 2. 모든 유저 조회 테스트
echo "2. 모든 유저 조회 테스트 (GET /auth/members)"
if [ -z "$TOKEN" ]; then
    echo "   ⚠️  토큰이 없어서 스킵합니다."
else
    curl -X GET "$BASE_URL/auth/members" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -w "\nHTTP Status: %{http_code}\n" \
        -s | jq '.' || echo "   ❌ 실패"
fi
echo ""

# 3. 내가 참가한 프로젝트 조회 테스트
echo "3. 내가 참가한 프로젝트 조회 테스트 (GET /projects/my-projects)"
if [ -z "$TOKEN" ]; then
    echo "   ⚠️  토큰이 없어서 스킵합니다."
else
    curl -X GET "$BASE_URL/projects/my-projects" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -w "\nHTTP Status: %{http_code}\n" \
        -s | jq '.' || echo "   ❌ 실패"
fi
echo ""

# 4. 채팅하고 있는 유저들 조회 테스트
echo "4. 채팅하고 있는 유저들 조회 테스트 (GET /chat/users)"
if [ -z "$TOKEN" ]; then
    echo "   ⚠️  토큰이 없어서 스킵합니다."
else
    curl -X GET "$BASE_URL/chat/users" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -w "\nHTTP Status: %{http_code}\n" \
        -s | jq '.' || echo "   ❌ 실패"
fi
echo ""

echo "=========================================="
echo "테스트 완료"
echo "=========================================="
echo ""
echo "사용법:"
echo "  ./test-new-features.sh <base_url> <access_token>"
echo ""
echo "예시:"
echo "  ./test-new-features.sh http://localhost:8080 eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

