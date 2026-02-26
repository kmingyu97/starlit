# STARLIT - Stock Trading Analysis & Realtime Live Investment Tool

주식시장을 한눈에 볼 수 있는 대시보드 플랫폼. MSA 학습 목적 프로젝트.

## 기술 스택

- **Frontend**: React 19 + Vite + TypeScript
- **Backend**: Spring Boot 4.0 + Java 17
- **Gateway**: Spring Cloud Gateway (WebMVC)
- **DB**: PostgreSQL 17 (서비스별 분리)
- **Infra**: Docker Compose
- **빌드**: Gradle (멀티 모듈)

## 서비스 구성

| 서비스 | 포트 | DB 포트 | 역할 |
|--------|------|---------|------|
| gateway | 8000 | - | 라우팅, JWT 검증 |
| user-service | 8001 | 5432 | 회원, 인증, 관심종목 |
| stock-service | 8002 | 5433 | 시세, 지수, 환율, 히트맵 |
| community-service | 8003 | 5434 | 게시글, 댓글, 좋아요 |
| frontend | 5173 | - | React SPA |

## 라우팅

```
/api/users/**     → user-service
/api/stocks/**    → stock-service
/api/community/** → community-service
```

## 패키지 구조

- 그룹: `com.starlit`
- 패키지: `com.starlit.{서비스명}` (예: `com.starlit.gateway`)

## 컨벤션

- 서비스 간 직접 통신 없음 (Gateway가 JWT 파싱 후 헤더로 유저 정보 전달)
- 비회원: 조회만 가능 / 회원: 커뮤니티 글쓰기, 관심종목 등
- 외부 API: 한국투자증권 API, 공공데이터포털 (무료)
- 비용: 로컬 개발 환경 기준 0원

## 상세 설계 문서

- [아키텍처 설계](docs/architecture.md)
