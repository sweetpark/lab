# JMeter 학습 가이드

Apache JMeter를 처음 접하는 개발자를 위한 단계별 학습 문서입니다.
`playground-jmeter` 모듈의 실제 테스트 파일과 함께 실습할 수 있도록 구성했습니다.

---

## 학습 로드맵

| 단계 | 문서 | 핵심 내용 |
|------|------|-----------|
| 기본 | [01-기본.md](./01-기본.md) | 설치, GUI 구조 이해, 첫 번째 요청 실행 |
| 초급 | [02-초급.md](./02-초급.md) | Thread Group, Sampler, Listener 구성, 결과 읽기 |
| 중급 | [03-중급.md](./03-중급.md) | 기능 개발 후 성능 테스트 프로세스, 결과 분석 |
| 고급 | [04-고급.md](./04-고급.md) | Assertion, 변수/CSV, 상관관계 추출, 플러그인 |
| 심화 | [05-심화.md](./05-심화.md) | 실무 패턴, CI/CD 연동, 분산 부하 테스트 |
| 환경 | [06-docker-환경구축.md](./06-docker-환경구축.md) | Docker로 격리 환경 구축, 정밀 측정 세팅 |

---

## 이 모듈의 JMX 파일 위치

```
playground-jmeter/jmeter/
├── test-plan.jmx              # 전체 시나리오 통합 테스트
└── plans/
    ├── 01-crud.jmx            # CRUD 부하 테스트
    ├── 02-lock.jmx            # DB 락 경합 비교
    ├── 03-thread-safety.jmx   # Race Condition 검증
    └── 04-virtual-thread.jmx  # 플랫폼 vs 가상 스레드 비교
```

---

## 빠른 시작 (이미 설치된 경우)

```powershell
# 1. 앱 실행
./gradlew :playground-jmeter:bootRun

# 2. JMeter GUI 실행
C:\...\apache-jmeter-5.6.3\bin\jmeter.bat

# 3. File → Open → playground-jmeter/jmeter/plans/01-crud.jmx
# 4. Ctrl+R 로 실행
```
