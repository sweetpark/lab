package com.example.playground.wiezon;

import com.example.playground.wiezon.Enum.CPIDType;
import com.example.playground.wiezon.context.DataCollector;
import com.example.playground.wiezon.context.TemplateContext;
import com.example.playground.wiezon.context.afiiliate.AffiliateProfile;
import com.example.playground.wiezon.context.patcher.CpidPatcher;
import com.example.playground.wiezon.context.patcher.FeePatcher;

import com.example.playground.wiezon.template.TemplateLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

public class AffiliateTest {

    @Test
    @DisplayName("제휴사 전체 프로필(CPID, FEE 등) 통합 생성 및 메모리 적재 테스트")
    void integrated_affiliate_profile_test() throws IOException {
        TemplateLoader loader = new TemplateLoader("src/test/resources/data/data");

        // [Step 1] 통합 데이터 컬렉터 준비
        DataCollector collector = new DataCollector();

        // [Step 2] Affiliate Profile 설정
        AffiliateProfile profile = new AffiliateProfile("PTN001", "test_cpid", "secretKey");
        profile.addCpidType(CPIDType.CERT);

        // [Step 3] Skeleton 로드 및 Patching
        // 3-1. TBSI_PTN_CPID 패치 (Division: affiliate)
        TemplateContext cpidTemplate = loader.load("affiliate/TBSI_PTN_CPID.json");
        CpidPatcher cpidPatcher = new CpidPatcher();
        cpidPatcher.patch(cpidTemplate, profile);
        collector.add(cpidTemplate);

        // 3-2. TBSI_PTN_FEE 패치 (Division: affiliate)
        TemplateContext feeTemplate = loader.load("affiliate/TBSI_PTN_FEE.json");
        FeePatcher feePatcher = new FeePatcher();
        feePatcher.patch(feeTemplate, profile);
        collector.add(feeTemplate);

        // [Step 4] 결과 확인 (Division별로 데이터가 잘 나뉘어 있는지 확인)
        System.out.println("=== Memory Data Status by Division ===");
        for (String division : collector.getAllDivisions()) {
            Map<String, TemplateContext> tables = collector.getTablesByDivision(division);
            System.out.println("Division: [" + division + "]");
            tables.forEach((tableName, ctx) -> {
                System.out.println("  └─ Table: " + tableName + " | Rows: " + ctx.getRows().size());
            });
        }

        // 검증: 'affiliate' 디비전에 데이터가 잘 들어갔나?
        TemplateContext storedCpid = collector.getTable("affiliate", "TBSI_PTN_CPID");
        Assertions.assertNotNull(storedCpid);
        Assertions.assertEquals("test_cpid", storedCpid.getRows().get(0).get("PTN_CPID").get("value"));

        TemplateContext storedFee = collector.getTable("affiliate", "TBSI_PTN_FEE");
        Assertions.assertNotNull(storedFee);
        Assertions.assertEquals(5, storedFee.getRows().size());

        // 검증 후 특정 Division만 메모리에서 날리기 (OOM 방지)
        collector.clear("affiliate");
        Assertions.assertTrue(collector.getTablesByDivision("affiliate").isEmpty());
    }


}


