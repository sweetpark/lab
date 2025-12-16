
# 멀티모듈 아키텍처 가이드 (Core & Playground)

이 문서는 본 프로젝트에서 사용하는 **멀티모듈 구조**와 그중에서도 **core 모듈의 역할, 그리고 단일 Application(app) 실행 전략**을 정리한 가이드입니다.

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
- playground-crawling

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
└── playground-crawling
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
    implementation project(':playground-crawling')

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
