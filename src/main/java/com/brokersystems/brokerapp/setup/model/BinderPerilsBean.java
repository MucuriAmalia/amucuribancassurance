package com.brokersystems.brokerapp.setup.model;

import java.util.List;

/**
 * Created by peter on 3/4/2017.
 */
public class BinderPerilsBean {

    private List<Long> perils;
    private Long binderDetail;
    private Long sectId;

    public List<Long> getPerils() {
        return perils;
    }

    public void setPerils(List<Long> perils) {
        this.perils = perils;
    }

    public Long getBinderDetail() {
        return binderDetail;
    }

    public void setBinderDetail(Long binderDetail) {
        this.binderDetail = binderDetail;
    }

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
    }
}
