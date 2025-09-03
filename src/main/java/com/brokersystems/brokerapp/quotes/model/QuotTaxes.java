package com.brokersystems.brokerapp.quotes.model;

import com.brokersystems.brokerapp.setup.model.RevenueItemsDef;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

/**
 * Created by peter on 3/15/2017.
 */
@Entity
@Table(name="sys_brk_quot_taxes")
public class QuotTaxes {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="qt_tax_id")
    private Long polTaxId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="qt_qpt_id")
    private QuoteProTrans proTrans;

    @ManyToOne
    @JoinColumn(name="qt_tax_rev_code",nullable=false)
    private RevenueItemsDef revenueItems;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "qt_tax_sub_id",nullable=false)
    private SubClassDef subclass;

    @Column(name="qt_tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "qt_tax_rate",nullable=false)
    private BigDecimal taxRate;

    @Column(name = "qt_tax_div_fact",nullable=false)
    private BigDecimal divFactor;

    @Column(name = "qt_tax_rate_type",nullable=false)
    private String rateType;

    @Column(name = "qt_tax_level")
    private String taxLevel;

    public Long getPolTaxId() {
        return polTaxId;
    }

    public void setPolTaxId(Long polTaxId) {
        this.polTaxId = polTaxId;
    }

    public QuoteProTrans getProTrans() {
        return proTrans;
    }

    public void setProTrans(QuoteProTrans proTrans) {
        this.proTrans = proTrans;
    }

    public RevenueItemsDef getRevenueItems() {
        return revenueItems;
    }

    public void setRevenueItems(RevenueItemsDef revenueItems) {
        this.revenueItems = revenueItems;
    }

    public SubClassDef getSubclass() {
        return subclass;
    }

    public void setSubclass(SubClassDef subclass) {
        this.subclass = subclass;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getDivFactor() {
        return divFactor;
    }

    public void setDivFactor(BigDecimal divFactor) {
        this.divFactor = divFactor;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getTaxLevel() {
        return taxLevel;
    }

    public void setTaxLevel(String taxLevel) {
        this.taxLevel = taxLevel;
    }


    @Override
    public String toString() {
        return "QuotTaxes{" +
                "polTaxId=" + polTaxId +
                ", revenueItems=" + revenueItems +
                ", subclass=" + subclass +
                ", taxAmount=" + taxAmount +
                ", taxRate=" + taxRate +
                ", divFactor=" + divFactor +
                ", rateType='" + rateType + '\'' +
                ", taxLevel='" + taxLevel + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuotTaxes quotTaxes = (QuotTaxes) o;

        if (!proTrans.equals(quotTaxes.proTrans)) return false;
        return revenueItems.equals(quotTaxes.revenueItems);

    }

    @Override
    public int hashCode() {
        int result = proTrans.hashCode();
        result = 31 * result + revenueItems.hashCode();
        return result;
    }
}
