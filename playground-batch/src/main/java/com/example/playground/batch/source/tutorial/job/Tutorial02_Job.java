package com.example.playground.batch.source.tutorial.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;

@Configuration
public class Tutorial02_Job {

    /**
     * ========================================================================
     * Job 란?
     * ========================================================================
     * 배치 작업의 최상위 단위
     * - 하나 이상의 Step으로 구성
     * - Job Instance + Job Execution으로 실행 이력 관리
     * - 중복 실행 방지 (같은 파라미터로 재실행 불가)
     * 
     * Job 구성 요소:
     * - JobRepository: 메타데이터 저장소
     * - Step: 실제 작업 수행 단위
     * - JobExecutionDecider: 조건 분기
     * 
     * 주요 기능:
     * - .start(step): 첫 Step 설정
     * - .next(step): 순차 실행
     * - .split().add(flow): 병렬 실행
     * - .on(ExitStatus): 조건 분기
     * ========================================================================
     */

    // ===== Step 정의 =====

    @Bean
    public Step tutorial02_initStep(JobRepository jobRepository,
                                     PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial02_initStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("[Step 1] 초기화 작업 수행");
                chunkContext.getStepContext().getStepExecution()
                    .getJobExecution().getExecutionContext()
                    .putString("initStatus", "completed");
                return RepeatStatus.FINISHED;
            }, platformTransactionManager)
            .build();
    }

    @Bean
    public Step tutorial02_processStep(JobRepository jobRepository,
                                        PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial02_processStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                String initStatus = chunkContext.getStepContext().getStepExecution()
                    .getJobExecution().getExecutionContext()
                    .getString("initStatus", "not_started");
                System.out.println("[Step 2] 프로세싱 (initStatus: " + initStatus + ")");
                return RepeatStatus.FINISHED;
            }, platformTransactionManager)
            .build();
    }

    @Bean
    public Step tutorial02_cleanupStep(JobRepository jobRepository,
                                        PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial02_cleanupStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("[Step 3] 정리 작업 수행");
                return RepeatStatus.FINISHED;
            }, platformTransactionManager)
            .build();
    }

    // ===== Decider 정의 (조건 분기) =====

    @Bean
    public JobExecutionDecider tutorial02_timeBasedDecider() {
        return (parameters, session) -> {
            int hour = LocalDateTime.now().getHour();
            System.out.println("[Decider] 현재 시각: " + hour + "시");
            return hour < 12 
                ? new org.springframework.batch.core.StepContribution.ExitStatus("MORNING")
                : new org.springframework.batch.core.StepContribution.ExitStatus("AFTERNOON");
        };
    }

    @Bean
    public Step tutorial02_morningStep(JobRepository jobRepository,
                                        PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial02_morningStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("[Conditional] 오전 작업 수행");
                return RepeatStatus.FINISHED;
            }, platformTransactionManager)
            .build();
    }

    @Bean
    public Step tutorial02_afternoonStep(JobRepository jobRepository,
                                         PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial02_afternoonStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("[Conditional] 오후 작업 수행");
                return RepeatStatus.FINISHED;
            }, platformTransactionManager)
            .build();
    }

    // ===== Job 정의 =====

    @Bean
    public Job tutorial02_jobJob(JobRepository jobRepository) {
        return new JobBuilder("tutorial02_jobJob", jobRepository)
            .start(tutorial02_initStep(jobRepository, null))
            .next(tutorial02_processStep(jobRepository, null))
            .next(tutorial02_timeBasedDecider())
            .on("MORNING").to(tutorial02_morningStep(jobRepository, null))
            .from(tutorial02_timeBasedDecider())
            .on("AFTERNOON").to(tutorial02_afternoonStep(jobRepository, null))
            .next(tutorial02_cleanupStep(jobRepository, null))
            .end()
            .build();
    }
}
