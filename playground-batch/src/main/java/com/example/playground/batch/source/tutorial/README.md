# Spring Batch Playground 튜토리얼

## 개요
Spring Batch 핵심 개념을 학습하기 위한 14개 튜토리얼

## 튜토리얼 목록

| 번호 | 튜토리얼 | 설명 |
|------|----------|------|
| 01 | JobParameter | Job 실행 시 파라미터 전달 |
| 02 | Job | Job 설정, 조건 분기 |
| 03 | Step | Step 기본 설정 |
| 04 | Tasklet | Tasklet 기반 처리 |
| 05 | Chunk | Chunk 기반 처리 기본 |
| 06 | ChunkProcessor | Reader, Processor, Writer |
| 07 | JobListener | Job 실행 전후 콜백 |
| 08 | StepListener | Step 실행 전후 콜백 |
| 09 | ChunkListener | Chunk 단위 콜백 |
| 10 | Scheduler | 자동 실행 스케줄링 |
| 11 | SkipAndRetry | 오류 처리 (건너뛰기/재시도) |
| 12 | Partitioning | 멀티스레드 분산 처리 |
| 13 | RemoteChunking | 원격 분산 처리 |
| 14 | MetaTable | 메타 테이블 활용 |

## 폴더 구조

```
src/main/java/com/example/playground/batch/source/tutorial/
├── job/
│   ├── Tutorial01_JobParameter.java
│   └── Tutorial02_Job.java
├── step/
│   └── Tutorial03_Step.java
├── tasklet/
│   └── Tutorial04_Tasklet.java
├── chunk/
│   ├── Tutorial05_Chunk.java
│   └── Tutorial06_ChunkProcessor.java
├── listener/
│   ├── Tutorial07_JobListener.java
│   ├── Tutorial08_StepListener.java
│   └── Tutorial09_ChunkListener.java
├── scheduler/
│   └── Tutorial10_Scheduler.java
├── advanced/
│   ├── Tutorial11_SkipAndRetry.java
│   ├── Tutorial12_Partitioning.java
│   └── Tutorial13_RemoteChunking.java
└── meta/
    └── Tutorial14_MetaTable.java
```

## 실행 방법

### 방법 1: 특정 Job만 실행 (Program arguments)

```bash
# application.properties에서 모든 Job 자동 실행 비활성화
spring.batch.job.enabled=false

# Program arguments로 실행
--spring.batch.job.names=tutorial01_jobParameterJob
--userName=홍길동
--requestDate=2024-01-15
```

### 방법 2: JobLauncher 직접 호출

```java
@Autowired
private JobLauncher jobLauncher;

JobParameters params = new JobParametersBuilder()
    .addString("userName", "홍길동")
    .addLong("timestamp", System.currentTimeMillis())
    .toJobParameters();

jobLauncher.run(tutorial01_jobParameterJob, params);
```

## 튜토리얼별 실행 예시

### Tutorial01_JobParameter
```bash
--spring.batch.job.names=tutorial01_jobParameterJob
--userName=홍길동
--requestDate=2024-01-15
```

### Tutorial02_Job
```bash
--spring.batch.job.names=tutorial02_jobJob
```

### Tutorial11_SkipAndRetry
```bash
--spring.batch.job.names=tutorial11_skipJob
```

### Tutorial12_Partitioning
```bash
--spring.batch.job.names=tutorial12_partitioningJob
```

### Tutorial14_MetaTable
```bash
--spring.batch.job.names=tutorial14_metaTableJob
```

## 메타 테이블 확인

```sql
-- Job 실행 이력
SELECT * FROM BATCH_JOB_EXECUTION ORDER BY START_TIME DESC LIMIT 10;

-- 실패한 Job
SELECT * FROM BATCH_JOB_EXECUTION WHERE STATUS = 'FAILED';

-- Step 통계
SELECT STEP_NAME, COUNT(*), SUM(READ_COUNT), SUM(WRITE_COUNT)
FROM BATCH_STEP_EXECUTION GROUP BY STEP_NAME;
```

## 학습 순서

1. **기초** (1-4): JobParameter, Job, Step, Tasklet
2. **중급** (5-10): Chunk, ChunkProcessor, Listeners, Scheduler
3. **고급** (11-14): Skip/Retry, Partitioning, RemoteChunking, MetaTable

## 참고 자료

- [Spring Batch Reference](https://docs.spring.io/spring-batch/reference/)
- [Spring Boot Batch](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#batch)
