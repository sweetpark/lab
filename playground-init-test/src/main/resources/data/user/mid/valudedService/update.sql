/* I_mp120101.updateAddInfo - 가맹점 부가서비스 설정 갱신 */
UPDATE TBSI_MBS tsm
   SET tsm.SMS_PAY_FLG = '1'
     , tsm.MSG_PAY_LMT_CD = '02'
     , tsm.URL_PAY_FLG = '1'
     , tsm.MMS_PAY_FLG = '1' 
 WHERE tsm.MID = 'wzonTest0m';