package com.brokersystems.brokerapp.trans.model;

import com.brokersystems.brokerapp.accounts.model.CoaSubAccounts;
import com.brokersystems.brokerapp.setup.model.OrgBranch;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_chq_trans_dtls")
public class ChequeTransDtls {

    @Id
    @SequenceGenerator(name = "chequeTransDtlsSeq",sequenceName = "cheque_trans_dtls_seq",allocationSize=1)
    @GeneratedValue(generator = "chequeTransDtlsSeq")
    @Column(name="ctd_no")
    private Long ctdNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ctd_ct_no", nullable = false)
    private ChequeTrans chequeTrans;

    @Column(name = "ctd_amount", nullable = false)
    private BigDecimal transAmount;

    @Column(name = "ctd_narrative", length = 150)
    private String narrative;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ctd_gl_acc", nullable = false)
    private CoaSubAccounts glAcc;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ctd_br_code", nullable = false)
    private OrgBranch branch;

    @Column(name = "ctd_dc", length = 150)
    private String drcr;

    @Column(name = "ctd_tax_rate")
    private BigDecimal taxRate;

    @Column(name = "ctd_tax_amount")
    private BigDecimal taxAmount;


    public Long getCtdNo() {
        return ctdNo;
    }

    public void setCtdNo(Long ctdNo) {
        this.ctdNo = ctdNo;
    }

    public ChequeTrans getChequeTrans() {
        return chequeTrans;
    }

    public void setChequeTrans(ChequeTrans chequeTrans) {
        this.chequeTrans = chequeTrans;
    }

    public BigDecimal getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(BigDecimal transAmount) {
        this.transAmount = transAmount;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public CoaSubAccounts getGlAcc() {
        return glAcc;
    }

    public void setGlAcc(CoaSubAccounts glAcc) {
        this.glAcc = glAcc;
    }

    public OrgBranch getBranch() {
        return branch;
    }

    public void setBranch(OrgBranch branch) {
        this.branch = branch;
    }

    public String getDrcr() {
        return drcr;
    }

    public void setDrcr(String drcr) {
        this.drcr = drcr;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
}
