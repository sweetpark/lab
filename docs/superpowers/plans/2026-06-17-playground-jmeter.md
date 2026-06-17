# playground-jmeter 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** JMeter 성능 테스트 학습용 모듈 `playground-jmeter`를 구현한다. CRUD 성능, DB 락 경합(5종), 스레드 공유자원 안전성(9종), 플랫폼 스레드 vs 가상 스레드 성능을 JMeter로 직접 측정할 수 있다.

**Architecture:** 4개의 독립 패키지(crud / lock / thread.safety / thread.virtual)로 분리한다. 유일한 cross-package 의존은 `lock` 패키지가 `crud.entity.Product`를 참조하는 것이며, 비즈니스 로직은 절대 공유하지 않는다. 락 종류별로 Service 클래스를 1:1 분리해 각 전략의 동작을 독립적으로 관찰할 수 있게 설계한다.

**Tech Stack:** Spring Boot 3.4.4, Java 21, Spring Data JPA, JdbcTemplate, MariaDB(lab DB 재사용), JMeter 5.6+, com.github.jmeter-gradle-plugin 1.7.0

---

## 파일 맵

```
playground-jmeter/
├── build.gradle
├── src/main/java/com/example/playground/jmeter/
│   ├── JmeterApplication.java
│   ├── config/
│   │   └── VirtualThreadConfig.java
│   ├── crud/
│   │   ├── controller/ProductController.java
│   │   ├── service/ProductService.java
│   │   ├── repository/ProductRepository.java
│   │   ├── entity/Product.java
│   │   └── dto/
│   │       ├── ProductRequest.java
│   │       └── ProductResponse.java
│   ├── lock/
│   │   ├── controller/LockController.java
│   │   ├── service/
│   │   │   ├── OptimisticLockService.java
│   │   │   ├── PessimisticLockService.java
│   │   │   ├── RowLockService.java
│   │   │   ├── TableLockService.java
│   │   │   └── AppLockService.java
│   │   ├── repository/LockProductRepository.java
│   │   └── dto/StockRequest.java
│   └── thread/
│       ├── safety/
│       │   ├── controller/
│       │   │   ├── UnsafeThreadController.java
│       │   │   ├── SafeThreadController.java
│       │   │   └── ThreadResultController.java
│       │   └── service/
│       │       ├── unsafe/
│       │       │   ├── UnsafeFieldService.java
│       │       │   ├── UnsafeListService.java
│       │       │   ├── UnsafeCheckThenActService.java
│       │       │   └── UnsafeSingletonFieldService.java
│       │       └── safe/
│       │           ├── SafeAtomicService.java
│       │           ├── SafeSynchronizedService.java
│       │           ├── SafeReentrantLockService.java
│       │           ├── SafeConcurrentMapService.java
│       │           └── SafeStatelessService.java
│       └── virtual/
│           ├── controller/VirtualThreadController.java
│           └── service/
│               ├── PlatformThreadService.java
│               └── VirtualThreadService.java
├── src/main/resources/application.properties
└── jmeter/
    ├── test-plan.jmx
    └── plans/
        ├── 01-crud.jmx
        ├── 02-lock.jmx
        ├── 03-thread-safety.jmx
        └── 04-virtual-thread.jmx
```

---

## Phase 1 — 모듈 기반 설정

### Task 1: 모듈 등록 및 Gradle 설정

**Files:**
- Modify: `settings.gradle`
- Create: `playground-jmeter/build.gradle`
- Create: `playground-jmeter/src/main/resources/application.properties`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/JmeterApplication.java`

- [ ] **Step 1: settings.gradle에 모듈 추가**

```groovy
// settings.gradle 마지막 줄에 추가
include 'playground-jmeter'
```

- [ ] **Step 2: build.gradle 작성**

```groovy
// playground-jmeter/build.gradle
plugins {
    id 'org.springframework.boot' version '3.4.4'
    id 'io.spring.dependency-management'
}

// 이 모듈만 Java 21을 사용한다. 가상 스레드(Virtual Thread)는 Java 21에서 정식 출시된 기능이다.
// 기존 모듈들은 Java 17을 유지하므로 전체 toolchain은 건드리지 않는다.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

    // RowLockService, TableLockService에서 JPA를 우회해 직접 SQL을 실행하기 위해 필요
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'

    runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
}
```

- [ ] **Step 3: application.properties 작성**

```properties
spring.application.name=playground-jmeter
server.port=8090

# 기존 lab DB를 재사용한다. 테이블은 ddl-auto=create로 자동 생성된다.
spring.datasource.url=jdbc:mariadb://localhost:3306/lab
spring.datasource.username=${username}
spring.datasource.password=${password}
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

logging.level.com.example.playground.jmeter=DEBUG
```

- [ ] **Step 4: JmeterApplication.java 작성**

```java
package com.example.playground.jmeter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JmeterApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmeterApplication.class, args);
    }
}
```

- [ ] **Step 5: 빌드 확인**

```bash
./gradlew :playground-jmeter:build
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 6: git add**

```bash
git add settings.gradle playground-jmeter/
```

---

## Phase 2 — CRUD 패키지

### Task 2: Product 엔티티 + Repository + DTO

**Files:**
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/crud/entity/Product.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/crud/repository/ProductRepository.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/crud/dto/ProductRequest.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/crud/dto/ProductResponse.java`

- [ ] **Step 1: Product 엔티티 작성**

```java
package com.example.playground.jmeter.crud.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer price;

    // 락 경합 테스트의 핵심 자원. 여러 스레드가 동시에 이 값을 차감하려 할 때 어떤 일이 벌어지는지 관찰한다.
    private Integer stock;

    // 낙관적 락(Optimistic Lock)의 버전 컬럼.
    // JPA는 UPDATE 시 WHERE version = :현재버전 조건을 자동으로 추가한다.
    // 다른 트랜잭션이 먼저 수정해 version이 바뀌었다면 UPDATE 결과가 0건이 되고 OptimisticLockException을 던진다.
    @Version
    private Long version;

    private LocalDateTime createdAt;

    @Builder
    public Product(String name, Integer price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.createdAt = LocalDateTime.now();
    }

    public void decreaseStock(int amount) {
        if (this.stock < amount) {
            throw new IllegalStateException("재고 부족: 현재 재고=" + this.stock + ", 요청=" + amount);
        }

        this.stock -= amount;
    }

    public void update(String name, Integer price) {
        this.name = name;
        this.price = price;
    }
}
```

- [ ] **Step 2: ProductRepository 작성**

```java
package com.example.playground.jmeter.crud.repository;

import com.example.playground.jmeter.crud.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

- [ ] **Step 3: DTO 작성**

```java
// ProductRequest.java
package com.example.playground.jmeter.crud.dto;

// Java 16+ record: 불변 DTO. getter/equals/hashCode/toString을 자동 생성한다.
public record ProductRequest(String name, Integer price, Integer stock) {
}
```

```java
// ProductResponse.java
package com.example.playground.jmeter.crud.dto;

import com.example.playground.jmeter.crud.entity.Product;
import java.time.LocalDateTime;

public record ProductResponse(Long id, String name, Integer price, Integer stock, Long version, LocalDateTime createdAt) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getVersion(),
                product.getCreatedAt()
        );
    }
}
```

- [ ] **Step 4: git add**

```bash
git add playground-jmeter/src/main/java/com/example/playground/jmeter/crud/
```

---

### Task 3: ProductService + ProductController

**Files:**
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/crud/service/ProductService.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/crud/controller/ProductController.java`

- [ ] **Step 1: ProductService 작성**

```java
package com.example.playground.jmeter.crud.service;

import com.example.playground.jmeter.crud.dto.ProductRequest;
import com.example.playground.jmeter.crud.dto.ProductResponse;
import com.example.playground.jmeter.crud.entity.Product;
import com.example.playground.jmeter.crud.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .price(request.price())
                .stock(request.stock())
                .build();

        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: id=" + id));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: id=" + id));

        product.update(request.name(), request.price());

        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
```

- [ ] **Step 2: ProductController 작성**

```java
package com.example.playground.jmeter.crud.controller;

import com.example.playground.jmeter.crud.dto.ProductRequest;
import com.example.playground.jmeter.crud.dto.ProductResponse;
import com.example.playground.jmeter.crud.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

- [ ] **Step 3: 애플리케이션 실행 후 smoke test**

```bash
# 애플리케이션 실행 (별도 터미널)
./gradlew :playground-jmeter:bootRun

# 상품 생성
curl -X POST http://localhost:8090/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"테스트상품","price":10000,"stock":100}'

# 전체 조회
curl http://localhost:8090/api/products
```

Expected: 상품 생성 응답에 `id`, `version:0` 포함

- [ ] **Step 4: git add**

```bash
git add playground-jmeter/src/main/java/com/example/playground/jmeter/crud/
```

---

## Phase 3 — Lock 패키지

### Task 4: LockProductRepository + OptimisticLockService + PessimisticLockService

**Files:**
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/lock/repository/LockProductRepository.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/lock/dto/StockRequest.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/lock/service/OptimisticLockService.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/lock/service/PessimisticLockService.java`

- [ ] **Step 1: StockRequest DTO 작성**

```java
package com.example.playground.jmeter.lock.dto;

public record StockRequest(int amount) {
}
```

- [ ] **Step 2: LockProductRepository 작성**

```java
package com.example.playground.jmeter.lock.repository;

import com.example.playground.jmeter.crud.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LockProductRepository extends JpaRepository<Product, Long> {

    // 비관적 쓰기 락: 조회 시점에 DB 행에 배타적 잠금을 건다(SELECT ... FOR UPDATE).
    // 다른 트랜잭션은 이 트랜잭션이 커밋 또는 롤백될 때까지 해당 행에 접근할 수 없다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithPessimisticLock(@Param("id") Long id);
}
```

- [ ] **Step 3: OptimisticLockService 작성**

```java
package com.example.playground.jmeter.lock.service;

import com.example.playground.jmeter.crud.entity.Product;
import com.example.playground.jmeter.lock.repository.LockProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimisticLockService {

    private final LockProductRepository lockProductRepository;

    // 낙관적 락(Optimistic Lock) 전략.
    //
    // [동작 원리]
    // 1. 트랜잭션 A, B가 동시에 같은 상품을 조회 → 둘 다 version=5를 읽는다.
    // 2. A가 먼저 UPDATE 성공 → DB의 version이 6으로 증가한다.
    // 3. B가 UPDATE 시도: WHERE version=5 조건이 맞지 않아 수정된 행이 0건 → JPA가 OptimisticLockException을 던진다.
    //
    // [언제 쓰나] 충돌이 드문 환경(읽기 위주). 충돌 시 재시도 비용이 발생하므로
    // 충돌이 잦은 환경에서는 비관적 락보다 오히려 느릴 수 있다.
    @Transactional
    public void decreaseStock(Long productId, int amount) {
        Product product = lockProductRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: id=" + productId));

        // OptimisticLockException은 트랜잭션 커밋 시점에 발생한다.
        // 여기서는 재시도 없이 바로 예외를 전파해 JMeter에서 오류율로 관찰한다.
        product.decreaseStock(amount);
    }
}
```

- [ ] **Step 4: PessimisticLockService 작성**

```java
package com.example.playground.jmeter.lock.service;

import com.example.playground.jmeter.crud.entity.Product;
import com.example.playground.jmeter.lock.repository.LockProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PessimisticLockService {

    private final LockProductRepository lockProductRepository;

    // 비관적 락(Pessimistic Lock) 전략.
    //
    // [동작 원리]
    // SELECT ... FOR UPDATE로 행 조회 시 즉시 배타 락을 획득한다.
    // 다른 트랜잭션은 이 락이 해제될 때까지 대기한다(큐 직렬화).
    // 트랜잭션이 짧으면 TPS 감소가 적지만, 길어질수록 대기 스레드가 쌓여 응답시간이 급증한다.
    //
    // [언제 쓰나] 충돌이 잦고 데이터 정합성이 반드시 보장되어야 할 때.
    // 예: 한정판 재고 차감, 포인트 잔액 변경
    @Transactional
    public void decreaseStock(Long productId, int amount) {
        // 이 시점에 SELECT ... FOR UPDATE 실행 → 행 락 획득
        Product product = lockProductRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: id=" + productId));

        product.decreaseStock(amount);
        // 트랜잭션 커밋 시 락 해제 → 다음 대기 트랜잭션이 락 획득
    }
}
```

- [ ] **Step 5: git add**

```bash
git add playground-jmeter/src/main/java/com/example/playground/jmeter/lock/
```

---

### Task 5: RowLockService + TableLockService + AppLockService

**Files:**
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/lock/service/RowLockService.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/lock/service/TableLockService.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/lock/service/AppLockService.java`

- [ ] **Step 1: RowLockService 작성**

```java
package com.example.playground.jmeter.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RowLockService {

    private final JdbcTemplate jdbcTemplate;

    // 행 락(Row Lock) 전략 — JDBC 직접 사용.
    //
    // [비관적 락과의 차이]
    // 비관적 락(PessimisticLockService)은 JPA @Lock 어노테이션으로 처리한다.
    // 행 락은 동일한 SELECT ... FOR UPDATE를 JDBC로 직접 실행해 JPA 캐시를 우회한다.
    // 결과는 비관적 락과 동일하지만, 이 방식은 JPA 없이 MyBatis/순수 JDBC 환경에서 쓰인다.
    // JMeter에서 두 엔드포인트의 응답시간을 비교해 JPA 오버헤드를 측정할 수 있다.
    @Transactional
    public void decreaseStock(Long productId, int amount) {
        // FOR UPDATE: InnoDB의 행 수준 잠금. 동일 행에 대한 다른 트랜잭션의 쓰기를 대기시킨다.
        Integer stock = jdbcTemplate.queryForObject(
                "SELECT stock FROM product WHERE id = ? FOR UPDATE",
                Integer.class,
                productId
        );

        if (stock == null || stock < amount) {
            throw new IllegalStateException("재고 부족: 현재=" + stock + ", 요청=" + amount);
        }

        jdbcTemplate.update(
                "UPDATE product SET stock = stock - ? WHERE id = ?",
                amount, productId
        );
    }
}
```

- [ ] **Step 2: TableLockService 작성**

```java
package com.example.playground.jmeter.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableLockService {

    private final JdbcTemplate jdbcTemplate;

    // 테이블 락(Table Lock) 전략.
    //
    // [행 락 vs 테이블 락]
    // 행 락: 특정 행만 잠금 → 다른 행은 동시 접근 가능 (InnoDB 기본)
    // 테이블 락: 테이블 전체를 잠금 → product 테이블 모든 행에 대한 쓰기 차단
    //
    // [주의] LOCK TABLES는 현재 트랜잭션을 암묵적으로 커밋하고 Spring 트랜잭션 관리와 충돌한다.
    // 따라서 @Transactional을 사용하지 않고 LOCK/UNLOCK을 직접 관리한다.
    // JMeter 테스트에서 테이블 락 구간에 다른 상품 조회도 블로킹되는지 확인할 수 있다.
    public void decreaseStock(Long productId, int amount) {
        try {
            // 테이블 전체에 쓰기 락 획득
            jdbcTemplate.execute("LOCK TABLES product WRITE");

            Integer stock = jdbcTemplate.queryForObject(
                    "SELECT stock FROM product WHERE id = ?",
                    Integer.class,
                    productId
            );

            if (stock == null || stock < amount) {
                throw new IllegalStateException("재고 부족: 현재=" + stock + ", 요청=" + amount);
            }

            jdbcTemplate.update(
                    "UPDATE product SET stock = stock - ? WHERE id = ?",
                    amount, productId
            );

        } finally {
            // 반드시 finally에서 락 해제. 예외 발생 시에도 락이 남아 전체 서비스가 멈추는 사태를 방지한다.
            jdbcTemplate.execute("UNLOCK TABLES");
        }
    }
}
```

- [ ] **Step 3: AppLockService 작성**

```java
package com.example.playground.jmeter.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppLockService {

    private final JdbcTemplate jdbcTemplate;

    // 애플리케이션 락(Application Lock) 전략 — MariaDB GET_LOCK() 사용.
    //
    // [동작 원리]
    // GET_LOCK('이름', 타임아웃): 지정한 이름의 사용자 정의 잠금을 획득한다.
    // 동일 이름의 락을 다른 커넥션이 보유 중이면 타임아웃까지 대기한다.
    // DB 행/테이블과 무관하게 비즈니스 로직 레벨에서 직렬화할 수 있다.
    //
    // [언제 쓰나] 여러 테이블에 걸친 복합 연산을 하나의 임계 구역으로 묶을 때.
    // Redis가 없는 환경에서 분산 락의 간단한 대안으로 사용한다.
    //
    // [주의] GET_LOCK은 커넥션 단위로 동작한다. HikariCP처럼 커넥션 풀을 사용할 때
    // 락 획득과 해제가 반드시 같은 커넥션에서 이루어져야 한다.
    @Transactional
    public void decreaseStock(Long productId, int amount) {
        String lockName = "product_stock_lock_" + productId;

        // 타임아웃 5초: 5초 내 락을 못 얻으면 0(실패) 반환
        Integer acquired = jdbcTemplate.queryForObject(
                "SELECT GET_LOCK(?, 5)",
                Integer.class,
                lockName
        );

        if (acquired == null || acquired != 1) {
            throw new IllegalStateException("락 획득 실패: " + lockName);
        }

        try {
            Integer stock = jdbcTemplate.queryForObject(
                    "SELECT stock FROM product WHERE id = ?",
                    Integer.class,
                    productId
            );

            if (stock == null || stock < amount) {
                throw new IllegalStateException("재고 부족: 현재=" + stock + ", 요청=" + amount);
            }

            jdbcTemplate.update(
                    "UPDATE product SET stock = stock - ? WHERE id = ?",
                    amount, productId
            );

        } finally {
            // 락 해제를 finally에서 보장한다. 해제하지 않으면 커넥션이 반환될 때까지 락이 유지된다.
            jdbcTemplate.execute("SELECT RELEASE_LOCK('" + lockName + "')");
        }
    }
}
```

- [ ] **Step 4: git add**

```bash
git add playground-jmeter/src/main/java/com/example/playground/jmeter/lock/service/
```

---

### Task 6: LockController

**Files:**
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/lock/controller/LockController.java`

- [ ] **Step 1: LockController 작성**

```java
package com.example.playground.jmeter.lock.controller;

import com.example.playground.jmeter.lock.dto.StockRequest;
import com.example.playground.jmeter.lock.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/products/{id}/stock")
@RequiredArgsConstructor
public class LockController {

    private final OptimisticLockService optimisticLockService;
    private final PessimisticLockService pessimisticLockService;
    private final RowLockService rowLockService;
    private final TableLockService tableLockService;
    private final AppLockService appLockService;

    // 낙관적 락: 충돌 시 409 Conflict 반환. JMeter에서 오류율로 경합 빈도를 측정한다.
    @PutMapping("/optimistic")
    public ResponseEntity<String> optimistic(@PathVariable Long id, @RequestBody StockRequest request) {
        try {
            optimisticLockService.decreaseStock(id, request.amount());
            return ResponseEntity.ok("낙관적 락 성공");

        } catch (OptimisticLockingFailureException e) {
            log.warn("낙관적 락 충돌 발생: productId={}", id);
            return ResponseEntity.status(409).body("충돌 발생 - 다른 트랜잭션이 먼저 수정함");
        }
    }

    @PutMapping("/pessimistic")
    public ResponseEntity<String> pessimistic(@PathVariable Long id, @RequestBody StockRequest request) {
        pessimisticLockService.decreaseStock(id, request.amount());
        return ResponseEntity.ok("비관적 락 성공");
    }

    @PutMapping("/row-lock")
    public ResponseEntity<String> rowLock(@PathVariable Long id, @RequestBody StockRequest request) {
        rowLockService.decreaseStock(id, request.amount());
        return ResponseEntity.ok("행 락 성공");
    }

    @PutMapping("/table-lock")
    public ResponseEntity<String> tableLock(@PathVariable Long id, @RequestBody StockRequest request) {
        tableLockService.decreaseStock(id, request.amount());
        return ResponseEntity.ok("테이블 락 성공");
    }

    @PutMapping("/app-lock")
    public ResponseEntity<String> appLock(@PathVariable Long id, @RequestBody StockRequest request) {
        appLockService.decreaseStock(id, request.amount());
        return ResponseEntity.ok("애플리케이션 락 성공");
    }
}
```

- [ ] **Step 2: 애플리케이션 재시작 후 smoke test**

```bash
# 재고 100인 상품 생성 (id=1이라고 가정)
curl -X POST http://localhost:8090/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"락테스트상품","price":10000,"stock":100}'

# 낙관적 락 재고 차감 테스트
curl -X PUT http://localhost:8090/api/products/1/stock/optimistic \
  -H "Content-Type: application/json" \
  -d '{"amount":1}'
```

Expected: `"낙관적 락 성공"` 응답

- [ ] **Step 3: git add**

```bash
git add playground-jmeter/src/main/java/com/example/playground/jmeter/lock/
```

---

## Phase 4 — Thread Safety 패키지

### Task 7: Unsafe 서비스 4종

**Files:**
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/service/unsafe/UnsafeFieldService.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/service/unsafe/UnsafeListService.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/service/unsafe/UnsafeCheckThenActService.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/service/unsafe/UnsafeSingletonFieldService.java`

- [ ] **Step 1: UnsafeFieldService 작성**

```java
package com.example.playground.jmeter.thread.safety.service.unsafe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UnsafeFieldService {

    // [문제 케이스] static 공유 변수에 대한 Race Condition.
    //
    // counter++ 는 원자적 연산이 아니다. 내부적으로 3단계로 분리된다:
    //   1) 메모리에서 counter 값을 레지스터로 읽는다 (READ)
    //   2) 레지스터 값을 1 증가시킨다 (INCREMENT)
    //   3) 레지스터 값을 다시 메모리에 쓴다 (WRITE)
    //
    // 스레드 A가 1단계(READ)를 마치고 3단계(WRITE) 전에 스레드 B가 끼어들면,
    // 두 스레드 모두 같은 값을 읽어 각각 +1 해서 쓰게 된다 → 증가분 유실.
    //
    // JMeter로 30명이 각 10회 요청(총 300회)하면 counter가 300보다 작게 나온다.
    private static int counter = 0;

    public void increment() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }

    public void reset() {
        counter = 0;
    }
}
```

- [ ] **Step 2: UnsafeListService 작성**

```java
package com.example.playground.jmeter.thread.safety.service.unsafe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UnsafeListService {

    // [문제 케이스] 공유 ArrayList에 여러 스레드가 동시에 add().
    //
    // ArrayList는 내부 배열(Object[])의 크기가 가득 차면 더 큰 배열로 복사(grow)한다.
    // 두 스레드가 동시에 grow 타이밍에 진입하면 배열 인덱스가 충돌해 null 원소가 생기거나
    // ArrayIndexOutOfBoundsException / 데이터 유실이 발생한다.
    private final List<String> sharedList = new ArrayList<>();

    public void add(String value) {
        sharedList.add(value);
    }

    public int size() {
        return sharedList.size();
    }

    public void reset() {
        sharedList.clear();
    }
}
```

- [ ] **Step 3: UnsafeCheckThenActService 작성**

```java
package com.example.playground.jmeter.thread.safety.service.unsafe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UnsafeCheckThenActService {

    // [문제 케이스] Check-Then-Act 패턴의 비원자성.
    //
    // if (stock > 0) { stock-- } 는 두 단계로 구성된 복합 연산이다.
    // 스레드 A와 B가 동시에 if 조건을 통과하면, 둘 다 stock--를 실행해 stock이 음수가 된다.
    //
    //   [타임라인]
    //   Thread A: stock=1 확인(조건 true) → (컨텍스트 스위치)
    //   Thread B: stock=1 확인(조건 true) → stock-- → stock=0
    //   Thread A: stock-- → stock=-1  ← 음수 재고 발생!
    //
    // DB 락 없이 애플리케이션 레벨에서만 처리할 때 이 문제가 발생한다.
    private int stock = 100;

    public String decreaseStock() {
        if (stock > 0) {
            stock--;
            return "차감 성공. 남은 재고=" + stock;
        }

        return "재고 없음";
    }

    public int getStock() {
        return stock;
    }

    public void reset() {
        stock = 100;
    }
}
```

- [ ] **Step 4: UnsafeSingletonFieldService 작성**

```java
package com.example.playground.jmeter.thread.safety.service.unsafe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UnsafeSingletonFieldService {

    // [문제 케이스] Spring 싱글톤 빈(Bean)의 인스턴스 필드 공유.
    //
    // Spring @Service는 기본적으로 싱글톤이다.
    // 즉, 애플리케이션 전체에서 이 빈의 인스턴스가 하나만 존재한다.
    // 여러 HTTP 요청(스레드)이 동시에 이 빈을 호출하면 requestCount 필드를 공유한다.
    //
    // [흔한 실수] 요청 단위로 상태를 저장하려고 인스턴스 필드를 쓰는 경우.
    // 올바른 해결책: 상태를 메서드 지역 변수나 ThreadLocal에 두거나, 무상태 설계를 따른다.
    private int requestCount = 0;

    private String lastRequestId = "";

    public String process(String requestId) {
        requestCount++;
        lastRequestId = requestId;

        // 다른 스레드가 끼어들어 lastRequestId를 바꾸면 여기서 엉뚱한 값이 출력된다.
        return "처리완료. 총요청수=" + requestCount + ", 마지막요청=" + lastRequestId;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void reset() {
        requestCount = 0;
        lastRequestId = "";
    }
}
```

- [ ] **Step 5: git add**

```bash
git add playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/service/unsafe/
```

---

### Task 8: Safe 서비스 5종

**Files:**
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/service/safe/SafeAtomicService.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/service/safe/SafeSynchronizedService.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/service/safe/SafeReentrantLockService.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/service/safe/SafeConcurrentMapService.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/service/safe/SafeStatelessService.java`

- [ ] **Step 1: SafeAtomicService 작성**

```java
package com.example.playground.jmeter.thread.safety.service.safe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class SafeAtomicService {

    // [해결책 1] AtomicInteger — Lock-Free CAS(Compare-And-Swap) 방식.
    //
    // CPU 하드웨어 수준의 원자적 명령(CMPXCHG)을 사용한다.
    // incrementAndGet()은 "읽기-증가-쓰기"를 단일 원자 연산으로 처리하므로 스레드 간 충돌이 없다.
    //
    // [장점] synchronized보다 빠르다. 락이 없어 컨텍스트 스위치가 발생하지 않는다.
    // [단점] 단일 변수에만 적용 가능. 복합 연산(조건+변경)은 별도 처리가 필요하다.
    private final AtomicInteger counter = new AtomicInteger(0);

    public int increment() {
        return counter.incrementAndGet();
    }

    public int getCounter() {
        return counter.get();
    }

    public void reset() {
        counter.set(0);
    }
}
```

- [ ] **Step 2: SafeSynchronizedService 작성**

```java
package com.example.playground.jmeter.thread.safety.service.safe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SafeSynchronizedService {

    // [해결책 2] synchronized 블록.
    //
    // 한 번에 하나의 스레드만 이 블록에 진입할 수 있다(상호 배제).
    // 진입하지 못한 스레드는 모니터 락이 해제될 때까지 BLOCKED 상태로 대기한다.
    //
    // [장점] 문법이 직관적이고 Java 기본 제공.
    // [단점] 블로킹 방식이라 대기 스레드가 CPU를 낭비. 타임아웃 설정 불가.
    // 단일 JVM 환경에서만 유효하다(분산 환경에서는 효과 없음).
    private int counter = 0;

    public synchronized void increment() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }

    public synchronized void reset() {
        counter = 0;
    }
}
```

- [ ] **Step 3: SafeReentrantLockService 작성**

```java
package com.example.playground.jmeter.thread.safety.service.safe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class SafeReentrantLockService {

    // [해결책 3] ReentrantLock — synchronized의 고급 버전.
    //
    // synchronized와 동일한 상호 배제를 제공하지만 추가 기능이 있다:
    //   - tryLock(timeout): 지정 시간 내 락을 못 얻으면 포기한다(데드락 방지)
    //   - lockInterruptibly(): 대기 중 인터럽트 허용
    //   - fair=true: 공정 모드 - 먼저 기다린 스레드가 먼저 락 획득(기아 현상 방지)
    //
    // 이 예시에서는 tryLock으로 최대 3초를 기다리고, 실패하면 예외를 던진다.
    private final ReentrantLock lock = new ReentrantLock();

    private int counter = 0;

    public void increment() throws InterruptedException {
        boolean acquired = lock.tryLock(3, TimeUnit.SECONDS);

        if (!acquired) {
            throw new IllegalStateException("락 획득 시간 초과 (3초)");
        }

        try {
            counter++;

        } finally {
            // 반드시 finally에서 해제. 예외 발생 시에도 락이 풀려야 다른 스레드가 진입할 수 있다.
            lock.unlock();
        }
    }

    public int getCounter() {
        return counter;
    }

    public void reset() {
        counter = 0;
    }
}
```

- [ ] **Step 4: SafeConcurrentMapService 작성**

```java
package com.example.playground.jmeter.thread.safety.service.safe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class SafeConcurrentMapService {

    // [해결책 4] ConcurrentHashMap — 스레드 안전한 Map.
    //
    // HashMap은 멀티스레드 환경에서 무한 루프(CPU 100%) 또는 데이터 유실을 일으킨다.
    // ConcurrentHashMap은 버킷 단위 세그먼트 락을 사용해 동시 읽기/쓰기를 허용한다.
    //
    // compute()는 키에 대한 읽기-계산-쓰기를 원자적으로 처리한다.
    // 직접 get() → 계산 → put() 3단계로 나눠 쓰면 Race Condition이 재발한다.
    private final ConcurrentHashMap<String, AtomicInteger> countMap = new ConcurrentHashMap<>();

    public int increment(String key) {
        // computeIfAbsent: 키가 없으면 새 AtomicInteger(0)을 원자적으로 삽입
        return countMap.computeIfAbsent(key, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public int getCount(String key) {
        AtomicInteger value = countMap.get(key);
        return value == null ? 0 : value.get();
    }

    public void reset() {
        countMap.clear();
    }
}
```

- [ ] **Step 5: SafeStatelessService 작성**

```java
package com.example.playground.jmeter.thread.safety.service.safe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SafeStatelessService {

    // [해결책 5] 무상태(Stateless) 설계 — 가장 이상적인 방법.
    //
    // 인스턴스 필드에 상태를 두지 않는다.
    // 모든 데이터는 메서드 파라미터로 받고, 결과는 반환값으로 전달한다.
    // Spring @Service 빈을 싱글톤으로 설계하는 올바른 방법이다.
    //
    // 스레드마다 스택 프레임이 분리되므로 메서드 지역 변수는 자동으로 스레드 안전하다.
    public String process(String requestId, int value) {
        // 지역 변수: 각 스레드의 스택에 독립적으로 생성되므로 공유되지 않는다.
        int localResult = value * 2;

        return "requestId=" + requestId + ", result=" + localResult;
    }
}
```

- [ ] **Step 6: git add**

```bash
git add playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/service/safe/
```

---

### Task 9: Thread Safety 컨트롤러 3종

**Files:**
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/controller/UnsafeThreadController.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/controller/SafeThreadController.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/controller/ThreadResultController.java`

- [ ] **Step 1: UnsafeThreadController 작성**

```java
package com.example.playground.jmeter.thread.safety.controller;

import com.example.playground.jmeter.thread.safety.service.unsafe.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/thread/unsafe")
@RequiredArgsConstructor
public class UnsafeThreadController {

    private final UnsafeFieldService unsafeFieldService;
    private final UnsafeListService unsafeListService;
    private final UnsafeCheckThenActService unsafeCheckThenActService;
    private final UnsafeSingletonFieldService unsafeSingletonFieldService;

    @PostMapping("/field")
    public ResponseEntity<String> field() {
        unsafeFieldService.increment();
        return ResponseEntity.ok("카운터 증가 요청 완료");
    }

    @PostMapping("/list")
    public ResponseEntity<String> list(@RequestParam(defaultValue = "item") String value) {
        unsafeListService.add(value);
        return ResponseEntity.ok("리스트 추가 완료. 현재크기=" + unsafeListService.size());
    }

    @PostMapping("/check-then-act")
    public ResponseEntity<String> checkThenAct() {
        return ResponseEntity.ok(unsafeCheckThenActService.decreaseStock());
    }

    @PostMapping("/singleton-field")
    public ResponseEntity<String> singletonField(@RequestParam(defaultValue = "req") String requestId) {
        return ResponseEntity.ok(unsafeSingletonFieldService.process(requestId));
    }

    // JMeter 테스트 전 초기화 엔드포인트
    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        unsafeFieldService.reset();
        unsafeListService.reset();
        unsafeCheckThenActService.reset();
        unsafeSingletonFieldService.reset();
        return ResponseEntity.ok("초기화 완료");
    }
}
```

- [ ] **Step 2: SafeThreadController 작성**

```java
package com.example.playground.jmeter.thread.safety.controller;

import com.example.playground.jmeter.thread.safety.service.safe.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/thread/safe")
@RequiredArgsConstructor
public class SafeThreadController {

    private final SafeAtomicService safeAtomicService;
    private final SafeSynchronizedService safeSynchronizedService;
    private final SafeReentrantLockService safeReentrantLockService;
    private final SafeConcurrentMapService safeConcurrentMapService;
    private final SafeStatelessService safeStatelessService;

    @PostMapping("/atomic")
    public ResponseEntity<String> atomic() {
        int result = safeAtomicService.increment();
        return ResponseEntity.ok("AtomicInteger 증가. 현재=" + result);
    }

    @PostMapping("/synchronized")
    public ResponseEntity<String> synchronizedCounter() {
        safeSynchronizedService.increment();
        return ResponseEntity.ok("synchronized 증가 완료");
    }

    @PostMapping("/reentrant-lock")
    public ResponseEntity<String> reentrantLock() throws InterruptedException {
        safeReentrantLockService.increment();
        return ResponseEntity.ok("ReentrantLock 증가 완료");
    }

    @PostMapping("/concurrent-map")
    public ResponseEntity<String> concurrentMap(@RequestParam(defaultValue = "default") String key) {
        int count = safeConcurrentMapService.increment(key);
        return ResponseEntity.ok("ConcurrentHashMap 증가. key=" + key + ", count=" + count);
    }

    @PostMapping("/stateless")
    public ResponseEntity<String> stateless(
            @RequestParam(defaultValue = "req") String requestId,
            @RequestParam(defaultValue = "5") int value) {
        return ResponseEntity.ok(safeStatelessService.process(requestId, value));
    }

    // JMeter 테스트 전 초기화
    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        safeAtomicService.reset();
        safeSynchronizedService.reset();
        safeReentrantLockService.reset();
        safeConcurrentMapService.reset();
        return ResponseEntity.ok("초기화 완료");
    }
}
```

- [ ] **Step 3: ThreadResultController 작성**

```java
package com.example.playground.jmeter.thread.safety.controller;

import com.example.playground.jmeter.thread.safety.service.safe.*;
import com.example.playground.jmeter.thread.safety.service.unsafe.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/thread/result")
@RequiredArgsConstructor
public class ThreadResultController {

    private final UnsafeFieldService unsafeFieldService;
    private final SafeAtomicService safeAtomicService;
    private final SafeSynchronizedService safeSynchronizedService;
    private final SafeReentrantLockService safeReentrantLockService;
    private final UnsafeCheckThenActService unsafeCheckThenActService;

    // JMeter 테스트 완료 후 이 엔드포인트로 최종 카운터 값을 확인한다.
    // expected=300(30명 × 10회) 와 actual 값을 비교하면 Race Condition 발생 여부를 즉시 알 수 있다.
    @GetMapping
    public ResponseEntity<Map<String, Object>> result(
            @RequestParam(defaultValue = "300") int expected) {

        int unsafeCount = unsafeFieldService.getCounter();
        int atomicCount = safeAtomicService.getCounter();
        int synchronizedCount = safeSynchronizedService.getCounter();
        int reentrantCount = safeReentrantLockService.getCounter();

        return ResponseEntity.ok(Map.of(
                "expected", expected,
                "unsafe_field", Map.of("actual", unsafeCount, "correct", unsafeCount == expected),
                "safe_atomic", Map.of("actual", atomicCount, "correct", atomicCount == expected),
                "safe_synchronized", Map.of("actual", synchronizedCount, "correct", synchronizedCount == expected),
                "safe_reentrant_lock", Map.of("actual", reentrantCount, "correct", reentrantCount == expected),
                "unsafe_stock", unsafeCheckThenActService.getStock()
        ));
    }
}
```

- [ ] **Step 4: git add**

```bash
git add playground-jmeter/src/main/java/com/example/playground/jmeter/thread/safety/
```

---

## Phase 5 — Virtual Thread 패키지

### Task 10: VirtualThreadConfig + 서비스 2종

**Files:**
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/config/VirtualThreadConfig.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/virtual/service/PlatformThreadService.java`
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/virtual/service/VirtualThreadService.java`

- [ ] **Step 1: VirtualThreadConfig 작성**

```java
package com.example.playground.jmeter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class VirtualThreadConfig {

    // 가상 스레드 전용 ExecutorService.
    //
    // Executors.newVirtualThreadPerTaskExecutor():
    //   - 작업마다 새로운 가상 스레드를 생성한다.
    //   - 가상 스레드는 JVM이 관리하는 경량 스레드로, OS 스레드와 1:1 매핑되지 않는다.
    //   - I/O 블로킹 시 캐리어 스레드(OS 스레드)를 반납하고 다른 가상 스레드가 실행된다.
    //   - 수만 개를 동시에 생성해도 OS 스레드를 그만큼 소비하지 않는다.
    @Bean(name = "virtualThreadExecutor")
    public ExecutorService virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    // 비교 기준이 되는 플랫폼 스레드 풀.
    //
    // Executors.newFixedThreadPool(200):
    //   - OS 스레드 200개를 미리 생성한다.
    //   - 스레드가 I/O로 블로킹되면 해당 OS 스레드는 아무것도 못 하고 대기한다.
    //   - 200개 초과 요청은 큐에서 대기한다.
    @Bean(name = "platformThreadExecutor")
    public ExecutorService platformThreadExecutor() {
        return Executors.newFixedThreadPool(200);
    }
}
```

- [ ] **Step 2: PlatformThreadService 작성**

```java
package com.example.playground.jmeter.thread.virtual.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformThreadService {

    // @Qualifier로 플랫폼 스레드 풀을 명시적으로 주입한다.
    @Qualifier("platformThreadExecutor")
    private final ExecutorService platformThreadExecutor;

    // I/O 대기 시뮬레이션: Thread.sleep(100ms)으로 DB 쿼리나 외부 API 호출 대기를 흉내낸다.
    //
    // 플랫폼 스레드 200개 풀에서 실행한다.
    // 100명이 동시에 요청하면 100개 스레드가 100ms 동안 모두 블로킹 상태가 된다.
    // 201번째 요청은 스레드가 남을 때까지 큐에서 대기한다.
    public CompletableFuture<String> ioTask() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String threadName = Thread.currentThread().toString();
                Thread.sleep(100);  // I/O 대기 시뮬레이션
                return "platform thread: " + threadName;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }, platformThreadExecutor);
    }

    // CPU 집약 연산: 소수 판별 반복 계산.
    // 플랫폼 스레드와 가상 스레드 모두 OS 코어를 점유해야 하므로 차이가 없다.
    // JMeter에서 두 엔드포인트를 비교해 "가상 스레드 = 항상 빠른 것이 아님"을 확인한다.
    public CompletableFuture<String> cpuTask() {
        return CompletableFuture.supplyAsync(() -> {
            long count = 0;

            for (int i = 2; i < 50000; i++) {
                if (isPrime(i)) {
                    count++;
                }
            }

            return "platform thread. 소수 개수=" + count;
        }, platformThreadExecutor);
    }

    private boolean isPrime(int n) {
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }
}
```

- [ ] **Step 3: VirtualThreadService 작성**

```java
package com.example.playground.jmeter.thread.virtual.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class VirtualThreadService {

    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

    // 가상 스레드 I/O 대기 시뮬레이션.
    //
    // Thread.sleep(100ms) 블로킹 시 JVM이 자동으로 캐리어 스레드(OS 스레드)를 회수해
    // 다른 가상 스레드가 해당 캐리어 스레드에서 실행된다.
    //
    // 100명이 동시 요청해도 OS 스레드가 100개 필요하지 않다.
    // 적은 수의 캐리어 스레드로 수천 개의 가상 스레드를 처리할 수 있어 TPS가 높다.
    public CompletableFuture<String> ioTask() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String threadName = Thread.currentThread().toString();
                // isVirtual(): Java 21에서 추가된 메서드. 가상 스레드인지 확인한다.
                boolean isVirtual = Thread.currentThread().isVirtual();

                Thread.sleep(100);  // 가상 스레드는 이 시점에 캐리어 스레드를 반납한다.
                return "virtual thread (isVirtual=" + isVirtual + "): " + threadName;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }, virtualThreadExecutor);
    }

    // CPU 집약 연산: 플랫폼 스레드와 동일한 로직.
    //
    // CPU 연산 중에는 가상 스레드도 캐리어 스레드를 점유(핀닝)한다.
    // I/O가 없으므로 캐리어 스레드를 반납할 기회가 없어 성능 이점이 사라진다.
    // 이 테스트는 "가상 스레드는 I/O 바운드 작업에 유리하다"는 원칙을 직접 검증한다.
    public CompletableFuture<String> cpuTask() {
        return CompletableFuture.supplyAsync(() -> {
            long count = 0;

            for (int i = 2; i < 50000; i++) {
                if (isPrime(i)) {
                    count++;
                }
            }

            return "virtual thread. 소수 개수=" + count;
        }, virtualThreadExecutor);
    }

    private boolean isPrime(int n) {
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }
}
```

- [ ] **Step 4: git add**

```bash
git add playground-jmeter/src/main/java/com/example/playground/jmeter/config/
git add playground-jmeter/src/main/java/com/example/playground/jmeter/thread/virtual/service/
```

---

### Task 11: VirtualThreadController

**Files:**
- Create: `playground-jmeter/src/main/java/com/example/playground/jmeter/thread/virtual/controller/VirtualThreadController.java`

- [ ] **Step 1: VirtualThreadController 작성**

```java
package com.example.playground.jmeter.thread.virtual.controller;

import com.example.playground.jmeter.thread.virtual.service.PlatformThreadService;
import com.example.playground.jmeter.thread.virtual.service.VirtualThreadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class VirtualThreadController {

    private final PlatformThreadService platformThreadService;
    private final VirtualThreadService virtualThreadService;

    @GetMapping("/api/thread/platform/io-task")
    public ResponseEntity<String> platformIoTask() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(platformThreadService.ioTask().get());
    }

    @GetMapping("/api/thread/virtual/io-task")
    public ResponseEntity<String> virtualIoTask() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(virtualThreadService.ioTask().get());
    }

    @GetMapping("/api/thread/platform/cpu-task")
    public ResponseEntity<String> platformCpuTask() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(platformThreadService.cpuTask().get());
    }

    @GetMapping("/api/thread/virtual/cpu-task")
    public ResponseEntity<String> virtualCpuTask() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(virtualThreadService.cpuTask().get());
    }
}
```

- [ ] **Step 2: 전체 빌드 및 smoke test**

```bash
./gradlew :playground-jmeter:build

# 가상 스레드 I/O 테스트
curl http://localhost:8090/api/thread/virtual/io-task
```

Expected: `"virtual thread (isVirtual=true): ..."` 응답

- [ ] **Step 3: git add**

```bash
git add playground-jmeter/src/main/java/com/example/playground/jmeter/thread/virtual/
```

---

## Phase 6 — JMeter 테스트 계획

### Task 12: test-plan.jmx 작성

**Files:**
- Create: `playground-jmeter/jmeter/test-plan.jmx`

- [ ] **Step 1: jmeter 디렉터리 생성 및 test-plan.jmx 작성**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2" properties="5.0" jmeter="5.6.3">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan"
              testname="playground-jmeter 전체 성능 테스트" enabled="true">
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
      <elementProp name="TestPlan.user_defined_variables" elementType="Arguments"
                   guiclass="ArgumentsPanel" testclass="Arguments" enabled="true">
        <collectionProp name="Arguments.arguments">
          <elementProp name="HOST" elementType="Argument">
            <stringProp name="Argument.name">HOST</stringProp>
            <stringProp name="Argument.value">localhost</stringProp>
          </elementProp>
          <elementProp name="PORT" elementType="Argument">
            <stringProp name="Argument.name">PORT</stringProp>
            <stringProp name="Argument.value">8090</stringProp>
          </elementProp>
          <elementProp name="PRODUCT_ID" elementType="Argument">
            <stringProp name="Argument.name">PRODUCT_ID</stringProp>
            <stringProp name="Argument.value">1</stringProp>
          </elementProp>
        </collectionProp>
      </elementProp>
    </TestPlan>
    <hashTree>

      <!-- ==================== THREAD GROUP 1: 상품 생성 부하 ==================== -->
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup"
                   testname="[CRUD-1] 상품 생성 부하" enabled="true">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController"
                     guiclass="LoopControlPanel" testclass="LoopController" enabled="true">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">10</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">50</stringProp>
        <stringProp name="ThreadGroup.ramp_time">10</stringProp>
      </ThreadGroup>
      <hashTree>
        <HeaderManager guiclass="HeaderPanel" testclass="HeaderManager"
                       testname="Content-Type JSON" enabled="true">
          <collectionProp name="HeaderManager.headers">
            <elementProp name="" elementType="Header">
              <stringProp name="Header.name">Content-Type</stringProp>
              <stringProp name="Header.value">application/json</stringProp>
            </elementProp>
          </collectionProp>
        </HeaderManager>
        <hashTree/>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy"
                          testname="POST /api/products" enabled="true">
          <stringProp name="HTTPSampler.domain">${HOST}</stringProp>
          <stringProp name="HTTPSampler.port">${PORT}</stringProp>
          <stringProp name="HTTPSampler.protocol">http</stringProp>
          <stringProp name="HTTPSampler.path">/api/products</stringProp>
          <stringProp name="HTTPSampler.method">POST</stringProp>
          <boolProp name="HTTPSampler.postBodyRaw">true</boolProp>
          <elementProp name="HTTPsampler.Arguments" elementType="Arguments">
            <collectionProp name="Arguments.arguments">
              <elementProp name="" elementType="HTTPArgument">
                <stringProp name="Argument.value">{"name":"부하테스트상품","price":10000,"stock":1000}</stringProp>
                <boolProp name="HTTPArgument.always_encode">false</boolProp>
              </elementProp>
            </collectionProp>
          </elementProp>
        </HTTPSamplerProxy>
        <hashTree/>
        <ResultCollector guiclass="SummaryReport" testclass="ResultCollector"
                         testname="Summary Report - CRUD 생성" enabled="true">
          <boolProp name="ResultCollector.error_logging">false</boolProp>
          <objProp>
            <name>saveConfig</name>
            <value class="SampleSaveConfiguration">
              <time>true</time><latency>true</latency><timestamp>true</timestamp>
              <success>true</success><label>true</label><code>true</code>
              <message>true</message><threadName>true</threadName>
              <bytes>true</bytes><sentBytes>true</sentBytes>
              <url>true</url><threadCounts>true</threadCounts>
            </value>
          </objProp>
          <stringProp name="filename"></stringProp>
        </ResultCollector>
        <hashTree/>
      </hashTree>

      <!-- ==================== THREAD GROUP 2: 단건 조회 부하 ==================== -->
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup"
                   testname="[CRUD-2] 단건 조회 부하" enabled="true">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController"
                     guiclass="LoopControlPanel" testclass="LoopController" enabled="true">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">20</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">50</stringProp>
        <stringProp name="ThreadGroup.ramp_time">10</stringProp>
      </ThreadGroup>
      <hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy"
                          testname="GET /api/products/{id}" enabled="true">
          <stringProp name="HTTPSampler.domain">${HOST}</stringProp>
          <stringProp name="HTTPSampler.port">${PORT}</stringProp>
          <stringProp name="HTTPSampler.protocol">http</stringProp>
          <stringProp name="HTTPSampler.path">/api/products/${PRODUCT_ID}</stringProp>
          <stringProp name="HTTPSampler.method">GET</stringProp>
        </HTTPSamplerProxy>
        <hashTree/>
        <ResultCollector guiclass="SummaryReport" testclass="ResultCollector"
                         testname="Summary Report - CRUD 조회" enabled="true">
          <boolProp name="ResultCollector.error_logging">false</boolProp>
          <objProp>
            <name>saveConfig</name>
            <value class="SampleSaveConfiguration">
              <time>true</time><latency>true</latency><timestamp>true</timestamp>
              <success>true</success><label>true</label><code>true</code>
              <bytes>true</bytes><threadCounts>true</threadCounts>
            </value>
          </objProp>
          <stringProp name="filename"></stringProp>
        </ResultCollector>
        <hashTree/>
      </hashTree>

      <!-- ==================== THREAD GROUP 3: 낙관적 락 경합 ==================== -->
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup"
                   testname="[LOCK-1] 낙관적 락 경합" enabled="true">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController"
                     guiclass="LoopControlPanel" testclass="LoopController" enabled="true">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">10</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">20</stringProp>
        <stringProp name="ThreadGroup.ramp_time">5</stringProp>
      </ThreadGroup>
      <hashTree>
        <HeaderManager guiclass="HeaderPanel" testclass="HeaderManager"
                       testname="Content-Type JSON" enabled="true">
          <collectionProp name="HeaderManager.headers">
            <elementProp name="" elementType="Header">
              <stringProp name="Header.name">Content-Type</stringProp>
              <stringProp name="Header.value">application/json</stringProp>
            </elementProp>
          </collectionProp>
        </HeaderManager>
        <hashTree/>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy"
                          testname="PUT /stock/optimistic" enabled="true">
          <stringProp name="HTTPSampler.domain">${HOST}</stringProp>
          <stringProp name="HTTPSampler.port">${PORT}</stringProp>
          <stringProp name="HTTPSampler.protocol">http</stringProp>
          <stringProp name="HTTPSampler.path">/api/products/${PRODUCT_ID}/stock/optimistic</stringProp>
          <stringProp name="HTTPSampler.method">PUT</stringProp>
          <boolProp name="HTTPSampler.postBodyRaw">true</boolProp>
          <elementProp name="HTTPsampler.Arguments" elementType="Arguments">
            <collectionProp name="Arguments.arguments">
              <elementProp name="" elementType="HTTPArgument">
                <stringProp name="Argument.value">{"amount":1}</stringProp>
                <boolProp name="HTTPArgument.always_encode">false</boolProp>
              </elementProp>
            </collectionProp>
          </elementProp>
        </HTTPSamplerProxy>
        <hashTree/>
        <ResultCollector guiclass="SummaryReport" testclass="ResultCollector"
                         testname="Summary Report - 낙관적 락" enabled="true">
          <boolProp name="ResultCollector.error_logging">false</boolProp>
          <objProp>
            <name>saveConfig</name>
            <value class="SampleSaveConfiguration">
              <time>true</time><latency>true</latency><timestamp>true</timestamp>
              <success>true</success><label>true</label><code>true</code>
              <bytes>true</bytes><threadCounts>true</threadCounts>
            </value>
          </objProp>
          <stringProp name="filename"></stringProp>
        </ResultCollector>
        <hashTree/>
      </hashTree>

      <!-- ==================== THREAD GROUP 4: 비관적 락 경합 ==================== -->
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup"
                   testname="[LOCK-2] 비관적 락 경합" enabled="true">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController"
                     guiclass="LoopControlPanel" testclass="LoopController" enabled="true">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">10</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">20</stringProp>
        <stringProp name="ThreadGroup.ramp_time">5</stringProp>
      </ThreadGroup>
      <hashTree>
        <HeaderManager guiclass="HeaderPanel" testclass="HeaderManager"
                       testname="Content-Type JSON" enabled="true">
          <collectionProp name="HeaderManager.headers">
            <elementProp name="" elementType="Header">
              <stringProp name="Header.name">Content-Type</stringProp>
              <stringProp name="Header.value">application/json</stringProp>
            </elementProp>
          </collectionProp>
        </HeaderManager>
        <hashTree/>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy"
                          testname="PUT /stock/pessimistic" enabled="true">
          <stringProp name="HTTPSampler.domain">${HOST}</stringProp>
          <stringProp name="HTTPSampler.port">${PORT}</stringProp>
          <stringProp name="HTTPSampler.protocol">http</stringProp>
          <stringProp name="HTTPSampler.path">/api/products/${PRODUCT_ID}/stock/pessimistic</stringProp>
          <stringProp name="HTTPSampler.method">PUT</stringProp>
          <boolProp name="HTTPSampler.postBodyRaw">true</boolProp>
          <elementProp name="HTTPsampler.Arguments" elementType="Arguments">
            <collectionProp name="Arguments.arguments">
              <elementProp name="" elementType="HTTPArgument">
                <stringProp name="Argument.value">{"amount":1}</stringProp>
                <boolProp name="HTTPArgument.always_encode">false</boolProp>
              </elementProp>
            </collectionProp>
          </elementProp>
        </HTTPSamplerProxy>
        <hashTree/>
        <ResultCollector guiclass="SummaryReport" testclass="ResultCollector"
                         testname="Summary Report - 비관적 락" enabled="true">
          <boolProp name="ResultCollector.error_logging">false</boolProp>
          <objProp>
            <name>saveConfig</name>
            <value class="SampleSaveConfiguration">
              <time>true</time><latency>true</latency><timestamp>true</timestamp>
              <success>true</success><label>true</label><code>true</code>
              <bytes>true</bytes><threadCounts>true</threadCounts>
            </value>
          </objProp>
          <stringProp name="filename"></stringProp>
        </ResultCollector>
        <hashTree/>
      </hashTree>

      <!-- ==================== THREAD GROUP 5: 스레드 안전성 Unsafe vs Safe ==================== -->
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup"
                   testname="[THREAD-1] Unsafe Counter (Race Condition 확인)" enabled="true">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController"
                     guiclass="LoopControlPanel" testclass="LoopController" enabled="true">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">10</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">30</stringProp>
        <stringProp name="ThreadGroup.ramp_time">5</stringProp>
      </ThreadGroup>
      <hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy"
                          testname="POST /api/thread/unsafe/field" enabled="true">
          <stringProp name="HTTPSampler.domain">${HOST}</stringProp>
          <stringProp name="HTTPSampler.port">${PORT}</stringProp>
          <stringProp name="HTTPSampler.protocol">http</stringProp>
          <stringProp name="HTTPSampler.path">/api/thread/unsafe/field</stringProp>
          <stringProp name="HTTPSampler.method">POST</stringProp>
        </HTTPSamplerProxy>
        <hashTree/>
        <ResultCollector guiclass="SummaryReport" testclass="ResultCollector"
                         testname="Summary Report - Unsafe Field" enabled="true">
          <boolProp name="ResultCollector.error_logging">false</boolProp>
          <objProp>
            <name>saveConfig</name>
            <value class="SampleSaveConfiguration">
              <time>true</time><latency>true</latency><timestamp>true</timestamp>
              <success>true</success><label>true</label><code>true</code>
              <bytes>true</bytes><threadCounts>true</threadCounts>
            </value>
          </objProp>
          <stringProp name="filename"></stringProp>
        </ResultCollector>
        <hashTree/>
      </hashTree>

      <!-- ==================== THREAD GROUP 6: 플랫폼 vs 가상 스레드 I/O ==================== -->
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup"
                   testname="[VTHREAD-1] 플랫폼 스레드 I/O (비교 기준)" enabled="true">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController"
                     guiclass="LoopControlPanel" testclass="LoopController" enabled="true">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">5</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">100</stringProp>
        <stringProp name="ThreadGroup.ramp_time">10</stringProp>
      </ThreadGroup>
      <hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy"
                          testname="GET /api/thread/platform/io-task" enabled="true">
          <stringProp name="HTTPSampler.domain">${HOST}</stringProp>
          <stringProp name="HTTPSampler.port">${PORT}</stringProp>
          <stringProp name="HTTPSampler.protocol">http</stringProp>
          <stringProp name="HTTPSampler.path">/api/thread/platform/io-task</stringProp>
          <stringProp name="HTTPSampler.method">GET</stringProp>
          <stringProp name="HTTPSampler.response_timeout">30000</stringProp>
        </HTTPSamplerProxy>
        <hashTree/>
        <ResultCollector guiclass="SummaryReport" testclass="ResultCollector"
                         testname="Summary Report - Platform Thread I/O" enabled="true">
          <boolProp name="ResultCollector.error_logging">false</boolProp>
          <objProp>
            <name>saveConfig</name>
            <value class="SampleSaveConfiguration">
              <time>true</time><latency>true</latency><timestamp>true</timestamp>
              <success>true</success><label>true</label><code>true</code>
              <bytes>true</bytes><threadCounts>true</threadCounts>
            </value>
          </objProp>
          <stringProp name="filename"></stringProp>
        </ResultCollector>
        <hashTree/>
      </hashTree>

      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup"
                   testname="[VTHREAD-2] 가상 스레드 I/O (비교 대상)" enabled="true">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController"
                     guiclass="LoopControlPanel" testclass="LoopController" enabled="true">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">5</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">100</stringProp>
        <stringProp name="ThreadGroup.ramp_time">10</stringProp>
      </ThreadGroup>
      <hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy"
                          testname="GET /api/thread/virtual/io-task" enabled="true">
          <stringProp name="HTTPSampler.domain">${HOST}</stringProp>
          <stringProp name="HTTPSampler.port">${PORT}</stringProp>
          <stringProp name="HTTPSampler.protocol">http</stringProp>
          <stringProp name="HTTPSampler.path">/api/thread/virtual/io-task</stringProp>
          <stringProp name="HTTPSampler.method">GET</stringProp>
          <stringProp name="HTTPSampler.response_timeout">30000</stringProp>
        </HTTPSamplerProxy>
        <hashTree/>
        <ResultCollector guiclass="SummaryReport" testclass="ResultCollector"
                         testname="Summary Report - Virtual Thread I/O" enabled="true">
          <boolProp name="ResultCollector.error_logging">false</boolProp>
          <objProp>
            <name>saveConfig</name>
            <value class="SampleSaveConfiguration">
              <time>true</time><latency>true</latency><timestamp>true</timestamp>
              <success>true</success><label>true</label><code>true</code>
              <bytes>true</bytes><threadCounts>true</threadCounts>
            </value>
          </objProp>
          <stringProp name="filename"></stringProp>
        </ResultCollector>
        <hashTree/>
      </hashTree>

    </hashTree>
  </hashTree>
</jmeterTestPlan>
```

> **참고:** 위 `test-plan.jmx`는 핵심 Thread Group 7개를 포함한 기본 파일이다.
> JMeter GUI(`File > Open`)로 열어 나머지 Thread Group(행 락, 테이블 락, 앱 락, Safe 카운터, CPU 비교)을 추가하고 저장하면 된다.
> 추가 방법: 좌측 트리에서 `Test Plan` 우클릭 → `Add > Threads > Thread Group`

- [ ] **Step 2: git add**

```bash
git add playground-jmeter/jmeter/
```

---

### Task 13: Gradle JMeter 플러그인 연동

**Files:**
- Modify: `playground-jmeter/build.gradle`

- [ ] **Step 1: build.gradle에 JMeter 플러그인 추가**

```groovy
// playground-jmeter/build.gradle 전체 (플러그인 추가)
plugins {
    id 'org.springframework.boot' version '3.4.4'
    id 'io.spring.dependency-management'
    // JMeter Gradle 플러그인: ./gradlew :playground-jmeter:jmeter 로 CLI 실행 가능
    id 'com.github.jmeter-gradle-plugin' version '1.7.0'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
}

// JMeter 플러그인 설정
jmeter {
    // .jmx 파일 위치
    jmxDir = file("${project.projectDir}/jmeter")
    // 결과 리포트 저장 경로
    reportDir = file("${project.buildDir}/jmeter-reports")
    // JMeter 버전 (test-plan.jmx의 jmeter="5.6.3"과 일치)
    jmeterVersion = '5.6.3'
}
```

- [ ] **Step 2: 애플리케이션이 실행 중인 상태에서 Gradle로 JMeter 실행**

```bash
# 1. 별도 터미널에서 애플리케이션 실행
./gradlew :playground-jmeter:bootRun

# 2. 다른 터미널에서 JMeter 실행
./gradlew :playground-jmeter:jmeter
```

Expected: `BUILD SUCCESSFUL`, 결과 파일이 `playground-jmeter/build/jmeter-reports/` 에 생성됨

- [ ] **Step 3: git add**

```bash
git add playground-jmeter/build.gradle
git add playground-jmeter/jmeter/
```

---

## JMeter GUI 사용법 (입문 가이드)

### 설치
1. https://jmeter.apache.org/download_jmeter.cgi 에서 최신 버전 다운로드
2. 압축 해제 후 `bin/jmeter.bat` (Windows) 실행

### test-plan.jmx 열기
1. `File > Open` → `playground-jmeter/jmeter/test-plan.jmx` 선택
2. 좌측 트리에서 Thread Group 선택 → 우측에서 사용자 수/반복 수 조정 가능
3. 초록색 ▶ 버튼으로 실행
4. 좌측 트리에서 `Summary Report` 선택 → TPS/응답시간/오류율 확인

### 테스트 전 필수 준비
```bash
# 1. 애플리케이션 실행 (포트 8090)
./gradlew :playground-jmeter:bootRun

# 2. 락 테스트용 초기 데이터 삽입 (재고 충분히 설정)
curl -X POST http://localhost:8090/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"락테스트상품","price":10000,"stock":10000}'
# → 응답의 id를 JMeter 변수 PRODUCT_ID에 설정

# 3. 스레드 안전성 테스트 전 초기화
curl -X POST http://localhost:8090/api/thread/unsafe/reset
curl -X POST http://localhost:8090/api/thread/safe/reset
```

---

## 스펙 참조

- 설계 문서: `docs/superpowers/specs/2026-06-17-playground-jmeter-design.md`
