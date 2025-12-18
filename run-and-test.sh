#!/bin/bash

# 환경변수를 설정하고 서버를 실행한 후 테스트하는 스크립트

cd "$(dirname "$0")"

echo "=========================================="
echo "=== 환경변수 설정 및 서버 테스트 ==="
echo "=========================================="
echo ""

# 환경변수 설정 (기본값 사용)
export SERVER_PORT=${SERVER_PORT:-8080}
export DB_DRIVER=${DB_DRIVER:-com.mysql.cj.jdbc.Driver}

echo "📋 환경변수 확인:"
echo "  SERVER_PORT: ${SERVER_PORT:-'설정되지 않음'}"
echo "  DB_URL: ${DB_URL:-'설정되지 않음'}"
echo "  DB_USERNAME: ${DB_USERNAME:-'설정되지 않음'}"
echo "  DB_PASSWORD: ${DB_PASSWORD:-'설정되지 않음'}"
echo "  JWT_SECRET: ${JWT_SECRET:-'설정되지 않음'}"
echo ""

# 필수 환경변수 확인
if [ -z "$DB_URL" ] || [ -z "$DB_USERNAME" ] || [ -z "$DB_PASSWORD" ] || [ -z "$JWT_SECRET" ]; then
    echo "⚠️  필수 환경변수가 설정되지 않았습니다."
    echo ""
    echo "환경변수를 설정하세요:"
    echo "  export DB_URL=jdbc:mysql://localhost:3306/devit"
    echo "  export DB_USERNAME=your_username"
    echo "  export DB_PASSWORD=your_password"
    echo "  export JWT_SECRET=your_jwt_secret"
    echo "  export SERVER_PORT=8080"
    echo ""
    echo "그리고 이 스크립트를 다시 실행하거나,"
    echo "서버를 수동으로 실행한 후 테스트하세요:"
    echo "  ./gradlew bootRun"
    echo ""
    exit 1
fi

BASE_URL="http://localhost:${SERVER_PORT}"

echo "✅ 환경변수 설정 완료"
echo ""
echo "서버를 실행하려면:"
echo "  ./gradlew bootRun"
echo ""
echo "서버가 실행 중이면 다음 명령으로 테스트하세요:"
echo "  ./test-with-env.sh"
echo ""
echo "또는 직접 테스트:"
echo "  curl http://localhost:${SERVER_PORT}/projects"
echo ""

# 서버가 이미 실행 중인지 확인
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/projects" 2>/dev/null || echo "000")

if [ "$HTTP_CODE" != "000" ] && [ "$HTTP_CODE" != "500" ]; then
    echo "✅ 서버가 실행 중입니다 (HTTP $HTTP_CODE)"
    echo ""
    echo "테스트를 실행합니다..."
    echo ""
    ./test-with-env.sh
else
    echo "서버를 실행한 후 테스트하세요."
fi

