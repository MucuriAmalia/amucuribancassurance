package com.brokersystems.brokerapp.trans.model;

import com.brokersystems.brokerapp.setup.model.BankAccounts;

import javax.persistence.*;
import java.math.BigDecimal;
@Entity
@Table(name="sys_brk_petty_cash_dtls")
public class PettyCashDtls {

    @Id
    @SequenceGenerator(name = "pettyCashDtlsSeq",sequenceName = "petty_cash_dtls_seq",allocationSize=1)
    @GeneratedValue(generator = "pettyCashDtlsSeq")
    @Column(name="ptd_no")
    private Long ptdNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ptd_ct_no", nullable = false)
    private ChequeTrans chequeTrans;

    @Column(name = "ptd_amount", nullable = false)
    private BigDecimal transAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ptd_ba_acc", nullable = false)
    private BankAccounts bankAccounts;

    @Column(name = "ptd_narrative", length = 150)
    private String narrative;

    @Column(name = "ptd_dc", length = 150)
    private String drcr;

    public Long getPtdNo() {
        return ptdNo;
    }

    public void setPtdNo(Long ptdNo) {
        this.ptdNo = ptdNo;
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

    public BankAccounts getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(BankAccounts bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public String getDrcr() {
        return drcr;
    }

    public void setDrcr(String drcr) {
        this.drcr = drcr;
    }
}
