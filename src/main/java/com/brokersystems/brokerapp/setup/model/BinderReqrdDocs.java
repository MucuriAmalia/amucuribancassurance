package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

/**
 * Created by HP on 9/15/2017.
 */
@Entity
@Table(name="sys_brk_bind_req_docs")
public class BinderReqrdDocs {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="brd_id")
    private Long brdId;

    @ManyToOne
    @JoinColumn(name="brd_srqd_code",nullable=false)
    private SubClassReqdDocs requiredDocs;

    @Column(name = "brd_mandatory")
    private boolean mandatory;

    @ManyToOne
    @JoinColumn(name="brd_det_code",nullable=false)
    private BinderDetails binderDetail;

    public Long getBrdId() {
        return brdId;
    }

    public void setBrdId(Long brdId) {
        this.brdId = brdId;
    }

    public SubClassReqdDocs getRequiredDocs() {
        return requiredDocs;
    }

    public void setRequiredDocs(SubClassReqdDocs requiredDocs) {
        this.requiredDocs = requiredDocs;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public BinderDetails getBinderDetail() {
        return binderDetail;
    }

    public void setBinderDetail(BinderDetails binderDetail) {
        this.binderDetail = binderDetail;
    }

    public void setMandataory(boolean b) {
    }
}
