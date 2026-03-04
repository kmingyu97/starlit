# user-service 개발 태스크 (TDD)

각 태스크 = 1 커밋 단위. Red → Green → Refactor → Commit.

## Phase 1: 프로젝트 설정

### TASK-01: 의존성 및 설정 추가 ✅
- [x] build.gradle에 JWT, Security 의존성 추가
- [x] application.properties에 JWT 설정 (secret, expiration)
- [x] SecurityConfig: 필터 체인 비활성화 + BCryptPasswordEncoder 빈
- [x] 빌드 확인
- 커밋: `chore: user-service 의존성 및 보안 설정 추가` (c48a0ed)

## Phase 2: 공통 모듈

### TASK-02: 에러 응답 공통 처리 ✅
- [x] ErrorResponse DTO (status, code, message)
- [x] ErrorCode enum
- [x] CustomException
- [x] GlobalExceptionHandler (@RestControllerAdvice)
- [x] 테스트: GlobalExceptionHandler 단위 테스트 (3건)
- 커밋: `feat: user-service 공통 에러 처리 구현` (a4e75b5)

## Phase 3: User 도메인

### TASK-03: User 엔티티 + Repository ✅
- [x] User 엔티티 (id, email, password, nickname, createdAt)
- [x] UserRepository (JpaRepository)
- [x] 테스트: existsByEmail, existsByNickname 등 Repository 테스트 (@DataJpaTest, 5건)
- 커밋: `feat: User 엔티티 및 Repository 구현` (b9b03a2)

### TASK-04: 회원가입 API
- [ ] SignupRequest DTO (email, password, nickname + validation)
- [ ] SignupResponse DTO (id, email, nickname)
- [ ] UserService.signup() - 중복 체크, BCrypt 해싱, 저장
- [ ] UserController POST /api/users/signup
- [ ] 테스트: UserService 단위 테스트 (Mockito)
- [ ] 테스트: UserController 통합 테스트 (@WebMvcTest)
- 커밋: `feat: 회원가입 API 구현`

### TASK-05: JWT 유틸리티
- [ ] JwtProvider (토큰 생성, 파싱, 검증)
- [ ] 테스트: 토큰 생성/파싱/만료 검증 단위 테스트
- 커밋: `feat: JWT 토큰 발급/검증 유틸리티 구현`

### TASK-06: 로그인 API
- [ ] LoginRequest DTO (email, password)
- [ ] LoginResponse DTO (token, nickname)
- [ ] UserService.login() - 비밀번호 검증 + JWT 발급
- [ ] UserController POST /api/users/login
- [ ] 테스트: UserService.login 단위 테스트
- [ ] 테스트: UserController 통합 테스트
- 커밋: `feat: 로그인 API 구현`

### TASK-07: 프로필 조회/수정 API
- [ ] ProfileResponse DTO
- [ ] ProfileUpdateRequest DTO
- [ ] UserService.getProfile(), updateProfile()
- [ ] UserController GET/PUT /api/users/me (X-User-Id 헤더 기반)
- [ ] 테스트: 서비스 단위 테스트
- [ ] 테스트: 컨트롤러 통합 테스트
- 커밋: `feat: 프로필 조회/수정 API 구현`

## Phase 4: Watchlist 도메인

### TASK-08: Watchlist 엔티티 + Repository
- [ ] Watchlist 엔티티 (id, userId, stockCode, stockName, createdAt)
- [ ] WatchlistRepository
- [ ] 테스트: Repository 테스트 (@DataJpaTest)
- 커밋: `feat: Watchlist 엔티티 및 Repository 구현`

### TASK-09: 관심종목 CRUD API
- [ ] WatchlistRequest DTO (stockCode, stockName)
- [ ] WatchlistResponse DTO
- [ ] WatchlistService - 추가/목록/삭제
- [ ] WatchlistController (X-User-Id 헤더 기반)
- [ ] 테스트: 서비스 단위 테스트
- [ ] 테스트: 컨트롤러 통합 테스트
- 커밋: `feat: 관심종목 CRUD API 구현`

---

## 진행 상태

- 현재: **TASK-04 진행 예정**
- 마지막 완료: TASK-03 (b9b03a2)
