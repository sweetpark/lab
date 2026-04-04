package com.example.playground.wiezon.context.patcher;

import com.example.playground.wiezon.Enum.FEEType;
import com.example.playground.wiezon.context.Profile;
import com.example.playground.wiezon.context.TemplateContext;
import com.example.playground.wiezon.context.afiiliate.AffiliateProfile;
import com.example.playground.wiezon.context.afiiliate.CpidDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeePatcher implements Patcher {
    @Override
    public void patch(TemplateContext context, Profile profile) {
        if (context.getRows().isEmpty()) return;
        AffiliateProfile affiliateProfile = (AffiliateProfile) profile;

        Map<String, Map<String, Object>> baseRow = context.getRows().get(0);
        List<Map<String, Map<String, Object>>> newRows = new ArrayList<>();


        for (CpidDetail cpidDetail : affiliateProfile.getCpids()) {
            for (FEEType feeType : cpidDetail.feeTypes) {
                Map<String, Map<String, Object>> newRow = com.example.playground.wiezon.util.CommonUtil.deepCopyRow(baseRow);

                com.example.playground.wiezon.util.CommonUtil.patchValue(newRow, "PTN_CD", affiliateProfile.getPtnCd());
                com.example.playground.wiezon.util.CommonUtil.patchValue(newRow, "PTN_CPID", affiliateProfile.getPtnCpid());
                com.example.playground.wiezon.util.CommonUtil.patchValue(newRow, "PM_CD", cpidDetail.pmCd);
                com.example.playground.wiezon.util.CommonUtil.patchValue(newRow, "SPM_CD", cpidDetail.spmCd);
                com.example.playground.wiezon.util.CommonUtil.patchValue(newRow, "SM_MBS_CD", feeType.SMMbsCd);
                com.example.playground.wiezon.util.CommonUtil.patchValue(newRow, "FEE_RATE", feeType.feeRate);
                com.example.playground.wiezon.util.CommonUtil.patchValue(newRow, "FEE_TYPE_CD", feeType.feeType);

                newRows.add(newRow);
            }
        }

        context.getRows().clear();
        context.getRows().addAll(newRows);
    }
}
