# playground-jmeter 설계 문서

- **작성일:** 2026-06-17
- **작성자:** wypark
- **목적:** JMeter 입문 + 성능 테스트 학습용 모듈. DB 락 경합, 스레드 공유자원 안전성, 플랫폼 스레드 vs 가상 스레드 성능을 JMeter로 직접 측정한다.

---

## 1. 모듈 개요

| 항목 | 내용 |
|------|------|
| 모듈명 | `playground-jmeter` |
| Java 버전 | **21** (가상 스레드 사용을 위해 해당 모듈만 Java 21 적용) |
| Spring Boot | 3.4.4 |
| 포트 | **8090** (Application 모듈 8089와 충돌 방지) |
| DB | 기존 MariaDB `lab` 데이터베이스 재사용 |
| DDL | `ddl-auto=create` (테이블 자동 생성) |

---

## 2. 모듈 디렉터리 구조

각 테스트 관심사(CRUD, 락, 스레드 안전성, 가상 스레드)를 최상위 패키지로 완전히 분리한다.
패키지 간 의존은 `Product` 엔티티 한 곳(crud.entity)만 허용하며, 비즈니스 로직은 절대 공유하지 않는다.

```
playground-jmeter/
├── build.gradle
├── src/
│   └── main/
│       ├── java/com/example/playground/jmeter/
│       │   ├── JmeterApplication.java
│       │   │
│       │   ├── config/
│       │   │   └── VirtualThreadConfig.java              # 가상 스레드 Executor Bean 등록
│       │   │
│       │   ├── crud/                                     # [1] CRUD 성능 테스트
│       │   │   ├── controller/
│       │   │   │   └── ProductController.java
│       │   │   ├── service/
│       │   │   │   └── ProductService.java
│       │   │   ├── repository/
│       │   │   │   └── ProductRepository.java
│       │   │   └── entity/
│       │   │       └── Product.java                      # @Version 포함 (lock 패키지도 동일 테이블 사용)
│       │   │
│       │   ├── lock/                                     # [2] DB 락 경합 테스트
│       │   │   ├── controller/
│       │   │   │   └── LockController.java
│       │   │   ├── service/
│       │   │   │   ├── OptimisticLockService.java        # JPA @Version
│       │   │   │   ├── PessimisticLockService.java       # JPA @Lock(PESSIMISTIC_WRITE)
│       │   │   │   ├── RowLockService.java               # JDBC SELECT ... FOR UPDATE
│       │   │   │   ├── TableLockService.java             # JDBC LOCK TABLES ... WRITE
│       │   │   │   └── AppLockService.java               # MariaDB GET_LOCK()
│       │   │   └── repository/
│       │   │       └── LockProductRepository.java        # PESSIMISTIC_WRITE 전용 쿼리 정의
│       │   │
│       │   └── thread/
│       │       ├── safety/                               # [3] 스레드 공유자원 테스트
│       │       │   ├── controller/
│       │       │   │   ├── UnsafeThreadController.java   # /api/thread/unsafe/**
│       │       │   │   ├── SafeThreadController.java     # /api/thread/safe/**
│       │       │   │   └── ThreadResultController.java   # /api/thread/result (카운터 조회)
│       │       │   └── service/
│       │       │       ├── unsafe/
│       │       │       │   ├── UnsafeFieldService.java         # static int counter++ Race Condition
│       │       │       │   ├── UnsafeListService.java          # 공유 ArrayList
│       │       │       │   ├── UnsafeCheckThenActService.java  # if stock>0 → stock-- 복합 연산
│       │       │       │   └── UnsafeSingletonFieldService.java # Bean 인스턴스 필드 공유
│       │       │       └── safe/
│       │       │           ├── SafeAtomicService.java          # AtomicInteger
│       │       │           ├── SafeSynchronizedService.java    # synchronized 블록
│       │       │           ├── SafeReentrantLockService.java   # ReentrantLock
│       │       │           ├── SafeConcurrentMapService.java   # ConcurrentHashMap
│       │       │           └── SafeStatelessService.java       # 무상태 서비스 (Spring 기본)
│       │       │
│       │       └── virtual/                              # [4] 플랫폼 스레드 vs 가상 스레드 비교
│       │           ├── controller/
│       │           │   └── VirtualThreadController.java  # /api/thread/platform/**, /api/thread/virtual/**
│       │           └── service/
│       │               ├── PlatformThreadService.java    # 일반 스레드풀 실행
│       │               └── VirtualThreadService.java     # Thread.ofVirtual() 실행
│       │
│       └── resources/
│           └── application.properties
└── jmeter/
    ├── test-plan.jmx                                     # JMeter GUI에서 열 수 있는 전체 테스트 계획
    └── plans/                                            # Thread Group별 개별 .jmx 파일 (선택 실행용)
        ├── 01-crud.jmx
        ├── 02-lock.jmx
        ├── 03-thread-safety.jmx
        └── 04-virtual-thread.jmx
```

### 패키지 의존 관계

```
crud.entity.Product  ←─── lock.repository.LockProductRepository (동일 테이블 공유)
        │
        └── 그 외 패키지는 crud.entity 참조 없음 (완전 독립)

thread.safety  ──── thread.virtual  간 의존 없음
lock           ──── thread.*        간 의존 없음
```

---

## 3. 엔티티 설계

### Product

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | PK, Auto Increment |
| `name` | String | 상품명 |
| `price` | Integer | 가격 |
| `stock` | Integer | 재고 (락 경합 테스트 대상) |
| `version` | Long | 낙관적 락용 버전 (`@Version`) |
| `createdAt` | LocalDateTime | 생성일시 |

---

## 4. API 설계

### 4-1. 기본 CRUD (`/api/products`)

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/api/products` | 상품 생성 |
| `GET` | `/api/products` | 전체 목록 조회 |
| `GET` | `/api/products/{id}` | 단건 조회 |
| `PUT` | `/api/products/{id}` | 상품 수정 |
| `DELETE` | `/api/products/{id}` | 상품 삭제 |

### 4-2. DB 락 경합 (`/api/products/{id}/stock`)

| Method | Endpoint | 락 전략 | 구현 방식 |
|--------|----------|---------|----------|
| `PUT` | `/stock/optimistic` | 낙관적 락 | JPA `@Version` → `OptimisticLockException` 시 재시도 없이 409 반환 |
| `PUT` | `/stock/pessimistic` | 비관적 락 | JPA `@Lock(PESSIMISTIC_WRITE)` → `SELECT ... FOR UPDATE` |
| `PUT` | `/stock/row-lock` | 행 락 | JDBC `SELECT ... FOR UPDATE` (MyBatis 방식, JPA 우회) |
| `PUT` | `/stock/table-lock` | 테이블 락 | JDBC Native `LOCK TABLES product WRITE` |
| `PUT` | `/stock/app-lock` | 애플리케이션 락 | MariaDB `GET_LOCK()` / `RELEASE_LOCK()` |

### 4-3. 스레드 공유자원 (`/api/thread`)

#### 문제 케이스 (Thread-Unsafe)

| Method | Endpoint | 공유 자원 | 발생 문제 |
|--------|----------|-----------|----------|
| `POST` | `/unsafe/field` | `static int counter++` | Race Condition |
| `POST` | `/unsafe/list` | 공유 `ArrayList` | `ConcurrentModificationException` / 데이터 유실 |
| `POST` | `/unsafe/check-then-act` | `if stock > 0 → stock--` | 복합 연산 비원자성 → 음수 재고 |
| `POST` | `/unsafe/singleton-field` | Spring Bean 인스턴스 필드 | 싱글톤 공유 상태 오염 |

#### 안전 케이스 (Thread-Safe)

| Method | Endpoint | 해결 방법 | 특징 |
|--------|----------|----------|------|
| `POST` | `/safe/atomic` | `AtomicInteger` | Lock-free, 고성능 |
| `POST` | `/safe/synchronized` | `synchronized` 블록 | 단순, 단일 JVM |
| `POST` | `/safe/reentrant-lock` | `ReentrantLock` | 타임아웃/공정성 설정 가능 |
| `POST` | `/safe/concurrent-map` | `ConcurrentHashMap` | 세그먼트 락 |
| `POST` | `/safe/stateless` | 상태 없는 서비스 | Spring 기본 패턴, 이상적 설계 |
| `GET` | `/result` | — | 현재 카운터 값 조회 (정확성 검증용) |

### 4-4. 플랫폼 스레드 vs 가상 스레드 (`/api/thread`)

| Method | Endpoint | 스레드 방식 | 시나리오 |
|--------|----------|------------|---------|
| `GET` | `/platform/io-task` | 플랫폼 스레드 | `Thread.sleep(100ms)` I/O 대기 시뮬레이션 |
| `GET` | `/virtual/io-task` | 가상 스레드 | 동일 로직, 가상 스레드로 실행 |
| `GET` | `/platform/cpu-task` | 플랫폼 스레드 | CPU 집약 연산 |
| `GET` | `/virtual/cpu-task` | 가상 스레드 | 동일 CPU 연산 (차이 없음 확인용) |

---

## 5. JMeter 테스트 계획 (`test-plan.jmx`)

### Thread Group 구성

| # | 이름 | 동시 사용자 | Ramp-up | 반복 | 측정 포인트 |
|---|------|------------|---------|------|------------|
| 1 | CRUD - 생성 부하 | 50명 | 10초 | 10회 | INSERT TPS, 응답시간 |
| 2 | CRUD - 단건 조회 부하 | 50명 | 10초 | 20회 | SELECT TPS, PK 조회 속도 |
| 3 | CRUD - 혼합 시나리오 | 50명 | 15초 | 1회 | 생성→조회→수정 흐름 |
| 4 | 낙관적 락 경합 | 20명 | 5초 | 10회 | 409 오류율, OptimisticLockException 빈도 |
| 5 | 비관적 락 경합 | 20명 | 5초 | 10회 | 응답시간 증가, 대기 큐 형성 |
| 6 | 행 락 경합 | 20명 | 5초 | 10회 | 비관적 락과 응답시간 비교 |
| 7 | 테이블 락 경합 | 10명 | 3초 | 5회 | 전체 테이블 블로킹 효과 확인 |
| 8 | 애플리케이션 락 | 20명 | 5초 | 10회 | DB 부하 없는 직렬화 처리 |
| 9 | Unsafe vs Safe 카운터 | 30명 | 5초 | 10회 | 최종 카운터 값으로 Race Condition 검증 |
| 10 | 플랫폼 vs 가상 스레드 I/O | 100명 | 10초 | 5회 | TPS, 응답시간 비교 |
| 11 | 플랫폼 vs 가상 스레드 CPU | 50명 | 10초 | 5회 | CPU 작업에서 차이 없음 확인 |

### JMeter 리스너

- **View Results Tree** — 개별 요청/응답 상세 (디버깅용)
- **Summary Report** — TPS, 평균/최소/최대 응답시간, 오류율
- **Response Time Graph** — 시간대별 응답시간 추이
- **Active Threads Over Time** — 동시 사용자 수 추이

---

## 6. Gradle 연동

- **플러그인:** `com.github.jmeter-gradle-plugin` (또는 `jmeter` 태스크 커스텀)
- **실행 명령:** `./gradlew :playground-jmeter:jmeter`
- **결과 경로:** `playground-jmeter/build/jmeter-reports/`
- **GUI 실행:** JMeter 설치 후 `jmeter/test-plan.jmx` 직접 열기

---

## 7. 주석 정책 (이 모듈 한정)

이 모듈은 **학습용 튜토리얼**이므로 일반 컨벤션(Why만 주석)과 달리 **What + Why** 모두 설명하는 교육용 주석을 작성한다.

- 각 락 전략의 **동작 원리**와 **언제 사용하는지** 설명
- Thread-Unsafe 코드는 **어떤 타이밍에 문제가 생기는지** 시나리오 설명
- 가상 스레드는 **플랫폼 스레드와의 차이점** 및 **적합한 사용 케이스** 설명

---

## 8. 핵심 결정사항 요약

| 결정 | 내용 | 이유 |
|------|------|------|
| Java 21 (모듈 한정) | `playground-jmeter`만 Java 21 적용 | 가상 스레드 사용, 기존 모듈 영향 없음 |
| 포트 8090 | Application(8089)과 분리 | 두 모듈 동시 실행 가능 |
| 동시 사용자 최대 100명 | 로컬 환경 성능 제약 고려 | JMeter + App + DB 동일 머신 실행 |
| 낙관적 락 재시도 없음 | 409 즉시 반환 | JMeter에서 오류율로 경합 빈도 측정하기 위함 |
| `lab` DB 재사용 | 별도 DB 생성 없음 | 기존 환경 활용, `ddl-auto=create`로 테이블 자동 생성 |
