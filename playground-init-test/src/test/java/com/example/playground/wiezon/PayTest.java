package com.example.playground.wiezon;

import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.service.InitDataAssembler;
import kms.wiezon.com.crypt.CryptUtils;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
@ActiveProfiles("test")
public class PayTest {
    @Autowired
    public ResourcePatternResolver resolver;
    @Autowired
    Environment environment;
    @Autowired
    InitDataAssembler assembler;

    @Autowired
    public DataSource dataSource;
    public CryptUtils cryptUtils;

    @Value("${mid}")
    private String prefixMidName;


    /*
        CREATE DEFINER=`wiezon`@`%` FUNCTION `Solpay_V3`.`FN_GEN_TID`(v_mid VARCHAR(10), v_pm_cd varchar(2), v_spm_cd varchar(2)) RETURNS varchar(30) CHARSET utf8mb3 COLLATE utf8mb3_general_ci
        READS SQL DATA
    BEGIN

        DECLARE tid VARCHAR(30);
        DECLARE cur_time VARCHAR(20);

        SELECT DATE_FORMAT(CURRENT_TIMESTAMP(6),'%y%m%d%H%i%s%f') INTO cur_time;

        SELECT CONCAT(v_mid, v_pm_cd, v_spm_cd, SUBSTR(cur_time, 1, 6), lpad(LOWER(HEX(CAST(SUBSTR(cur_time, 7, 9) AS UNSIGNED))), 7, '0'), LPAD(FN_NEXTVAL('SEQ_TID'), 3, 0)) INTO tid;

        RETURN tid;

    END
     */
    @Test
    @DisplayName("tid 만들기")
    void genTid(){
        InitData initData = assembler.assemble();

        String mid = initData.getMidList().getFirst().getMid();
        String pmCd = "01";
        String spmCd = "01";
        int sequence = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSSSSS");
        String now = formatter.format(LocalDateTime.now());

        StringBuilder sb = new StringBuilder();
        sb.append(mid);
        sb.append(pmCd);
        sb.append(spmCd);
        sb.append(now, 0, 6);

        // SUBSTR(cur_time, 7, 9) → 7~8 두 자리라고 가정
        int timeTail = Integer.parseInt(now.substring(6, 8));

        // LOWER(HEX(CAST(... AS UNSIGNED)))
        String hex = Integer.toHexString(timeTail).toLowerCase();

        sb.append(String.format("%7s", hex).replace(' ', '0'));
        sb.append(String.format("%03d", sequence));


        System.out.println(sb);

    }
}
