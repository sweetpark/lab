# Docker 독립 환경에서 JMeter 부하 테스트

## 왜 Docker 환경이 필요한가?

```
[나쁜 구성] 같은 PC에서 모두 실행
  Windows PC
    ├── JMeter        ← CPU 20% 사용
    ├── Spring Boot   ← CPU 40% 사용
    └── MariaDB       ← CPU 15% 사용

문제: JMeter가 CPU를 쓸수록 App이 느려짐
     "App이 느린 건지, 내 PC가 부족한 건지" 구분 불가

[좋은 구성] Docker로 격리
  Windows PC (JMeter 전용)
    └── JMeter        ← 부하 발생에만 집중
          │
          │ HTTP (네트워크 스택 경유)
          ▼
  Docker (App + DB 전용, CPU/메모리 제한)
    ├── Spring Boot   ← 항상 2코어 1GB 환경
    └── MariaDB       ← 항상 1코어 512MB 환경

효과: "2코어 1GB 서버"에서의 성능을 항상 동일한 조건으로 측정
```

---

## 사전 요구사항

- **Docker Desktop for Windows** 설치 및 실행
  - [다운로드](https://www.docker.com/products/docker-desktop/)
  - WSL2 백엔드 권장 (기본값)
- Docker 동작 확인:
  ```powershell
  docker --version
  docker compose version
  ```

---

## 파일 구조

```
playground-jmeter/
├── Dockerfile              ← 멀티 스테이지 빌드 (JDK → JRE)
├── docker-compose.yml      ← App + MariaDB 컨테이너 정의
└── ...
.dockerignore               ← 빌드 컨텍스트 제외 파일 (루트)
```

---

## 실행 방법 (단계별)

### Step 1: JAR 빌드 없이 Docker Compose로 한 번에 실행

```powershell
# lab 루트 디렉토리에서 실행
cd C:\work\privacy\1.Project\Example\lab

# 백그라운드로 컨테이너 기동 (최초 실행 시 이미지 빌드 포함, 수 분 소요)
docker compose -f playground-jmeter/docker-compose.yml up -d --build
```

### Step 2: 기동 상태 확인

```powershell
# 컨테이너 상태 확인
docker compose -f playground-jmeter/docker-compose.yml ps

# 예상 출력:
# NAME             STATUS          PORTS
# jmeter-mariadb   Up (healthy)    0.0.0.0:3307->3306/tcp
# jmeter-app       Up              0.0.0.0:8090->8090/tcp

# App 헬스 체크
curl http://localhost:8090/actuator/health
# 또는 브라우저에서 http://localhost:8090/api/products
```

### Step 3: 테스트 데이터 생성

```powershell
# 락 경합 테스트용 상품 생성 (stock을 충분히 크게)
curl -X POST http://localhost:8090/api/products `
  -H "Content-Type: application/json" `
  -d '{"name":"테스트상품","price":10000,"stock":99999}'
```

### Step 4: JMeter 실행 (Windows 네이티브)

```powershell
# JMeter GUI 실행
C:\...\apache-jmeter-5.6.3\bin\jmeter.bat

# File → Open → playground-jmeter/jmeter/plans/01-crud.jmx
# Ctrl+R 실행
```

JMeter는 `localhost:8090`으로 요청을 보냅니다.
요청은 Windows 네트워크 스택 → WSL2 가상 NIC → Docker 컨테이너 순으로 전달됩니다.

---

## 컨테이너 관리 명령어

```powershell
# 컨테이너 중지 (데이터 유지)
docker compose -f playground-jmeter/docker-compose.yml stop

# 컨테이너 재시작
docker compose -f playground-jmeter/docker-compose.yml start

# 컨테이너 완전 삭제 (볼륨 포함 — DB 데이터도 삭제)
docker compose -f playground-jmeter/docker-compose.yml down -v

# 앱 로그 실시간 확인
docker logs -f jmeter-app

# MariaDB 로그 확인
docker logs -f jmeter-mariadb

# 컨테이너 내부 접속 (디버깅용)
docker exec -it jmeter-mariadb mariadb -u lab -plab lab
```

---

## 리소스 제한 조정

`docker-compose.yml`의 `deploy.resources.limits`를 수정하면
"어떤 서버 스펙에서의 성능"을 측정할지 변경할 수 있습니다.

```yaml
# 예: 저사양 서버 시뮬레이션 (1코어 512MB)
app:
  deploy:
    resources:
      limits:
        cpus: '1.0'
        memory: '512M'

# 예: 일반 운영 서버 (4코어 4GB)
app:
  deploy:
    resources:
      limits:
        cpus: '4.0'
        memory: '4G'
```

변경 후 재기동:
```powershell
docker compose -f playground-jmeter/docker-compose.yml up -d --build
```

---

## 네트워크 경로 확인

JMeter에서 보낸 요청이 실제로 네트워크를 타는지 확인합니다.

```powershell
# JMeter → Docker 컨테이너 네트워크 경로 추적
tracert localhost

# Docker 컨테이너 네트워크 인터페이스 확인
docker inspect jmeter-app --format '{{.NetworkSettings.Networks}}'
```

---

## 모니터링: 컨테이너 리소스 실시간 확인

JMeter 테스트 실행 중 컨테이너 CPU/메모리 사용량을 모니터링합니다.

```powershell
# 실시간 리소스 모니터링 (1초마다 갱신)
docker stats jmeter-app jmeter-mariadb
```

```
CONTAINER       CPU %   MEM USAGE / LIMIT   MEM %   NET I/O
jmeter-app      45.2%   312MiB / 1GiB       30.5%   15.2MB / 8.7MB
jmeter-mariadb  12.8%   256MiB / 512MiB     50.0%   1.2MB / 900KB
```

**해석:**
- `CPU %`가 `cpus limit × 100%`에 근접하면 CPU가 병목
- `MEM %`가 90% 이상이면 메모리 한계에 근접 → OOM 위험

---

## 트러블슈팅

### App이 기동되지 않을 때

```powershell
# 로그 확인
docker logs jmeter-app

# 주로 원인:
# 1. DB가 아직 준비 안 됨 → healthcheck 대기 중이면 정상, 잠시 후 재확인
# 2. JAR 빌드 실패 → ./gradlew :playground-jmeter:bootJar 먼저 실행
```

### MariaDB 연결 실패

```powershell
# MariaDB 컨테이너 직접 접속해 확인
docker exec -it jmeter-mariadb mariadb -u lab -plab -e "SHOW DATABASES;"

# App의 datasource URL이 컨테이너 서비스명(mariadb)을 가리키는지 확인
# docker-compose.yml: SPRING_DATASOURCE_URL: jdbc:mariadb://mariadb:3306/lab
```

### 포트 충돌 (3307, 8090)

```powershell
# 사용 중인 포트 확인
netstat -ano | findstr "3307"
netstat -ano | findstr "8090"

# docker-compose.yml에서 호스트 포트 변경
# "3307:3306" → "3308:3306"
```
