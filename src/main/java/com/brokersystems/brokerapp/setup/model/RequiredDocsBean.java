package com.brokersystems.brokerapp.setup.model;

import java.util.List;

/**
 * Created by HP on 9/18/2017.
 */
public class RequiredDocsBean {

    private List<Long> requiredDocs;
    private Long binderDetail;

    public List<Long> getRequiredDocs() {
        return requiredDocs;
    }

    public void setRequiredDocs(List<Long> requiredDocs) {
        this.requiredDocs = requiredDocs;
    }

    public Long getBinderDetail() {
        return binderDetail;
    }

    public void setBinderDetail(Long binderDetail) {
        this.binderDetail = binderDetail;
    }
}
