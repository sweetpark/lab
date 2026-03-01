package com.example.playground.wiezon.context;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CpidContext {
    String certPtnCd;
    String certCpid;
    String certKeyType;
    String certKey;

    String oldCertPtnCd;
    String oldCertCpid;
    String oldCertKeyType;
    String oldCertKey;

    String noCertPtnCd;
    String noCertCpid;
    String noCertKeyType;
    String noCertKey;

    String offlinePtnCd;
    String offlineCpid;
    String offlineKeyType;
    String offlineKey;

    @Override
    public String toString() {
        return "CpidContext{" +
                "\n  cert={" +
                "ptnCd='" + certPtnCd + '\'' +
                ", cpid='" + certCpid + '\'' +
                ", keyType='" + certKeyType + '\'' +
                ", key='" + certKey + '\'' +
                "}" +
                ",\n  oldCert={" +
                "ptnCd='" + oldCertPtnCd + '\'' +
                ", cpid='" + oldCertCpid + '\'' +
                ", keyType='" + oldCertKeyType + '\'' +
                ", key='" + oldCertKey + '\'' +
                "}" +
                ",\n  noCert={" +
                "ptnCd='" + noCertPtnCd + '\'' +
                ", cpid='" + noCertCpid + '\'' +
                ", keyType='" + noCertKeyType + '\'' +
                ", key='" + noCertKey + '\'' +
                "}" +
                ",\n  offline={" +
                "ptnCd='" + offlinePtnCd + '\'' +
                ", cpid='" + offlineCpid + '\'' +
                ", keyType='" + offlineKeyType + '\'' +
                ", key='" + offlineKey + '\'' +
                "}" +
                "\n}";
    }
}