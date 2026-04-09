package com.example.playground.batch.source.tutorial.advanced;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class Tutorial12_Partitioning {

    /**
     * ========================================================================
     * Partitioning 란?
     * ========================================================================
     * 대량 데이터를 멀티스레드로 분산 처리
     * 
     * 구성 요소:
     * - Master Step: 작업을 파티션으로 분할
     * - Worker Step: 실제 데이터 처리 (각 파티션마다 실행)
     * - Partitioner: 파티션 분할 전략
     * 
     * Partitioner 종류:
     * - SimplePartitioner: 범위 기반 분할
     * - ColumnRangePartitioner: DB 범위 기반 분할
     * - Custom Partitioner: 사용자 정의 분할 로직
     * 
     * 장점:
     * - 처리 속도 향상
     * - 메모리 분산
     * - CPU 활용도 증가
     * 
     * 주의:
     * - 스레드 안전성 보장 필요
     * - 파티션 수 = 스레드 수 (default: 4)
     * - 각 파티션은 독립적인 StepExecution
     * ========================================================================
     */

    // ===== Partitioner 정의 =====

    @Bean
    public Partitioner tutorial12_rangePartitioner() {
        return (gridSize) -> {
            Map<String, org.springframework.batch.core.partition.support.ExecutionContext> result = 
                new HashMap<>();
            
            int min = 1;
            int max = 100;
            int targetSize = (max - min) / gridSize + 1;
            
            for (int i = 0; i < gridSize; i++) {
                int start = min + (i * targetSize);
                int end = Math.min(start + targetSize - 1, max);
                
                org.springframework.batch.core.partition.support.ExecutionContext context = 
                    new org.springframework.batch.core.partition.support.ExecutionContext();
                context.putInt("minValue", start);
                context.putInt("maxValue", end);
                context.putInt("partitionNumber", i);
                
                result.put("partition" + i, context);
            }
            
            System.out.println("[Partitioner] " + gridSize + "개 파티션으로 분할 완료");
            return result;
        };
    }

    // ===== Worker Step =====

    @Bean
    public Step tutorial12_workerStep(JobRepository jobRepository,
                                      PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial12_workerStep", jobRepository)
            .<Integer, String>chunk(5)
            .reader(tutorial12_partitionedReader(null, null))
            .processor((ItemProcessor<Integer, String>) item -> {
                System.out.println("[Worker-" + Thread.currentThread().getName() + "] Process: " + item);
                return "Processed-" + item;
            })
            .writer(items -> {
                System.out.println("[Worker-" + Thread.currentThread().getName() + "] Write: " + items.size() + "건");
            })
            .build();
    }

    @Bean
    @StepScope
    public ItemReader<Integer> tutorial12_partitionedReader(
            @Value("#{stepExecutionContext['minValue']}") Integer minValue,
            @Value("#{stepExecutionContext['maxValue']}") Integer maxValue) {
        
        if (minValue == null) minValue = 1;
        if (maxValue == null) maxValue = 10;
        
        System.out.println("[Reader] Partitioned - Min: " + minValue + ", Max: " + maxValue);
        
        List<Integer> items = new ArrayList<>();
        for (int i = minValue; i <= maxValue; i++) {
            items.add(i);
        }
        return new ListItemReader<>(items);
    }

    // ===== Master Step =====

    @Bean
    public Step tutorial12_masterStep(JobRepository jobRepository) {
        return new StepBuilder("tutorial12_masterStep", jobRepository)
            .partitioner("tutorial12_workerStep", tutorial12_rangePartitioner())
            .gridSize(4)
            .build();
    }

    // ===== Job =====

    @Bean
    public Job tutorial12_partitioningJob(JobRepository jobRepository) {
        return new JobBuilder("tutorial12_partitioningJob", jobRepository)
            .start(tutorial12_masterStep(jobRepository))
            .build();
    }
}
