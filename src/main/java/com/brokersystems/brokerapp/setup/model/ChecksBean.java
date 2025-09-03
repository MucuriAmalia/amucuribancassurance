package com.brokersystems.brokerapp.setup.model;

import java.util.List;

/**
 * Created by HP on 11/6/2017.
 */
public class ChecksBean {

    private Long proCode;
    private List<Long> checks;

    public Long getProCode() {
        return proCode;
    }

    public void setProCode(Long proCode) {
        this.proCode = proCode;
    }

    public List<Long> getChecks() {
        return checks;
    }

    public void setChecks(List<Long> checks) {
        this.checks = checks;
    }
}
