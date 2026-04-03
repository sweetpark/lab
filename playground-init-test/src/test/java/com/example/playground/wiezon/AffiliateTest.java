package com.example.playground.wiezon;

import com.example.playground.wiezon.Enum.CpidType;
import com.example.playground.wiezon.profile.affiliate.AffiliateProfile;
import com.example.playground.wiezon.profile.affiliate.CpidDetail;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class AffiliateTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("제휴사 Profile 만들기 및 Patching 테스트")
    void extract_cpid() throws IOException {
        
        // [Step 1] Profile 생성 (비즈니스 로직 - "값"만 결정)
        // 이제 01, 05 같은 코드를 외울 필요 없이 Enum으로 관리합니다.
        AffiliateProfile profile = new AffiliateProfile();
        profile.addStandardSet("T999", "TEST_ID", "01", "SECRET_KEY_123");

        // [Step 2] Skeleton 로드 (그릇 - "구조"만 로드)
        // 실제 프로젝트 경로에 맞춰 경로를 조정합니다.
        Path path = Path.of("src/test/resources/data/data/affiliate/TBSI_PTN_CPID.json");
        if(!Files.exists(path)) {
            // 테스트 리소스 경로에서도 찾아봅니다.
            Assertions.fail();
        }
        
        String jsonContent = Files.readString(path);
        Map<String, Object> skeleton = objectMapper.readValue(jsonContent, new TypeReference<>() {});

        // [Step 3] Patching (조립)
        // 템플릿(Skeleton)의 각 행에 객체(Profile)의 값을 주입합니다.
        List<Map<String, Map<String, Object>>> skeletonRows = (List<Map<String, Map<String, Object>>>) skeleton.get("rows");
        List<CpidDetail> patchData = profile.getCpids();

        // 템플릿의 행 개수와 패치 데이터의 개수가 맞아야 합니다 (또는 로직에 따라 동적 생성 가능)
        for (int i = 0; i < patchData.size(); i++) {
            CpidDetail patch = patchData.get(i);
            Map<String, Map<String, Object>> row = skeletonRows.get(i);

            // "Map의 편리함" + "객체의 안정성" 결합
            // 객체에 있는 핵심 값만 골라서 템플릿의 value를 바꿔치기합니다.
            patchValue(row, "PTN_CD", patch.getPtnCd());
            patchValue(row, "PTN_CPID", patch.getPtnCpid());
            patchValue(row, "PM_CD", patch.getPmCd());
            patchValue(row, "SPM_CD", patch.getSpmCd());
            patchValue(row, "KEY_TYPE", patch.getKeyType());
            patchValue(row, "DATA", patch.getKey());
            patchValue(row, "MEMO", patch.getMemo());
        }

        // [결과 확인]
        System.out.println("=== Patching Result ===");
        skeletonRows.forEach(row -> {
            System.out.println("[" + row.get("MEMO").get("value") + "] " 
                    + row.get("PTN_CPID").get("value") + " (PM:" + row.get("PM_CD").get("value") 
                    + ", SPM:" + row.get("SPM_CD").get("value") + ")");
        });

        Assertions.assertEquals(4, skeletonRows.size());
        Assertions.assertEquals("TEST_ID", skeletonRows.get(0).get("PTN_CPID").get("value"));
    }

    /**
     * 특정 컬럼의 값을 패치하는 유틸리티 메서드
     */
    private void patchValue(Map<String, Map<String, Object>> row, String colName, Object newValue) {
        if (row.containsKey(colName)) {
            row.get(colName).put("value", newValue);
        }
    }
}
