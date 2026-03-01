package com.example.playground.wiezon.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 초기화 데이터 객체({@link MidContext}, {@link CpidContext})를
 * 변수 치환에 사용할 수 있는 Map&lt;String, String&gt; 형태로 변환해주는 컨텍스트 클래스입니다.
 */
public class VariableContext {

    public static Map<String, Object> getContextMap(GlobalContext globalContext) {
        Map<String, Object> variables = new HashMap<>();

        if (globalContext == null) return variables;

        variables.put("${CO_NO}", globalContext.getCono());
        variables.put("${GID}", globalContext.getGid());
        variables.put("${VID}", globalContext.getL1Vid());
        variables.put("${CRCT_PTN_CD}", globalContext.getCrctPtnCd());
        variables.put("${CRCT_CPID}", globalContext.getCrctCpid());
        variables.put("${CRCT_KEY_TYPE}", globalContext.getCrctKeyType());
        variables.put("${CRCT_KEY}", globalContext.getCrctKey());

        return variables;
    }

    /**
     * MidInitData 객체로부터 변수 맵을 추출합니다.
     * MID, CO_NO, GID, VID 및 하위 CpidMap의 모든 정보를 포함합니다.
     *
     * @param midContext 소스 데이터 객체
     * @return 치환용 변수 맵
     */
    public static Map<String, Object> getContextMap(MidContext midContext) {
        Map<String, Object> variables = new HashMap<>();

        if (midContext == null) return variables;

        // Mid 관련
        variables.put("${MID}", midContext.getMid());
        variables.put("${CO_NO}", midContext.getCono());
        variables.put("${GID}", midContext.getGid());
        variables.put("${VID}", midContext.getL1Vid());
        variables.put("${CRCT_PTN_CD}", midContext.getCrctPtnCd());
        variables.put("${CRCT_CPID}", midContext.getCrctCpid());
        variables.put("${CRCT_KEY_TYPE}", midContext.getCrctKeyType());
        variables.put("${CRCT_KEY}", midContext.getCrctKey());

        // CpidContext 관련
        variables.putAll(getContextMap(midContext.getCpidContext()));

        return variables;
    }

    /**
     * CpidContext 객체로부터 CPID 관련 변수 맵을 추출합니다.
     *
     * @param cpid 소스 데이터 객체
     * @return 치환용 변수 맵
     */
    public static Map<String, Object> getContextMap(CpidContext cpid) {
        Map<String, Object> variables = new HashMap<>();
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
     * PayContext 객체로부터 payContext 관련 변수 맵을 추출합니다.
     *
     * @param payContext 소스 데이터 객체
     * @return 치환용 변수 맵
     */
    public static Map<String, Object> getContextMap(PayContext payContext, String ptnCd){
        Map<String, Object> variables = new HashMap<>();
        if (payContext == null) return variables;
        variables.put("${payData.pmCd}", payContext.getPmCd());
        variables.put("${payData.spmCd}", payContext.getSpmCd());

        // 승인 &  부분취소
        variables.put("${payData.tid1}", payContext.getTid1());
        variables.put("${payData.tid1P1}", payContext.getTid1P1());
        variables.put("${payData.tid1P2}", payContext.getTid1P2());
        variables.put("${payData.tid1P3}", payContext.getTid1P3());
        variables.put("${APP_NO1}", payContext.getAppNo1());

        // 승인 & 전취소
        variables.put("${payData.tid2}", payContext.getTid2());
        variables.put("${APP_NO2}", payContext.getAppNo2());
        variables.put("${APP_NO3}", payContext.getAppNo3());

        variables.put("${VAN_CD}", ptnCd);
        return variables;
    }
}