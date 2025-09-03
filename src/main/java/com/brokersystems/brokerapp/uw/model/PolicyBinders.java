package com.brokersystems.brokerapp.uw.model;



import com.brokersystems.brokerapp.setup.model.BindersDef;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_policy_binders")
public class PolicyBinders {

    @Id
    @SequenceGenerator(name = "myPolicyBinderSeq",sequenceName = "policy_binder_seq",allocationSize=1)
    @GeneratedValue(generator = "myPolicyBinderSeq")
    @Column(name="pol_bind_id")
    private Long policyBindId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="pol_bind_policy_id", nullable = false)
    private PolicyTrans policyTrans;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="pol_bind_bind_id", nullable = false)
    private BindersDef binder;

    @Column(name="pol_bind_basic_premium")
    private BigDecimal basicPrem;

    @Column(name="pol_bind_prevbasic_prem")
    private BigDecimal prevBasicPrem;

    @Column(name="pol_bind_net_premium_amt")
    private BigDecimal netPrem;

    @Column(name="pol_bind_prevnet_premium_amt")
    private BigDecimal prevNetPrem;


    private BigDecimal commission;

    private BigDecimal whtx;

    private BigDecimal sd;

    private BigDecimal tl;

    private BigDecimal phcf;

    public Long getPolicyBindId() {
        return policyBindId;
    }

    public void setPolicyBindId(Long policyBindId) {
        this.policyBindId = policyBindId;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
    }

    public BindersDef getBinder() {
        return binder;
    }

    public void setBinder(BindersDef binder) {
        this.binder = binder;
    }

    public BigDecimal getBasicPrem() {
        return basicPrem;
    }

    public void setBasicPrem(BigDecimal basicPrem) {
        this.basicPrem = basicPrem;
    }

    public BigDecimal getPrevBasicPrem() {
        return prevBasicPrem;
    }

    public void setPrevBasicPrem(BigDecimal prevBasicPrem) {
        this.prevBasicPrem = prevBasicPrem;
    }

    public BigDecimal getNetPrem() {
        return netPrem;
    }

    public void setNetPrem(BigDecimal netPrem) {
        this.netPrem = netPrem;
    }

    public BigDecimal getPrevNetPrem() {
        return prevNetPrem;
    }

    public void setPrevNetPrem(BigDecimal prevNetPrem) {
        this.prevNetPrem = prevNetPrem;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getWhtx() {
        return whtx;
    }

    public void setWhtx(BigDecimal whtx) {
        this.whtx = whtx;
    }

    public BigDecimal getSd() {
        return sd;
    }

    public void setSd(BigDecimal sd) {
        this.sd = sd;
    }

    public BigDecimal getTl() {
        return tl;
    }

    public void setTl(BigDecimal tl) {
        this.tl = tl;
    }

    public BigDecimal getPhcf() {
        return phcf;
    }

    public void setPhcf(BigDecimal phcf) {
        this.phcf = phcf;
    }
}
