package com.brokersystems.brokerapp.certs.model;

import java.util.List;

/**
 * Created by waititu on 28/10/2018.
 */
public class CertTypeSubclassBean {

    private Long certTypeId;

    private List<Long> subclasses;

    public Long getCertTypeId() {
        return certTypeId;
    }

    public void setCertTypeId(Long certTypeId) {
        this.certTypeId = certTypeId;
    }

    public List<Long> getSubclasses() {
        return subclasses;
    }

    public void setSubclasses(List<Long> subclasses) {
        this.subclasses = subclasses;
    }
}
