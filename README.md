# Spring Playground

Spring Boot **Gradle 멀티모듈** 구조로 다양한 기능/기술을 독립적으로 실험해보는 학습용 저장소입니다. 각 `playground-*` 모듈은 하나의 주제(AOP, 캐시, 배치, 파일 I/O 등)를 독립적으로 검증하고, `app` 모듈에서 필요한 모듈을 조합해 단일 서버로 통합 실행합니다.

## 모듈 구성

| 모듈 | 주제 |
|---|---|
| `core` | 실행되지 않는 계약(인터페이스) 모듈 |
| `app` | 여러 playground 모듈을 조합해 실행하는 통합 서버 (8089) |
| `playground-aop` | Spring AOP 실험 |
| `playground-batch` | Spring Batch 실험 |
| `playground-cache` | 캐시 전략 실험 |
| `playground-cisMockApi` | Mock API 서버 실험 |
| `playground-crolling` | 크롤링 실험 |
| `playground-exception` | 예외 처리 전략 실험 |
| `playground-exteriorConfig` | 외부 설정/프로퍼티 관리 실험 |
| `playground-fileIO` | 파일 업/다운로드 실험 |
| `playground-init-test` | 초기화/테스트 셋업 실험 |
| `playground-jdbc` | 순수 JDBC 실험 |
| `playground-jmeter` | 부하 테스트(JMeter) 연동 |
| `playground-kms` | 키 관리(암복호화) 실험 |
| `playground-lang` | 언어/문법 실험 |
| `playground-notiCheck` | 알림 처리 실험 |
| `playground-paging` | 페이징 처리 실험 |
| `playground-thread` | 멀티스레딩 실험 |
| `playground-validation` | 유효성 검증 실험 |

## 실행 방법

```bash
./gradlew :app:bootRun   # 통합 서버 (전체 모듈 조합, port 8089)
./gradlew :playground-aop:bootRun   # 개별 모듈만 단독 실행
```

## 아키텍처 가이드 (Core & Playground)

아래는 멀티모듈 구조에서 **core 모듈의 역할과 단일 Application(app) 실행 전략**을 정리한 가이드입니다.

---

## 1. 전체 아키텍처 개요

본 프로젝트는 Gradle 멀티모듈 구조를 사용합니다.

```
playground-*  ─────▶  core
app           ─────▶  playground-*
app           ─────▶  core
```

- `core` : 계약(인터페이스) 모듈
- `playground-*` : 기능 구현 모듈
- `app` : 통합 실행 모듈 (단일 서버)

---

## 2. core 모듈의 역할

### 핵심 정의

> **core는 실행되지 않는 계약(Contract) 모듈이다.**

- `main()` 없음
- `@SpringBootApplication` 없음
- 단독 실행 불가

### core에 포함되는 것

- 인터페이스
- 공통 Enum / 정책
- 순수 유틸 로직(선택)

### core에 포함되면 안 되는 것

| 항목 | 이유 |
|---|---|
| @Service | 실행 책임 위반 |
| Controller | 웹 계층 아님 |
| DB 접근 | 환경 의존 |
| 외부 API 호출 | 구현 책임 |

---

## 3. playground-* 모듈의 역할

- 기능 단위 모듈
- 독립 실행 가능
- 필요 시 단독 테스트 용도

예:
- playground-aop
- playground-cache
- playground-crolling

---

## 4. 단일 서버(8089)로 모든 기능 사용하기

### ❌ 잘못된 접근

> core를 실행하면 모든 모듈을 쓸 수 있지 않을까?

→ **틀림**

core는 절대 실행 대상이 아님

---

### ⭕ 정답: 통합 실행 모듈(app)

```
practice
├── core
├── app            ← 실행 전용
├── playground-aop
├── playground-cache
└── playground-crolling
```

---

## 5. app 모듈의 역할

> **실제 서버를 띄우는 유일한 Spring Boot 애플리케이션**

- 포트: 8089
- playground 모듈 조합
- 운영 / 통합 테스트용

---

## 6. app 모듈 build.gradle 예시

```gradle
dependencies {
    implementation project(':core')
    implementation project(':playground-aop')
    implementation project(':playground-cache')
    implementation project(':playground-crolling')

    implementation 'org.springframework.boot:spring-boot-starter-web'
}
```

---

## 7. AppApplication 예시

```java
@SpringBootApplication(scanBasePackages = {
    "com.example.app",
    "com.example.core",
    "com.example.playground"
})
public class AppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
```

---

## 8. 포트 설정

```yaml
server:
  port: 8089
```

---

## 9. 실행 기준 정리

| 목적 | 실행 대상 |
|---|---|
| 단일 기능 테스트 | playground-* |
| 전체 기능 통합 | app (8089) |

---

## 10. 최종 결론

- core는 **절대 실행하지 않는다**
- 여러 모듈을 함께 쓰려면 **통합 Application(app)을 만든다**
- playground는 기능 단위, app은 조합 단위

> **core는 약속이고, app은 실행기다**
