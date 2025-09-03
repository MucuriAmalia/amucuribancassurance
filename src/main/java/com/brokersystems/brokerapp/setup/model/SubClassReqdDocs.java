package com.brokersystems.brokerapp.setup.model;

import com.brokersystems.brokerapp.uw.model.RiskDocs;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

/**
 * Created by peter on 3/5/2017.
 */
@Entity
@Table(name="sys_brk_subclass_req_docs")
public class SubClassReqdDocs {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="srq_id")
    private Long sclReqrdId;

    @ManyToOne
    @JoinColumn(name="srq_sub_code",nullable=false)
    private SubClassDef subclass;

    @ManyToOne
    @JoinColumn(name="srq_req_code",nullable=false)
    private RequiredDocs requiredDoc;

    @Column(name="srq_mandatory")
    private boolean mandatory;

    @JsonIgnore
    @OneToMany(mappedBy="reqdDocs")
    private List<RiskDocs> riskDocs;

    public Long getSclReqrdId() {
        return sclReqrdId;
    }

    public void setSclReqrdId(Long sclReqrdId) {
        this.sclReqrdId = sclReqrdId;
    }

    public SubClassDef getSubclass() {
        return subclass;
    }

    public void setSubclass(SubClassDef subclass) {
        this.subclass = subclass;
    }

    public RequiredDocs getRequiredDoc() {
        return requiredDoc;
    }

    public void setRequiredDoc(RequiredDocs requiredDoc) {
        this.requiredDoc = requiredDoc;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public List<RiskDocs> getRiskDocs() {
        return riskDocs;
    }

    public void setRiskDocs(List<RiskDocs> riskDocs) {
        this.riskDocs = riskDocs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubClassReqdDocs that = (SubClassReqdDocs) o;

        return sclReqrdId.equals(that.sclReqrdId);

    }

    @Override
    public int hashCode() {
        return sclReqrdId.hashCode();
    }
}
