# Spring Batch Playground 튜토리얼 설계

**Date**: 2026-04-09  
**Topic**: playground-batch에 Spring Batch 튜토리얼 추가

## 개요
Spring Batch의 핵심 개념(jobParameter, job, step, tasklet, chunk, listener, scheduler, skip/retry, partitioning, remote chunking, meta table)을 **주제별 폴더 + 독립적 설정**으로 학습하는 튜토리얼 제공

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

## 설계 결정

| 결정사항 | 선택 | 이유 |
|---------|------|------|
| 코드 구조 | 주제별 폴더 분리 | 각 개념의 독립성 및 검색 용이성 |
| 범위 | 기초~고급 | 14개 튜토리얼로 전체 개념 커버 |
| 진행 순서 | 난이도 순서 | 학습 곡선循序渐进 |
| 코드 스타일 | 학습 중심 | 주석+설명 자세히 포함 |
| 설계 접근 | 독립적 튜토리얼 | 각 튜토리얼이 독립적으로 동작 |
| 구현 패턴 | 어노테이션 기반 | Spring Batch 권장 방식, 실무 친화적 |

## 각 튜토리얼 구조

각 파일 내부 구조:
1. **개념 설명** (JavaDoc 주석)
2. **주요 코드** (설명 주석 포함)
3. **실행 방법** (application.properties 또는 CLI)
4. **기대 출력** (주석으로 표기)

## 적용 규칙

- `@Configuration` + `@Bean` 패턴
- `StepBuilder` / `JobBuilder` fluent API 사용
- Bean 이름: `tutorial{N}_{name}` (예: `tutorial01_jobParameterJob`)
- `@ConditionalOnProperty`로 필요시 활성화

## 튜토리얼 순서

1. **Tutorial01_JobParameter** - Job 실행 시 파라미터 전달 방법
2. **Tutorial02_Job** - Job 설정, 중복 실행 방지, 순차/병렬 Step
3. **Tutorial03_Step** - Step 설정, 입출력 관리
4. **Tutorial04_Tasklet** - Tasklet 기반 단일 작업 처리
5. **Tutorial05_Chunk** - Chunk 기반 데이터 처리 기본
6. **Tutorial06_ChunkProcessor** - ItemReader, ItemProcessor, ItemWriter
7. **Tutorial07_JobListener** - Job 실행 전후 로직
8. **Tutorial08_StepListener** - Step 실행 전후 로직
9. **Tutorial09_ChunkListener** - Chunk 단위 로깅/로직
10. **Tutorial10_Scheduler** - Cron/FixedDelay 기반 자동 실행
11. **Tutorial11_SkipAndRetry** - 오류 시 건너뛰기 및 재시도
12. **Tutorial12_Partitioning** - 멀티스레드 Step 처리
13. **Tutorial13_RemoteChunking** - 분산 환경 데이터 처리
14. **Tutorial14_MetaTable** - Spring Batch 메타 테이블 활용
