package com.example.playground.wiezon;

import com.example.playground.wiezon.Enum.CPIDType;
import com.example.playground.wiezon.context.DataCollector;
import com.example.playground.wiezon.context.TemplateContext;
import com.example.playground.wiezon.context.afiiliate.AffiliateProfile;
import com.example.playground.wiezon.context.oldContext.CpidContext;
import com.example.playground.wiezon.context.patcher.CpidPatcher;
import com.example.playground.wiezon.context.patcher.FeePatcher;

import com.example.playground.wiezon.service.InitDataAssembler;
import com.example.playground.wiezon.template.TemplateLoader;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.*;

@SpringBootTest
@ActiveProfiles("test")
class AffiliateTest {

    @Autowired
    private InitDataAssembler assembler;

    @Test
    @DisplayName("제휴사 전체 프로필(CPID, FEE 등) 통합 생성 및 메모리 적재 테스트")
    void integrated_affiliate_profile_test() throws IOException {
        TemplateLoader loader = new TemplateLoader("src/test/resources/data/data");
        List<CpidContext> baseCpids = assembler.getBaseCpids();

        // [Step 1] 통합 데이터 컬렉터 준비
        DataCollector collector = new DataCollector();


        // [Step 2] Affiliate Profile 설정
        List<AffiliateProfile> profiles = new ArrayList<>();
        for(CpidContext cpidProperties : baseCpids) {

            AffiliateProfile certProfile = new AffiliateProfile(cpidProperties.getCertPtnCd(), cpidProperties.getCertCpid(), cpidProperties.getCertKeyType(), cpidProperties.getCertKey());
            certProfile.addCpidType(CPIDType.CERT);

            AffiliateProfile oldCertProfile = new AffiliateProfile(cpidProperties.getOldCertPtnCd(), cpidProperties.getOldCertCpid(), cpidProperties.getOldCertKeyType(), cpidProperties.getOldCertKey());
            oldCertProfile.addCpidType(CPIDType.OLD_CERT);

            AffiliateProfile offProfile = new AffiliateProfile(cpidProperties.getOfflinePtnCd(), cpidProperties.getOfflineCpid(), cpidProperties.getOfflineKeyType(), cpidProperties.getOfflineKey());
            offProfile.addCpidType(CPIDType.OFFLINE);

            AffiliateProfile noProfile = new AffiliateProfile(cpidProperties.getNoCertPtnCd(), cpidProperties.getNoCertCpid(), cpidProperties.getNoCertKeyType(), cpidProperties.getNoCertKey());
            noProfile.addCpidType(CPIDType.NO_CERT);

            profiles.add(certProfile);
            profiles.add(oldCertProfile);
            profiles.add(offProfile);
            profiles.add(noProfile);
        }

        for(AffiliateProfile profile : profiles) {
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

        }

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
        // CPID 종류(4종) * cpids 설정 개수 만큼 쌓여있어야 함
        Assertions.assertTrue(storedCpid.getRows().size() >= 4);

        TemplateContext storedFee = collector.getTable("affiliate", "TBSI_PTN_FEE");
        Assertions.assertNotNull(storedFee);
        // 수수료는 CPID당 5개씩 쌓임
        Assertions.assertTrue(storedFee.getRows().size() >= 20);

        // 검증 후 특정 Division만 메모리에서 날리기 (OOM 방지)
        collector.clear("affiliate");
        Assertions.assertTrue(collector.getTablesByDivision("affiliate").isEmpty());
    }


}


