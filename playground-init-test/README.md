# Playground Init Test Module

이 모듈은 초기화 데이터(JSON 파일)를 읽어 데이터베이스에 적재하는 자동화 툴입니다.
개발 및 테스트 환경 구성을 위해 다양한 테이블 데이터를 동적으로 생성하고, 필요한 변수(`MID`, `CPID` 등)를 치환하여 DB에 Insert 합니다.

## 주요 기능

1.  **JSON 기반 데이터 정의**: `resources/data` 디렉토리 하위의 JSON 파일에 정의된 스키마와 데이터를 읽어들입니다.
2.  **동적 변수 치환**: 데이터 내의 `${MID}`, `${CO_NO}`와 같은 플레이스홀더를 실행 시점의 실제 값으로 자동 치환합니다.
3.  **데이터 전처리**:
    *   **암호화**: `ENC_HASH`, `PASSWORD`, `OTP` 등의 타입에 따라 자동으로 데이터를 암호화합니다.
    *   **날짜 처리**: `CUR_YYMMDD`, `YESTER_YYMMDD` 등의 키워드를 현재 또는 과거 날짜로 변환합니다.
4.  **테이블별 전략 처리**: 데이터의 분류(Division)에 따라 적절한 처리 전략을 선택하여 로직을 수행합니다.

## 아키텍처 및 디자인 패턴

이 프로젝트는 유지보수성과 확장성을 높이기 위해 다음과 같은 디자인 패턴을 적극적으로 활용하고 있습니다.

### 1. Strategy Pattern (전략 패턴)
테이블(데이터)의 종류에 따라 서로 다른 처리 로직을 캡슐화하고, 실행 시점에 적절한 전략을 선택하도록 설계되었습니다.

*   **Interface**: `MetaDataProcessStrategy`
*   **Concrete Strategies**:
    *   `MerchantProcessStrategy`: 가맹점(MID) 관련 데이터 처리 (1:N 복제 등)
    *   `AffiliateProcessStrategy`: 제휴사(CPID) 관련 데이터 처리
    *   `ContractProcessStrategy`: 계약 정보 처리
    *   `GroupProcessStrategy`, `VirtualProcessStrategy`: 그룹/가상계좌 처리
    *   `DefaultProcessStrategy`: 일반 데이터 처리
*   **Role**: `ToolRunner`에서 `MetaData`의 속성(`supports()`)을 확인하여 적절한 전략을 선택해 실행합니다.

### 2. Template Method Pattern (템플릿 메서드 패턴)
여러 전략 클래스에서 공통적으로 발생하는 "데이터 변환 -> 전처리 -> DB 저장"의 흐름을 상위 클래스에 정의하고, 하위 클래스에서는 세부적인 설정(변수 컨텍스트 등)만 제공하도록 구조화했습니다.

*   **Abstract Class**: `AbstractMetaDataProcessStrategy`
    *   `transformAndSave(template, variables)`: 템플릿 데이터를 복사하고, 변수를 치환한 뒤, 전처리 및 저장을 수행하는 공통 알고리즘(템플릿 메서드)을 구현했습니다.
*   **Usage**: 각 전략 클래스(`MerchantProcessStrategy` 등)는 이 클래스를 상속받아 중복 코드를 제거하고 핵심 비즈니스 로직에만 집중합니다.

### 3. Builder / Assembler Pattern (유사)
초기화에 필요한 복잡한 데이터 객체(`InitData`)를 여러 소스(프로퍼티, DB 등)로부터 모아 조립하는 역할을 분리했습니다.

*   **Class**: `InitDataAssembler`
*   **Role**: `Environment`(설정 파일)와 `DataSource`(DB)에서 정보를 조회하여 `InitData`(`MidInitData`, `CpidMap`) 객체를 생성 및 반환합니다.

### 4. Utility Class & Context Object
데이터 치환 로직을 유연하게 만들기 위해 전용 리졸버와 컨텍스트 객체를 사용합니다.

*   **`DataVariableResolver`**: 특정 DTO에 종속되지 않고, `Map<String, String>` 형태의 변수 맵만 있으면 모든 `${VARIABLE}` 패턴을 치환해주는 범용 유틸리티입니다.
*   **`VariableContext`**: 복잡한 DTO(`MidInitData`, `CpidMap`)를 `DataVariableResolver`가 이해할 수 있는 단순 변수 맵으로 변환해주는 역할을 합니다.

## 실행 방법

이 모듈은 Spring Boot 기반의 커맨드라인 애플리케이션으로 동작합니다.
`!test` 프로파일에서 활성화되며, 실행 후 트랜잭션 롤백을 통해 DB 상태를 원복하거나(테스트용), 실제 데이터를 커밋할 수 있습니다. (현재 코드는 `RuntimeException`을 통해 강제 롤백되도록 설정되어 있음)

```bash
./gradlew :playground-init-test:bootRun
```
