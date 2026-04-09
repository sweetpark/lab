package com.example.playground.batch.source.tutorial.listener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class Tutorial07_JobListener {

    /**
     * ========================================================================
     * JobExecutionListener 란?
     * ========================================================================
     * Job 실행 전후에 실행되는 콜백
     * 
     * 메서드:
     * - beforeJob(): Job 시작 전
     * - afterJob(): Job 종료 후 (성공/실패 상관없이)
     * 
     * 사용 사례:
     * - 로깅
     * - 리소스 준비/정리
     * - 메트릭 수집
     * - 알림 전송
     * 
     * JobExecution에서 사용 가능한 정보:
     * - getStatus(): 현재 상태
     * - getStartTime(), getEndTime(): 실행 시간
     * - getStepExecutions(): 각 Step 결과
     * - getExecutionContext(): 데이터 공유
     * ========================================================================
     */

    // ===== Listener 정의 =====

    @Bean
    public JobExecutionListener tutorial07_jobListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                System.out.println("=".repeat(60));
                System.out.println("[JobListener] Job 시작 전 - beforeJob()");
                System.out.println("-".repeat(60));
                System.out.println("Job Name: " + jobExecution.getJobInstance().getJobName());
                System.out.println("Job Instance ID: " + jobExecution.getJobId());
                System.out.println("Job Parameters: " + jobExecution.getJobParameters());
                System.out.println("Status: " + jobExecution.getStatus());
                System.out.println("-".repeat(60));
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                System.out.println("-".repeat(60));
                System.out.println("[JobListener] Job 종료 후 - afterJob()");
                System.out.println("-".repeat(60));
                System.out.println("Final Status: " + jobExecution.getStatus());
                System.out.println("Start Time: " + jobExecution.getStartTime());
                System.out.println("End Time: " + jobExecution.getEndTime());
                System.out.println("Duration: " + 
                    (jobExecution.getEndTime() != null && jobExecution.getStartTime() != null
                        ? java.time.Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()) + "ms"
                        : "N/A"));
                
                // 각 Step 결과 출력
                jobExecution.getStepExecutions().forEach(stepExecution -> 
                    System.out.println("  Step [" + stepExecution.getStepName() + "]: " + stepExecution.getStatus())
                );
                System.out.println("=".repeat(60));
            }
        };
    }

    // ===== Step 정의 =====

    @Bean
    public Step tutorial07_jobListenerStep(JobRepository jobRepository,
                                            PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial07_jobListenerStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("[Step] 실제 작업 수행 중...");
                return RepeatStatus.FINISHED;
            }, platformTransactionManager)
            .build();
    }

    // ===== Job 정의 (Listener 등록) =====

    @Bean
    public Job tutorial07_jobListenerJob(JobRepository jobRepository) {
        return new JobBuilder("tutorial07_jobListenerJob", jobRepository)
            .listener(tutorial07_jobListener())
            .start(tutorial07_jobListenerStep(jobRepository, null))
            .build();
    }
}
