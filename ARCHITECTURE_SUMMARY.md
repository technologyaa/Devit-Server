# Devit 프로젝트 아키텍처 설계 요약

## 아키텍처 설계 문서에 포함해야 할 내용

1. **시스템 개요**: Spring Boot 기반 실시간 협업 플랫폼으로 WebSocket 채팅, OAuth2/JWT 인증, 프로젝트 관리 기능을 제공하는 레이어드 아키텍처 패턴을 사용

2. **기술 스택**: Spring Boot 3.2.5, Java 17, MySQL 8.0, Spring Security (JWT + OAuth2), WebSocket, Redis (선택적), Swagger API 문서화

3. **레이어 구조**: Presentation Layer (Controller, WebSocket Handler, Static HTML) → Business Logic Layer (Service) → Data Access Layer (Repository, Entity) → Infrastructure Layer (MySQL, Redis, Email, Security)

4. **주요 컴포넌트**: 인증/인가 시스템 (JWT 필터, OAuth2 핸들러), WebSocket 채팅 시스템 (ChatHandler), 프로젝트 관리 시스템 (ProjectService), 전역 예외 처리 및 공통 응답 형식

5. **인프라 구조**: 로컬 환경에서는 Spring Boot 애플리케이션이 MySQL과 Redis에 직접 연결하며, AWS 배포 시에는 Elastic Beanstalk/EC2에 배포하고 RDS MySQL, ElastiCache Redis를 사용하여 확장 가능한 구조로 설계

