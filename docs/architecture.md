# STARLIT 아키텍처 설계

## 전체 아키텍처

```
[React Frontend :5173]
         │
         ▼
     [Gateway :8000]  ── JWT 검증, 라우팅, 헤더 주입
         │
    ┌────┼──────────────┐
    ▼    ▼              ▼
[user   [stock       [community
:8001]   :8002]       :8003]
  │        │              │
  ▼        ▼              ▼
[user-db [stock-db   [community-db
:5432]    :5433]      :5434]
           + 외부 API
```

---

## 1. Gateway

| 항목 | 내용 |
|------|------|
| 포트 | 8000 |
| 역할 | 라우팅, JWT 검증, 공통 헤더 주입 |
| 기술 | Spring Cloud Gateway (WebMVC) |

### 라우팅 규칙

```
/api/users/**     → user-service (8001)
/api/stocks/**    → stock-service (8002)
/api/community/** → community-service (8003)
```

### JWT 처리 흐름

1. 클라이언트가 `Authorization: Bearer {token}` 헤더로 요청
2. Gateway에서 JWT 검증
3. 검증 성공 시 `X-User-Id`, `X-User-Name` 헤더를 하위 서비스에 주입
4. 검증 실패 시 401 반환 (공개 API는 검증 없이 통과)

### JWT 시크릿 키

- user-service(발급)와 gateway(검증)가 동일한 시크릿 키를 사용
- 각 서비스의 `application.properties`에 동일한 값 설정
- 알고리즘: HMAC-SHA256 (HS256)
- 라이브러리: `io.jsonwebtoken:jjwt` (0.12.x)
- 토큰 만료: 24시간

```properties
# user-service, gateway 공통
jwt.secret=starlit-jwt-secret-key-for-development-only
jwt.expiration=86400000
```

### 공개 API (JWT 검증 제외)

Gateway에서 JWT 검증 없이 통과시키는 API 목록:

```
# 인증
POST   /api/users/signup
POST   /api/users/login

# 주식 데이터 (비회원 대시보드)
GET    /api/stocks/**

# 커뮤니티 조회
GET    /api/community/posts
GET    /api/community/posts/{id}
GET    /api/community/posts/{id}/comments
```

그 외 모든 요청은 JWT 필수 (없거나 만료 시 401).

### CORS 설정

- Gateway에서 일괄 처리 (각 서비스에서는 설정 불필요)
- 허용 Origin: `http://localhost:5173` (프론트엔드)
- 허용 메서드: `GET, POST, PUT, DELETE, OPTIONS`
- 허용 헤더: `Authorization, Content-Type`
- Credentials: `true` (쿠키/인증 헤더 허용)

---

## 2. user-service

| 항목 | 내용 |
|------|------|
| 포트 | 8001 |
| DB | user-db (PostgreSQL, 5432) |
| 역할 | 회원가입, 로그인, 프로필, 관심종목 |

### API

```
POST   /api/users/signup                   # 회원가입
POST   /api/users/login                    # 로그인 → JWT 발급
GET    /api/users/me                       # 내 정보 조회
PUT    /api/users/me                       # 내 정보 수정

GET    /api/users/watchlist                # 관심종목 목록
POST   /api/users/watchlist                # 관심종목 추가
DELETE /api/users/watchlist/{stockCode}    # 관심종목 삭제
```

### 비밀번호 처리

- 해싱: BCrypt (Spring Security의 `BCryptPasswordEncoder`)
- 회원가입 시 평문 → BCrypt 해싱 후 DB 저장
- 로그인 시 입력값과 DB 해시값 비교 (`matches()`)
- Spring Security는 BCryptPasswordEncoder만 사용하고, 필터 체인은 비활성화 (인증은 Gateway에서 처리)

### DB 스키마

```sql
-- 회원
CREATE TABLE users (
    id           BIGSERIAL PRIMARY KEY,
    email        VARCHAR(100) UNIQUE NOT NULL,
    password     VARCHAR(255) NOT NULL,
    nickname     VARCHAR(30) UNIQUE NOT NULL,
    created_at   TIMESTAMP DEFAULT NOW()
);

-- 관심종목
CREATE TABLE watchlist (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT REFERENCES users(id),
    stock_code   VARCHAR(20) NOT NULL,
    stock_name   VARCHAR(100) NOT NULL,
    created_at   TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, stock_code)
);
```

---

## 3. stock-service

| 항목 | 내용 |
|------|------|
| 포트 | 8002 |
| DB | stock-db (PostgreSQL, 5433) |
| 역할 | 시세, 지수, 환율, 종목정보, 히트맵, 인기검색 |
| 외부 API | 한국투자증권 API, 공공데이터포털 |

### API

```
# 대시보드 (비회원 접근 가능)
GET /api/stocks/indices                    # 주요 지수 (코스피/코스닥/다우/나스닥)
GET /api/stocks/exchange-rates             # 환율 (USD, JPY, EUR)

# 종목
GET /api/stocks                            # 종목 리스트 (검색, 필터, 페이징)
GET /api/stocks/{stockCode}                # 종목 상세 (시세, 기본 재무)
GET /api/stocks/{stockCode}/chart          # 차트 데이터 (일봉/주봉/월봉)
GET /api/stocks/{stockCode}/news           # 종목 관련 뉴스

# 히트맵
GET /api/stocks/heatmap                    # 히트맵 데이터 (섹터별 등락률, 시가총액)

# 인기 검색
GET /api/stocks/popular                    # 인기 검색 종목 TOP 10
```

### DB 스키마

```sql
-- 종목 마스터 (KRX에서 주기적 동기화)
CREATE TABLE stock_master (
    stock_code   VARCHAR(20) PRIMARY KEY,
    stock_name   VARCHAR(100) NOT NULL,
    market       VARCHAR(10) NOT NULL,        -- KOSPI / KOSDAQ
    sector       VARCHAR(100),                -- 업종
    market_cap   BIGINT,                      -- 시가총액
    updated_at   TIMESTAMP
);

-- 일별 시세 (캐시)
CREATE TABLE stock_daily_price (
    id           BIGSERIAL PRIMARY KEY,
    stock_code   VARCHAR(20) NOT NULL,
    trade_date   DATE NOT NULL,
    open_price   INTEGER,
    high_price   INTEGER,
    low_price    INTEGER,
    close_price  INTEGER,
    volume       BIGINT,
    change_rate  DECIMAL(6,2),                -- 등락률(%)
    UNIQUE(stock_code, trade_date)
);

-- 검색 로그 (인기 검색용)
CREATE TABLE search_log (
    id           BIGSERIAL PRIMARY KEY,
    stock_code   VARCHAR(20) NOT NULL,
    searched_at  TIMESTAMP DEFAULT NOW()
);
```

### 외부 API 연동 전략

| 데이터 | 소스 | 주기 |
|--------|------|------|
| 종목 마스터 | KRX 공공데이터 | 하루 1회 스케줄러 |
| 시세 데이터 | 한국투자증권 API | 요청 시 조회 → DB 캐시 |
| 지수/환율 | 한국투자증권 API | 일정 시간 캐시 |

---

## 4. community-service

| 항목 | 내용 |
|------|------|
| 포트 | 8003 |
| DB | community-db (PostgreSQL, 5434) |
| 역할 | 게시글, 댓글, 좋아요 |

### API

```
# 게시글 (비회원: 조회만 / 회원: 전체)
GET    /api/community/posts                    # 글 목록 (페이징, 카테고리 필터)
GET    /api/community/posts/{id}               # 글 상세
POST   /api/community/posts                    # 글 작성 (회원만)
PUT    /api/community/posts/{id}               # 글 수정 (본인만)
DELETE /api/community/posts/{id}               # 글 삭제 (본인만)

# 댓글
GET    /api/community/posts/{id}/comments      # 댓글 목록
POST   /api/community/posts/{id}/comments      # 댓글 작성 (회원만)
DELETE /api/community/comments/{id}            # 댓글 삭제 (본인만)

# 좋아요
POST   /api/community/posts/{id}/like          # 좋아요 토글 (회원만)
```

### DB 스키마

```sql
-- 게시글
CREATE TABLE posts (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    nickname      VARCHAR(30) NOT NULL,
    category      VARCHAR(30) NOT NULL,         -- 자유/종목토론/뉴스/질문
    title         VARCHAR(200) NOT NULL,
    content       TEXT NOT NULL,
    view_count    INTEGER DEFAULT 0,
    like_count    INTEGER DEFAULT 0,
    comment_count INTEGER DEFAULT 0,
    created_at    TIMESTAMP DEFAULT NOW(),
    updated_at    TIMESTAMP DEFAULT NOW()
);

-- 댓글
CREATE TABLE comments (
    id           BIGSERIAL PRIMARY KEY,
    post_id      BIGINT REFERENCES posts(id) ON DELETE CASCADE,
    user_id      BIGINT NOT NULL,
    nickname     VARCHAR(30) NOT NULL,
    content      TEXT NOT NULL,
    created_at   TIMESTAMP DEFAULT NOW()
);

-- 좋아요
CREATE TABLE post_likes (
    id           BIGSERIAL PRIMARY KEY,
    post_id      BIGINT REFERENCES posts(id) ON DELETE CASCADE,
    user_id      BIGINT NOT NULL,
    UNIQUE(post_id, user_id)
);
```

### 권한 처리

- Gateway가 JWT 검증 → `X-User-Id`, `X-User-Name` 헤더 주입
- 헤더 존재 → 회원 / 헤더 없음 → 비회원
- 쓰기 API: 헤더 없으면 `401 Unauthorized`
- 수정/삭제: `X-User-Id`와 작성자 ID 비교 → 불일치 시 `403 Forbidden`

---

## 5. 인프라 (Docker Compose)

```yaml
services:
  user-db:
    image: postgres:17
    container_name: user-db
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: userdb
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    volumes:
      - user-db-data:/var/lib/postgresql/data

  stock-db:
    image: postgres:17
    container_name: stock-db
    ports:
      - "5433:5432"
    environment:
      POSTGRES_DB: stockdb
      POSTGRES_USER: stock
      POSTGRES_PASSWORD: password
    volumes:
      - stock-db-data:/var/lib/postgresql/data

  community-db:
    image: postgres:17
    container_name: community-db
    ports:
      - "5434:5432"
    environment:
      POSTGRES_DB: communitydb
      POSTGRES_USER: community
      POSTGRES_PASSWORD: password
    volumes:
      - community-db-data:/var/lib/postgresql/data

volumes:
  user-db-data:
  stock-db-data:
  community-db-data:
```

---

## 6. 공통 규격

### 에러 응답 형식

모든 서비스가 동일한 에러 응답 포맷을 사용한다.

```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "이메일 형식이 올바르지 않습니다."
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| status | int | HTTP 상태 코드 |
| code | String | 에러 코드 (대문자 스네이크) |
| message | String | 사용자에게 보여줄 메시지 |

주요 에러 코드:

| 코드 | HTTP | 상황 |
|------|------|------|
| `VALIDATION_ERROR` | 400 | 입력값 검증 실패 |
| `DUPLICATE_EMAIL` | 409 | 이메일 중복 |
| `DUPLICATE_NICKNAME` | 409 | 닉네임 중복 |
| `INVALID_CREDENTIALS` | 401 | 로그인 실패 (이메일/비밀번호 불일치) |
| `UNAUTHORIZED` | 401 | 인증 필요 (JWT 없음/만료) |
| `FORBIDDEN` | 403 | 권한 없음 (본인 아님) |
| `NOT_FOUND` | 404 | 리소스 없음 |
| `INTERNAL_ERROR` | 500 | 서버 내부 오류 |

### 페이징 응답 형식

목록 조회 API (종목 리스트, 게시글 목록 등)의 응답 형식:

```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| content | List | 데이터 배열 |
| page | int | 현재 페이지 (0부터) |
| size | int | 페이지 크기 |
| totalElements | long | 전체 데이터 수 |
| totalPages | int | 전체 페이지 수 |

요청 파라미터: `?page=0&size=20&sort=createdAt,desc`

---

## 7. 서비스 간 통신

| 상황 | 방식 |
|------|------|
| 프론트 → 백엔드 | Gateway 경유 (JWT 포함) |
| 회원 정보 전달 | Gateway가 JWT 파싱 → X-User-Id, X-User-Name 헤더 주입 |
| 서비스 간 직접 호출 | 없음 (현재 설계에서 불필요) |

---

## 8. 추가 의존성

기존 build.gradle에 추가 필요한 라이브러리:

| 서비스 | 라이브러리 | 용도 |
|--------|-----------|------|
| **gateway** | `io.jsonwebtoken:jjwt-api:0.12.6` | JWT 검증 |
| **gateway** | `io.jsonwebtoken:jjwt-impl:0.12.6` (runtime) | JWT 구현체 |
| **gateway** | `io.jsonwebtoken:jjwt-jackson:0.12.6` (runtime) | JWT JSON 처리 |
| **user-service** | `io.jsonwebtoken:jjwt-api:0.12.6` | JWT 발급 |
| **user-service** | `io.jsonwebtoken:jjwt-impl:0.12.6` (runtime) | JWT 구현체 |
| **user-service** | `io.jsonwebtoken:jjwt-jackson:0.12.6` (runtime) | JWT JSON 처리 |
| **user-service** | `org.springframework.boot:spring-boot-starter-security` | BCryptPasswordEncoder |

---

## 9. 프론트엔드 개발 환경

### Vite 프록시

프론트엔드(5173) → Gateway(8000) 포트가 다르므로, 개발 시 Vite 프록시 설정 필요:

```typescript
// vite.config.ts
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8000',
        changeOrigin: true
      }
    }
  }
})
```

---

## 10. 프로젝트 디렉토리 구조

```
starlit/
├── frontend/                           # React + Vite + TypeScript
├── gateway/                            # Spring Cloud Gateway
├── services/
│   ├── user-service/                   # 회원/인증/관심종목
│   ├── stock-service/                  # 주식 데이터 (TODO)
│   └── community-service/             # 커뮤니티 (TODO)
├── infra/
│   └── docker-compose.yml             # DB 3개
├── docs/
│   └── architecture.md                # 이 문서
├── CLAUDE.md                           # 프로젝트 요약
├── build.gradle                        # 루트 빌드
└── settings.gradle                     # 모듈 등록
```

### 각 서비스 내부 패키지 구조

```
com.starlit.{서비스명}/
├── controller/         # REST API
├── service/            # 비즈니스 로직
├── repository/         # JPA Repository
├── entity/             # DB 엔티티
├── dto/                # 요청/응답 DTO
└── config/             # 설정
```
