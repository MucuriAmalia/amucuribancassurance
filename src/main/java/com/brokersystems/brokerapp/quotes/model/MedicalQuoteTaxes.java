package com.brokersystems.brokerapp.quotes.model;

import com.brokersystems.brokerapp.setup.model.RevenueItemsDef;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

/**
 * Created by waititu on 09/08/2017.
 */
@Entity
@Table(name="sys_brk_mquot_taxes")
public class MedicalQuoteTaxes {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="mqt_tax_id")
    private Long quotTaxId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="mqt_quot_id")
    private QuoteTrans quotation;

    @ManyToOne
    @JoinColumn(name="mqt_tax_rev_code",nullable=false)
    private RevenueItemsDef revenueItems;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "mqt_tax_sub_id",nullable=false)
    private SubClassDef subclass;

    @Column(name="mqt_tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "mqt_tax_rate",nullable=false)
    private BigDecimal taxRate;

    @Column(name = "mqt_tax_div_fact",nullable=false)
    private BigDecimal divFactor;

    @Column(name = "mqt_tax_rate_type",nullable=false)
    private String rateType;

    @Column(name = "mqt_tax_level")
    private String taxLevel;


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

    public Long getQuotTaxId() {
        return quotTaxId;
    }

    public void setQuotTaxId(Long quotTaxId) {
        this.quotTaxId = quotTaxId;
    }

    public QuoteTrans getQuotation() {
        return quotation;
    }

    public void setQuotation(QuoteTrans quotation) {
        this.quotation = quotation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((quotation == null) ? 0 : quotation.hashCode());
        result = prime * result + ((revenueItems == null) ? 0 : revenueItems.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MedicalQuoteTaxes other = (MedicalQuoteTaxes) obj;
        if (quotation == null) {
            if (other.quotation != null)
                return false;
        } else if (!quotation.equals(other.quotation))
            return false;
        if (revenueItems == null) {
            if (other.revenueItems != null)
                return false;
        } else if (!revenueItems.equals(other.revenueItems))
            return false;
        return true;
    }





}
