/* I_mp120101.uptMbsEtc - 가맹점 기타 설정 정보 갱신 */
UPDATE TBSI_MBS tsm
   SET tsm.CRCT_ISS_TYPE_CD = '1'
     , tsm.PAY_NOTI_CD      = '01'
     , tsm.RCPT_PRT_TYPE    = '0'
     , tsm.VAT_CALC_TYPE    = '0'
     , tsm.AUTO_CANCEL_FLG  = '1'
     , tsm.ORD_NO_DUP_FLG   = '0'
     , tsm.WORKER           = 'admin'
 WHERE tsm.MID = 'wzonTest0m';