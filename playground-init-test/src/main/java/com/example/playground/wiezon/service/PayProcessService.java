package com.example.playground.wiezon.service;

import com.example.playground.wiezon.Enum.PaymentDetailType;
import com.example.playground.wiezon.Enum.PaymentMethod;
import com.example.playground.wiezon.ToolRunner;
import com.example.playground.wiezon.context.MidContext;
import com.example.playground.wiezon.context.PayBaseContext;
import com.example.playground.wiezon.context.PayContext;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PayProcessService {

    private final DataSource dataSource;
    private final Set<String> sessionTids = new HashSet<>();

    public PayContext createPayContext(PayBaseContext payBaseContext, int day) throws SQLException {
        PayContext payContext = new PayContext();

        payContext.setMid(payBaseContext.getMid());
        payContext.setGid(payBaseContext.getGid());
        payContext.setVid(payBaseContext.getVid());
        //FIXME) VAN_CD 값 처리 확인 필요
        payContext.setVanCd(payBaseContext.getVanCd());

        payContext.setPmCd(PaymentDetailType.CARD_AUTH.getGroupCode());
        payContext.setSpmCd(PaymentDetailType.CARD_AUTH.getDetailCode());
        payContext.setTid1(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), payBaseContext.getMid(), day));
        payContext.setTid2(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), payBaseContext.getMid(), day));
        payContext.setTid1P1(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), payBaseContext.getMid(), day));
        payContext.setTid1P2(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), payBaseContext.getMid(), day));
        payContext.setTid1P3(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), payBaseContext.getMid(), day));
        payContext.setAppNo1(String.format("%08d", ToolRunner.app_no++));
        payContext.setAppNo2(String.format("%08d",ToolRunner.app_no++));
        payContext.setAppNo3(String.format("%08d",ToolRunner.app_no++));

        return payContext;
    }


    private @NonNull String createTid(String pmCD, String spmCD, String mid, int day) throws SQLException {
        int sequence = 0;

        while (sequence < 1000) {
            StringBuilder sb = new StringBuilder();

            // 전날 날짜 기준 TID 생성
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSSSSS");
            String date = formatter.format(LocalDateTime.now().minusDays(day));

            sb.append(mid);
            sb.append(pmCD);
            sb.append(spmCD);
            sb.append(date, 0, 6);

            // SUBSTR(cur_time, 7, 9) → HHmmssSSS (9자리) 사용
            int timeTail = Integer.parseInt(date.substring(6, 15));

            // LOWER(HEX(CAST(... AS UNSIGNED)))
            String hex = Integer.toHexString(timeTail).toLowerCase();

            sb.append(String.format("%7s", hex).replace(' ', '0'));
            sb.append(String.format("%03d", sequence));


            String tid = sb.toString();

            if (!sessionTids.contains(tid) && !checkExistTID(tid)) {
                sessionTids.add(tid);
                return tid;
            }

            sequence++;
        }



        throw new IllegalStateException("TID 생성 실패 - sequence 초과");
    }

    private boolean checkExistTID(String tid) throws SQLException {
        Connection con = DataSourceUtils.getConnection(dataSource);

        String sql = "SELECT 1 FROM TBTR_MSTR WHERE TID = ? LIMIT 1";
        try(PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setString(1, tid);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
