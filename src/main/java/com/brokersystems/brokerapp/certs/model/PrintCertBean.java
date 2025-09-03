package com.brokersystems.brokerapp.certs.model;

import java.util.List;

/**
 * Created by peter on 2/17/2017.
 */
public class PrintCertBean {

    private Long branchCert;
    private List<Long> certs;


    public Long getBranchCert() {
        return branchCert;
    }

    public void setBranchCert(Long branchCert) {
        this.branchCert = branchCert;
    }

    public List<Long> getCerts() {
        return certs;
    }

    public void setCerts(List<Long> certs) {
        this.certs = certs;
    }
}
