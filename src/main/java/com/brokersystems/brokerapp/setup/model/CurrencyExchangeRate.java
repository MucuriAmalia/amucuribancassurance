package com.brokersystems.brokerapp.setup.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="sys_brk_currency_rates")
public class CurrencyExchangeRate {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ce_code")
    private Long ceCode;
    @Column(name = "ce_rate", nullable = false)
    private BigDecimal rate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ce_base_cur_code",nullable=true)
    private Currencies baseCurrency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ce_cur_code",nullable=false)
    private Currencies currencies;

    @Column(name = "ce_exchange_date", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date exchageDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ce_created_user",nullable=false)
    private User createdBy;

    @Column(name = "ce_created_date", nullable = false)
    private Date createdDate;

    public Currencies getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currencies baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Long getCeCode() {
        return ceCode;
    }

    public void setCeCode(Long ceCode) {
        this.ceCode = ceCode;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Currencies getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Currencies currencies) {
        this.currencies = currencies;
    }

    public Date getExchageDate() {
        return exchageDate;
    }

    public void setExchageDate(Date exchageDate) {
        this.exchageDate = exchageDate;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
