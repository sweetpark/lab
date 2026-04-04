package com.example.playground.wiezon.context.patcher;

import com.example.playground.wiezon.context.Profile;
import com.example.playground.wiezon.context.TemplateContext;
import com.example.playground.wiezon.context.afiiliate.AffiliateProfile;
import com.example.playground.wiezon.context.afiiliate.CpidDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CpidPatcher implements Patcher {
    @Override
    public void patch(TemplateContext context, Profile profile) {
        if (context.getRows().isEmpty()) return;
        AffiliateProfile affiliateProfile = (AffiliateProfile) profile;

        List<Map<String, Map<String, Object>>> rows = context.getRows();
        if (!rows.isEmpty() && !affiliateProfile.getCpids().isEmpty()) {

            Map<String, Map<String, Object>> baseRow = rows.get(0);
            List<Map<String, Map<String, Object>>> newRows = new ArrayList<>();
            for (CpidDetail detail : affiliateProfile.getCpids()) {
                Map<String, Map<String, Object>> newRow = com.example.playground.wiezon.util.CommonUtil.deepCopyRow(baseRow);
                com.example.playground.wiezon.util.CommonUtil.patchValue(newRow, "PTN_CD", affiliateProfile.getPtnCd());
                com.example.playground.wiezon.util.CommonUtil.patchValue(newRow, "PTN_CPID", affiliateProfile.getPtnCpid());
                com.example.playground.wiezon.util.CommonUtil.patchValue(newRow, "PTN_TYPE", detail.ptnType);
                com.example.playground.wiezon.util.CommonUtil.patchValue(newRow, "PM_CD", detail.pmCd);
                com.example.playground.wiezon.util.CommonUtil.patchValue(newRow, "SPM_CD", detail.spmCd);
                com.example.playground.wiezon.util.CommonUtil.patchValue(newRow, "DATA", affiliateProfile.getSecretKey());

                newRows.add(newRow);
            }

            context.getRows().clear();
            context.getRows().addAll(newRows);
        }
    }
}
