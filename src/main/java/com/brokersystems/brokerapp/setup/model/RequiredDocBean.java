package com.brokersystems.brokerapp.setup.model;

import java.util.List;

/**
 * Created by peter on 3/5/2017.
 */
public class RequiredDocBean {

    private Long subCode;
    private List<Long> requiredDocs;

    public Long getSubCode() {
        return subCode;
    }

    public void setSubCode(Long subCode) {
        this.subCode = subCode;
    }

    public List<Long> getRequiredDocs() {
        return requiredDocs;
    }

    public void setRequiredDocs(List<Long> requiredDocs) {
        this.requiredDocs = requiredDocs;
    }
}
