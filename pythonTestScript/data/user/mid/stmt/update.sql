/* I_mp120101_stmt.uptMbsStmt - 가맹점 정산 정보 갱신 */
UPDATE TBSI_MBS
   SET BANK_CD = ''
     , ACNT_NO = ''
     , ACNT_NO_ENC = ''
     , ACNT_NM = ''
     , ACNT_NM_ENC = NULL
     , PAY_ID_CD = '2'
     , CC_CHK_FLG = '1'
     , AUTO_CAL_FLG = '0'
     , CC_PART_CL = '01:03:61'
     , ACNT_SND_NM = 'PAYQ'
     , TAX_EMAIL = 'wypark@wiezon.com'
     , WORKER = 'admin'
 WHERE MID = 'wzonTest0m';

/* I_mp120101_stmt.updateStmtSVC - 서비스(0004) 종료일 갱신 */
UPDATE TBSI_STMT_SVC
   SET WORKER = 'admin'
     , TO_DT = DATE_FORMAT(DATE_ADD('20260126', INTERVAL -1 DAY), '%Y%m%d')
 WHERE ID = 'wzonTest0m'
   AND STMT_SVC_CD = '0004'
   AND TO_DT = '99999999';

/* I_mp120101_stmt.updateStmtSVC - 서비스(0009) 종료일 갱신 */
UPDATE TBSI_STMT_SVC
   SET WORKER = 'admin'
     , TO_DT = DATE_FORMAT(DATE_ADD('20260126', INTERVAL -1 DAY), '%Y%m%d')
 WHERE ID = 'wzonTest0m'
   AND STMT_SVC_CD = '0009'
   AND TO_DT = '99999999';

/* I_mp120101_stmt.updateStmtSVC - 서비스(0001) 종료일 갱신 */
UPDATE TBSI_STMT_SVC
   SET WORKER = 'admin'
     , TO_DT = DATE_FORMAT(DATE_ADD('20260126', INTERVAL -1 DAY), '%Y%m%d')
 WHERE ID = 'wzonTest0m'
   AND STMT_SVC_CD = '0001'
   AND TO_DT = '99999999';