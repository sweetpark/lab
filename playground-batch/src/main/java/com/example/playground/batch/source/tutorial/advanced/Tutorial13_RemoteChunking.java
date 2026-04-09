package com.example.playground.batch.source.tutorial.advanced;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class Tutorial13_RemoteChunking {

    /**
     * ========================================================================
     * Remote Chunking 란?
     * ========================================================================
     * 분산 환경에서 메시지 브로커를 통한 Chunk 분산 처리
     * 
     * 구성:
     * - Master: ItemReader + Chunk 분배 (Orchestrator)
     * - Workers: Chunk 수신 -> Processor -> Writer (실제 처리)
     * 
     * 아키텍처:
     * [Master] --JMS/MQ--> [Worker1]
     *                    --> [Worker2]
     *                    --> [WorkerN]
     * 
     * 사용 시나리오:
     * -单机 처리 능력 한계 도달
     * - 데이터베이스 부하 분산
     * - 클라우드 환경에서 수평 확장
     * 
     * 참고: 실제 원격 분산은 JMS/RabbitMQ/Kafka 설정 필요
     * 이 튜토리얼은 구조를 설명하는 시뮬레이션 예제
     * ========================================================================
     */

    private static final ConcurrentHashMap<String, AtomicInteger> workerStats = new ConcurrentHashMap<>();

    static {
        workerStats.put("Worker-1", new AtomicInteger(0));
        workerStats.put("Worker-2", new AtomicInteger(0));
        workerStats.put("Worker-3", new AtomicInteger(0));
    }

    // ===== 데이터 모델 =====

    public record Order(Long id, String product, int quantity) {}

    // ===== Master: Order Reader =====

    @Bean
    @StepScope
    public ItemReader<Order> tutorial13_orderReader(
            @Value("#{jobParameters['chunkSize'] ?: 5}") int chunkSize) {
        
        System.out.println("[Master] Order Reader 초기화 (Chunk Size: " + chunkSize + ")");
        
        List<Order> orders = new ArrayList<>();
        for (long i = 1; i <= 20; i++) {
            orders.add(new Order(i, "Product-" + i, (int) (Math.random() * 10) + 1));
        }
        
        return new ListItemReader<>(orders);
    }

    // ===== Master: Chunk 분배 로직 (시뮬레이션) =====

    @Bean
    public ItemProcessor<Order, String> tutorial13_chunkDistributor() {
        return order -> {
            System.out.println("[Master] Chunk 분배: " + order);
            return "CHUNK:" + order.id() + ":" + order.product() + ":" + order.quantity();
        };
    }

    // ===== Worker: 실제 처리 (시뮬레이션) =====

    public static void processRemoteChunk(String chunkData) {
        String workerId = "Worker-" + ((int) (Math.random() * 3) + 1);
        workerStats.get(workerId).incrementAndGet();
        
        System.out.println("[Worker:" + workerId + "] 처리: " + chunkData);
    }

    // ===== Master Step =====

    @Bean
    public Step tutorial13_masterStep(JobRepository jobRepository,
                                       PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial13_masterStep", jobRepository)
            .<Order, String>chunk(3)
            .reader(tutorial13_orderReader(null))
            .processor(tutorial13_chunkDistributor())
            .writer(items -> {
                System.out.println("[Master] Chunk Writer: " + items.size() + "건");
                items.forEach(Tutorial13_RemoteChunking::processRemoteChunk);
            })
            .build();
    }

    // ===== Job =====

    @Bean
    public Job tutorial13_remoteChunkingJob(JobRepository jobRepository) {
        return new JobBuilder("tutorial13_remoteChunkingJob", jobRepository)
            .start(tutorial13_masterStep(jobRepository, null))
            .build();
    }

    /**
     * ========================================================================
     * Remote Chunking 설정 가이드
     * ========================================================================
     * 
     * 1. 의존성 추가:
     *    implementation 'org.springframework.boot:spring-boot-starter-amqp'
     *    implementation 'org.springframework.integration:spring-integration-batch'
     * 
     * 2. Master 설정:
     *    @Bean
     *    public SpecializationMessageReceiverItemReader<Order> remoteReader() {
     *        return new SpecializationMessageReceiverItemReader<>(
     *            new AmqpItemReceiver(amqpTemplate, exchange, routingKey)
     *        );
     *    }
     * 
     * 3. MQ 설정:
     *    spring.rabbitmq.host=localhost
     *    spring.rabbitmq.port=5672
     * ========================================================================
     */
}
