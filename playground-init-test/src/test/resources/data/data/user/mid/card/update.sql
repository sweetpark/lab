/* I_mp120100.updateMbsPtnLink - 기존 파트너 연결 종료 (old_cert_cpid 제외) */
UPDATE TBSI_MBS_PTN_LNK tsmpl
   SET TO_DT = DATE_FORMAT(DATE_ADD(NOW(),INTERVAL -1 DAY),'%Y%m%d')
 WHERE tsmpl.MID = 'wzonTest0m'
   AND DATE_FORMAT(NOW(),'%Y%m%d') BETWEEN tsmpl.FR_DT AND tsmpl.TO_DT
   AND tsmpl.PTN_CPID NOT IN (
       SELECT PTN_CPID
         FROM TBSI_PTN_FEE
        WHERE PM_CD ='01'
          AND SPM_CD = '02'
          AND PTN_CPID = 'old_cert_cpid'
          AND PTN_CD = '13'
        GROUP BY PTN_CPID, EZP_AUTH_CD
   )
   AND tsmpl.PM_CD = '01'
   AND tsmpl.SPM_CD = '02';

/* I_mp120100.updateMbsPtnLinkToDay - 기존 파트너 연결 종료일 연장 (old_cert_cpid) */
UPDATE TBSI_MBS_PTN_LNK tsmpl
  JOIN (
       SELECT PTN_CD
            , PTN_CPID
            , EZP_AUTH_CD
         FROM TBSI_PTN_FEE
        WHERE PM_CD ='01'
          AND SPM_CD = '02'
          AND PTN_CPID = 'old_cert_cpid'
          AND PTN_CD = '13'
        GROUP BY PTN_CPID, EZP_AUTH_CD
  ) join_data ON tsmpl.PTN_CD = join_data.PTN_CD
             AND tsmpl.EZP_AUTH_CD = join_data.EZP_AUTH_CD
             AND tsmpl.PTN_CPID = join_data.PTN_CPID
   SET TO_DT = '99999999'
 WHERE tsmpl.MID = 'wzonTest0m'
   AND tsmpl.PM_CD = '01'
   AND tsmpl.SPM_CD = '02'
   AND tsmpl.FR_DT = DATE_FORMAT(NOW(), '%Y%m%d');

/* I_mp120100.updateMbsPtnLink - 기존 파트너 연결 종료 (offline_cpid 제외) */
UPDATE TBSI_MBS_PTN_LNK tsmpl
   SET TO_DT = DATE_FORMAT(DATE_ADD(NOW(),INTERVAL -1 DAY),'%Y%m%d')
 WHERE tsmpl.MID = 'wzonTest0m'
   AND DATE_FORMAT(NOW(),'%Y%m%d') BETWEEN tsmpl.FR_DT AND tsmpl.TO_DT
   AND tsmpl.PTN_CPID NOT IN (
       SELECT PTN_CPID
         FROM TBSI_PTN_FEE
        WHERE PM_CD ='01'
          AND SPM_CD = '03'
          AND PTN_CPID = 'offline_cpid'
          AND PTN_CD = '13'
        GROUP BY PTN_CPID, EZP_AUTH_CD
   )
   AND tsmpl.PM_CD = '01'
   AND tsmpl.SPM_CD = '03';

/* I_mp120100.updateMbsPtnLinkToDay - 기존 파트너 연결 종료일 연장 (offline_cpid) */
UPDATE TBSI_MBS_PTN_LNK tsmpl
  JOIN (
       SELECT PTN_CD
            , PTN_CPID
            , EZP_AUTH_CD
         FROM TBSI_PTN_FEE
        WHERE PM_CD ='01'
          AND SPM_CD = '03'
          AND PTN_CPID = 'offline_cpid'
          AND PTN_CD = '13'
        GROUP BY PTN_CPID, EZP_AUTH_CD
  ) join_data ON tsmpl.PTN_CD = join_data.PTN_CD
             AND tsmpl.EZP_AUTH_CD = join_data.EZP_AUTH_CD
             AND tsmpl.PTN_CPID = join_data.PTN_CPID
   SET TO_DT = '99999999'
 WHERE tsmpl.MID = 'wzonTest0m'
   AND tsmpl.PM_CD = '01'
   AND tsmpl.SPM_CD = '03'
   AND tsmpl.FR_DT = DATE_FORMAT(NOW(), '%Y%m%d');

/* I_mp120100.updateMbsPtnLink - 기존 파트너 연결 종료 (no_cert_cpid 제외) */
UPDATE TBSI_MBS_PTN_LNK tsmpl
   SET TO_DT = DATE_FORMAT(DATE_ADD(NOW(),INTERVAL -1 DAY),'%Y%m%d')
 WHERE tsmpl.MID = 'wzonTest0m'
   AND DATE_FORMAT(NOW(),'%Y%m%d') BETWEEN tsmpl.FR_DT AND tsmpl.TO_DT
   AND tsmpl.PTN_CPID NOT IN (
       SELECT PTN_CPID
         FROM TBSI_PTN_FEE
        WHERE PM_CD ='01'
          AND SPM_CD = '05'
          AND PTN_CPID = 'no_cert_cpid'
          AND PTN_CD = '13'
        GROUP BY PTN_CPID, EZP_AUTH_CD
   )
   AND tsmpl.PM_CD = '01'
   AND tsmpl.SPM_CD = '05';

/* I_mp120100.updateMbsPtnLinkToDay - 기존 파트너 연결 종료일 연장 (no_cert_cpid) */
UPDATE TBSI_MBS_PTN_LNK tsmpl
  JOIN (
       SELECT PTN_CD
            , PTN_CPID
            , EZP_AUTH_CD
         FROM TBSI_PTN_FEE
        WHERE PM_CD ='01'
          AND SPM_CD = '05'
          AND PTN_CPID = 'no_cert_cpid'
          AND PTN_CD = '13'
        GROUP BY PTN_CPID, EZP_AUTH_CD
  ) join_data ON tsmpl.PTN_CD = join_data.PTN_CD
             AND tsmpl.EZP_AUTH_CD = join_data.EZP_AUTH_CD
             AND tsmpl.PTN_CPID = join_data.PTN_CPID
   SET TO_DT = '99999999'
 WHERE tsmpl.MID = 'wzonTest0m'
   AND tsmpl.PM_CD = '01'
   AND tsmpl.SPM_CD = '05'
   AND tsmpl.FR_DT = DATE_FORMAT(NOW(), '%Y%m%d');