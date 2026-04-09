package com.example.playground.batch.source.tutorial.listener;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class Tutorial09_ChunkListener {

    /**
     * ========================================================================
     * ChunkListener 란?
     * ========================================================================
     * Chunk 단위로 실행되는 콜백
     * 
     * 메서드:
     * - beforeChunk(): Chunk 처리 시작 전
     * - afterChunk(): Chunk 처리 완료 후
     * - afterChunkError(): Chunk 처리 중 오류 발생 시
     * 
     * 사용 사례:
     * - Chunk 단위 로깅/프로그레스 표시
     * - Chunk별 리소스 관리
     * - 특정 Chunk 단위 로직 실행
     * 
     * ChunkContext에서 사용 가능한 정보:
     * - getStepContext(): Step 정보 접근
     * - getReadCount(): 현재까지 읽은 총 건수
     * - getWriteCount(): 현재까지 기록한 총 건수
     * ========================================================================
     */

    private final AtomicInteger chunkNumber = new AtomicInteger(0);

    // ===== Listener 정의 =====

    @Bean
    public ChunkListener tutorial09_chunkListener() {
        return new ChunkListener() {
            @Override
            public void beforeChunk(StepContribution contribution, ChunkContext context) {
                int chunk = chunkNumber.incrementAndGet();
                System.out.println("-".repeat(50));
                System.out.println("[ChunkListener] Chunk #" + chunk + " 시작 - beforeChunk()");
                System.out.println("  Total Read So Far: " + context.getReadCount());
                System.out.println("  Step Name: " + context.getStepContext().getStepName());
            }

            @Override
            public void afterChunk(StepContribution contribution, ChunkContext context) {
                int chunk = chunkNumber.get();
                System.out.println("[ChunkListener] Chunk #" + chunk + " 완료 - afterChunk()");
                System.out.println("  Total Written: " + context.getStepContext()
                    .getStepExecution().getWriteCount());
                System.out.println("-".repeat(50));
            }

            @Override
            public void afterChunkError(StepContribution contribution, ChunkContext context) {
                int chunk = chunkNumber.get();
                System.out.println("[ChunkListener] Chunk #" + chunk + " 오류 - afterChunkError()");
                System.out.println("  Error occurred during chunk processing");
                System.out.println("  Read Count: " + context.getReadCount());
            }
        };
    }

    // ===== Step 정의 =====

    @Bean
    public Step tutorial09_chunkListenerStep(JobRepository jobRepository,
                                             PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial09_chunkListenerStep", jobRepository)
            .listener(tutorial09_chunkListener())
            .<Integer, String>chunk(3)
            .reader(() -> {
                int[] counter = {(int) System.currentTimeMillis() % 100};
                return () -> {
                    int value = counter[0]++;
                    return value > 11 ? null : value;
                };
            })
            .processor(item -> {
                System.out.println("    [Process] Item: " + item);
                return "Processed-" + item;
            })
            .writer(items -> {
                System.out.println("    [Write] Items: " + items);
            })
            .build();
    }

    // ===== Job =====

    @Bean
    public Job tutorial09_chunkListenerJob(JobRepository jobRepository) {
        return new JobBuilder("tutorial09_chunkListenerJob", jobRepository)
            .start(tutorial09_chunkListenerStep(jobRepository, null))
            .build();
    }
}
