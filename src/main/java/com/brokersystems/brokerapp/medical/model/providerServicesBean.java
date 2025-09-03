package com.brokersystems.brokerapp.medical.model;

import java.util.List;

/**
 * Created by waititu on 01/11/2017.
 */
public class providerServicesBean {
    private Long providerCode;
    private List<Long> services;

    public Long getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(Long providerCode) {
        this.providerCode = providerCode;
    }

    public List<Long> getServices() {
        return services;
    }

    public void setServices(List<Long> services) {
        this.services = services;
    }
}
