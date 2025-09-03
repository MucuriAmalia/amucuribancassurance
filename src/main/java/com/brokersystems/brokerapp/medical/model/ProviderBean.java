package com.brokersystems.brokerapp.medical.model;

import java.util.List;

/**
 * Created by peter on 5/5/2017.
 */
public class ProviderBean {

    private Long bindCode;

    private Long catCode;

    private List<Long> providers;

    public Long getBindCode() {
        return bindCode;
    }

    public void setBindCode(Long bindCode) {
        this.bindCode = bindCode;
    }

    public List<Long> getProviders() {
        return providers;
    }

    public void setProviders(List<Long> providers) {
        this.providers = providers;
    }

    public Long getCatCode() {
        return catCode;
    }

    public void setCatCode(Long catCode) {
        this.catCode = catCode;
    }
}
