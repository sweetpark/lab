# ────────────────────────────────────────────────────────────────
# Stage 1: Build
#   Gradle Wrapper와 소스코드를 사용해 실행 가능한 JAR를 생성한다.
#   빌드 결과물만 Stage 2로 복사하므로 최종 이미지에 JDK/Gradle이 포함되지 않는다.
# ────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace

# 의존성 캐시 레이어 분리:
# gradle wrapper와 빌드 스크립트를 먼저 복사해 소스 변경 시 의존성 재다운로드를 막는다.
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# 서브모듈 빌드 파일만 먼저 복사
COPY playground-jmeter/build.gradle playground-jmeter/build.gradle

# 의존성 미리 다운로드 (소스 변경 시 이 레이어는 캐시 히트)
RUN chmod +x gradlew && \
    ./gradlew :playground-jmeter:dependencies --no-daemon -q 2>/dev/null || true

# 전체 소스 복사 후 빌드
COPY . .
RUN ./gradlew :playground-jmeter:bootJar --no-daemon -x test

# ────────────────────────────────────────────────────────────────
# Stage 2: Runtime
#   JRE만 포함한 경량 이미지. 빌드 도구는 포함되지 않는다.
# ────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 보안: root로 실행하지 않도록 전용 사용자 생성
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Stage 1에서 생성된 JAR만 복사
COPY --from=builder /workspace/playground-jmeter/build/libs/*.jar app.jar

# 컨테이너 포트 선언 (실제 포트 바인딩은 docker-compose.yml에서)
EXPOSE 8090

# JVM 옵션:
#   -XX:+UseContainerSupport : 컨테이너 CPU/메모리 제한을 JVM이 인식하도록 설정
#   -XX:MaxRAMPercentage=75  : 컨테이너 메모리의 75%를 Heap으로 사용 (나머지는 OS/Metaspace)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
