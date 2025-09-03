package com.brokersystems.brokerapp.medical.model;

import java.util.List;

/**
 * Created by waititu on 10/11/2017.
 */
public class providerServicesActivitiesBean {
    private Long serviceCode;
    private List<Long> serviceactivities;

    public Long getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(Long serviceCode) {
        this.serviceCode = serviceCode;
    }

    public List<Long> getServiceactivities() {
        return serviceactivities;
    }

    public void setServiceactivities(List<Long> serviceactivities) {
        this.serviceactivities = serviceactivities;
    }
}
