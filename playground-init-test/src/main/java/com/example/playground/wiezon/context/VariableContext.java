package com.example.playground.wiezon.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 초기화 데이터 객체({@link MidInitData}, {@link CpidMap})를
 * 변수 치환에 사용할 수 있는 Map&lt;String, String&gt; 형태로 변환해주는 컨텍스트 클래스입니다.
 */
public class VariableContext {

    /**
     * MidInitData 객체로부터 변수 맵을 추출합니다.
     * MID, CO_NO, GID, VID 및 하위 CpidMap의 모든 정보를 포함합니다.
     *
     * @param midInitData 소스 데이터 객체
     * @return 치환용 변수 맵
     */
    public static Map<String, String> getContextMap(MidInitData midInitData) {
        Map<String, String> variables = new HashMap<>();

        if (midInitData == null) return variables;

        // Mid 관련
        variables.put("${MID}", midInitData.getMid());
        variables.put("${CO_NO}", midInitData.getCono());
        variables.put("${GID}", midInitData.getGid());
        variables.put("${VID}", midInitData.getL1Vid());
        variables.put("${CRCT_PTN_CD}", midInitData.getCrctPtnCd());
        variables.put("${CRCT_CPID}", midInitData.getCrctCpid());
        variables.put("${CRCT_KEY_TYPE}", midInitData.getCrctKeyType());
        variables.put("${CRCT_KEY}", midInitData.getCrctKey());

        // CpidMap 관련
        variables.putAll(getContextMap(midInitData.getCpidMap()));

        return variables;
    }

    /**
     * CpidMap 객체로부터 CPID 관련 변수 맵을 추출합니다.
     *
     * @param cpid 소스 데이터 객체
     * @return 치환용 변수 맵
     */
    public static Map<String, String> getContextMap(CpidMap cpid) {
        Map<String, String> variables = new HashMap<>();
        if (cpid == null) return variables;

        // Cert
        variables.put("${CERT_PTN_CD}", cpid.getCertPtnCd());
        variables.put("${CERTIFICATION_CPID}", cpid.getCertCpid());
        variables.put("${CERT_KEY_TYPE}", cpid.getCertKeyType());
        variables.put("${CERT_KEY}", cpid.getCertKey());

        // Old
        variables.put("${OLD_PTN_CD}", cpid.getOldCertPtnCd());
        variables.put("${OLD_CERT_CPID}", cpid.getOldCertCpid());
        variables.put("${OLD_KEY_TYPE}", cpid.getOldCertKeyType());
        variables.put("${OLD_KEY}", cpid.getOldCertKey());

        // No Cert
        variables.put("${NO_CERT_PTN_CD}", cpid.getNoCertPtnCd());
        variables.put("${NO_CERT_CPID}", cpid.getNoCertCpid());
        variables.put("${NO_CERT_KEY_TYPE}", cpid.getNoCertKeyType());
        variables.put("${NO_CERT_KEY}", cpid.getNoCertKey());

        // Offline
        variables.put("${OFFLINE_PTN_CD}", cpid.getOfflinePtnCd());
        variables.put("${OFFLINE_CPID}", cpid.getOfflineCpid());
        variables.put("${OFFLINE_KEY_TYPE}", cpid.getOfflineKeyType());
        variables.put("${OFFLINE_KEY}", cpid.getOfflineKey());

        return variables;
    }


    /**
     * PayData 객체로부터 payData 관련 변수 맵을 추출합니다.
     *
     * @param payData 소스 데이터 객체
     * @return 치환용 변수 맵
     */
    public static Map<String, String> getContextMap(PayData payData, String ptnCd){
        Map<String, String> variables = new HashMap<>();
        if (payData == null) return variables;
        variables.put("${payData.pmCd}", payData.getPmCd());
        variables.put("${payData.spmCd}", payData.getSpmCd());

        // 승인 &  부분취소
        variables.put("${payData.tid1}", payData.getTid1());
        variables.put("${payData.tid1P1}", payData.getTid1P1());
        variables.put("${payData.tid1P2}", payData.getTid1P2());
        variables.put("${payData.tid1P3}", payData.getTid1P3());
        variables.put("${APP_NO1}", payData.getAppNo1());

        // 승인 & 전취소
        variables.put("${payData.tid2}", payData.getTid2());
        variables.put("${APP_NO2}", payData.getAppNo2());
        variables.put("${APP_NO3}", payData.getAppNo3());

        variables.put("${VAN_CD}", ptnCd);
        return variables;
    }
}