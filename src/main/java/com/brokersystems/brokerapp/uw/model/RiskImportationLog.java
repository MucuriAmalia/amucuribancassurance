package com.brokersystems.brokerapp.uw.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by HP on 10/15/2017.
 */
@Entity
@Table(name="sys_brk_import_logs")
public class RiskImportationLog {

    @Id
    @SequenceGenerator(name = "riskImportSeq",sequenceName = "risk_import_seq",allocationSize=1)
    @GeneratedValue(generator = "riskImportSeq")
    @Column(name="il_id")
    private Long ilId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="il_policy_id")
    private PolicyTrans policyTrans;

    @Column(name="il_message",nullable = false,length = 200)
    private String errorMessage;

    public Long getIlId() {
        return ilId;
    }

    public void setIlId(Long ilId) {
        this.ilId = ilId;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
