package com.example.playground.thread.service.db;

import com.example.playground.thread.entity.Product;
import com.example.playground.thread.mapper.PessimisticMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [비관적 락 (Pessimistic Lock)]
 * 데이터 충돌이 빈번할 것으로 예상하고 DB 레벨에서 락을 잡음 (SELECT ... FOR UPDATE)
 * 작업을 마칠 때까지 다른 세션에서 해당 행(Row)을 수정하거나 읽는 것을 차단합니다.
 *
 * ✔ 장점: 충돌이 잦을 때 재시도 로직이 필요 없으며 정합성이 확실함
 * ✔ 단점: 락 점유 시간이 길어질수록 다른 트랜잭션 대기가 길어져 성능 저하 가능성
 */
@Service
@RequiredArgsConstructor
public class PessimisticService {

    private final PessimisticMapper mapper;

    /**
     * [비관적 락 사용 시 주의사항]
     * ✔ @Transactional 필수: 락은 트랜잭션이 커밋되거나 롤백될 때까지 유지됩니다.
     * ✔ 데드락(Deadlock) 가능성: 여러 행에 대해 순차적으로 락을 획득할 때 순서가 다르면 발생 가능
     * ✔ 타임아웃 설정 권장: 무한정 대기하지 않도록 DB 수준의 락 타임아웃 고려
     */
    @Transactional
    public void decreasePessimistic(Long id) {
        // SELECT ... FOR UPDATE 실행 (락 획득 대기)
        Product product = mapper.selectForUpdate(id);

        if (product.getQuantity() <= 0) {
            throw new RuntimeException("재고 부족 (Pessimistic)");
        }

        // 해당 행에 대해 이미 락을 점유 중이므로 안전하게 수정 가능
        mapper.updateStock(id);
    }

    public int getQuantity(Long id){
        return mapper.selectForUpdate(id).getQuantity();
    }
}
