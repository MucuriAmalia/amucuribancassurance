package com.brokersystems.brokerapp.uw.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by peter on 6/6/2017.
 */
@Entity
@Table(name="sys_brk_admin_policies")
public class AdminFeePolicies {

    @Id
    @SequenceGenerator(name = "myAdminFeePolSeq",sequenceName = "admin_pol_fee_seq",allocationSize=1)
    @GeneratedValue(generator = "myAdminFeePolSeq")
    @Column(name="afp_id")
    private Long afpId;

    @ManyToOne
    @JoinColumn(name="afp_pol_id")
    private PolicyTrans policy;

    @ManyToOne
    @JoinColumn(name="afp_af_id")
    private AdminFee adminFee;

    @Column(name="afp_vat_amt")
    private BigDecimal vatAmt;

    @Column(name="afp_vat_excise_duty")
    private BigDecimal vatExciseDuty;

    @Column(name="afp_excise_duty")
    private BigDecimal exciseDuty;

    @Column(name="afp_admin_fee_amt")
    private BigDecimal adminFeeAmt;

    @Column(name="afp_admin_fee_net")
    private BigDecimal adminNetAmt;

    @Column(name = "afp_date",nullable = false)
    private Date processedDate;

    public Long getAfpId() {
        return afpId;
    }

    public void setAfpId(Long afpId) {
        this.afpId = afpId;
    }

    public PolicyTrans getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyTrans policy) {
        this.policy = policy;
    }

    public AdminFee getAdminFee() {
        return adminFee;
    }

    public void setAdminFee(AdminFee adminFee) {
        this.adminFee = adminFee;
    }

    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = processedDate;
    }

    public BigDecimal getVatAmt() {
        return vatAmt;
    }

    public void setVatAmt(BigDecimal vatAmt) {
        this.vatAmt = vatAmt;
    }

    public BigDecimal getVatExciseDuty() {
        return vatExciseDuty;
    }

    public void setVatExciseDuty(BigDecimal vatExciseDuty) {
        this.vatExciseDuty = vatExciseDuty;
    }

    public BigDecimal getExciseDuty() {
        return exciseDuty;
    }

    public void setExciseDuty(BigDecimal exciseDuty) {
        this.exciseDuty = exciseDuty;
    }

    public BigDecimal getAdminFeeAmt() {
        return adminFeeAmt;
    }

    public void setAdminFeeAmt(BigDecimal adminFeeAmt) {
        this.adminFeeAmt = adminFeeAmt;
    }

    public BigDecimal getAdminNetAmt() {
        return adminNetAmt;
    }

    public void setAdminNetAmt(BigDecimal adminNetAmt) {
        this.adminNetAmt = adminNetAmt;
    }
}
