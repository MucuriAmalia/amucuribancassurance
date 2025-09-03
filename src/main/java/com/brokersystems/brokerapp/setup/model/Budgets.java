package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_budgets")
public class Budgets {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="budget_id")
    private Long id;

    @Column(name="year",nullable = false)
    private Integer year;

    public void setYear(Integer year) {
        this.year = year;
    }

    @ManyToOne
    @JoinColumn(name="branch",nullable = true)
    private OrgBranch orgBranch;

    @Column(name="budget_name")
    private String name;

    @Column(name="january_prod")
    private BigDecimal janProd;

    @Column(name="january_pol")
    private BigDecimal janPol;

    @Column(name="february_prod")
    private BigDecimal febProd;

    @Column(name="february_pol")
    private BigDecimal febPol;

    @Column(name="march_prod")
    private BigDecimal marProd;

    @Column(name="march_pol")
    private BigDecimal marPol;

    @Column(name="april_prod")
    private BigDecimal aprProd;

    @Column(name="april_pol")
    private BigDecimal aprPol;

    @Column(name="may_prod")
    private BigDecimal mayProd;

    @Column(name="may_pol")
    private BigDecimal mayPol;

    @Column(name="june_prod")
    private BigDecimal junProd;

    @Column(name="june_pol")
    private BigDecimal junPol;

    @Column(name="july_prod")
    private BigDecimal julProd;

    @Column(name="july_pol")
    private BigDecimal julPol;

    @Column(name="august_prod")
    private BigDecimal augProd;

    @Column(name="august_pol")
    private BigDecimal augPol;

    @Column(name="september_prod")
    private BigDecimal sepProd;

    @Column(name="september_pol")
    private BigDecimal sepPol;

    @Column(name="october_prod")
    private BigDecimal octProd;

    @Column(name="october_pol")
    private BigDecimal octPol;

    @Column(name="november_prod")
    private BigDecimal novProd;

    @Column(name="november_pol")
    private BigDecimal novPol;

    @Column(name="december_prod")
    private BigDecimal decProd;

    @Column(name="december_pol")
    private BigDecimal decPol;

    @ManyToOne
    @JoinColumn(name="agent_id",nullable = true)
    private AccountDef accountDef;

    @ManyToOne
    @JoinColumn(name="budget_product")
    private ProductReportGroup productReportGroup;



    public OrgBranch getOrgBranch() {
        return orgBranch;
    }

    public void setOrgBranch(OrgBranch orgBranch) {
        this.orgBranch = orgBranch;
    }

    public ProductReportGroup getProductReportGroup() {
        return productReportGroup;
    }

    public void setProductReportGroup(ProductReportGroup productReportGroup) {
        this.productReportGroup = productReportGroup;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getJanProd() {
        return janProd;
    }

    public void setJanProd(BigDecimal janProd) {
        this.janProd = janProd;
    }

    public BigDecimal getJanPol() {
        return janPol;
    }

    public void setJanPol(BigDecimal janPol) {
        this.janPol = janPol;
    }

    public BigDecimal getFebProd() {
        return febProd;
    }

    public void setFebProd(BigDecimal febProd) {
        this.febProd = febProd;
    }

    public BigDecimal getFebPol() {
        return febPol;
    }

    public void setFebPol(BigDecimal febPol) {
        this.febPol = febPol;
    }

    public BigDecimal getMarProd() {
        return marProd;
    }

    public void setMarProd(BigDecimal marProd) {
        this.marProd = marProd;
    }

    public BigDecimal getMarPol() {
        return marPol;
    }

    public void setMarPol(BigDecimal marPol) {
        this.marPol = marPol;
    }

    public BigDecimal getAprProd() {
        return aprProd;
    }

    public void setAprProd(BigDecimal aprProd) {
        this.aprProd = aprProd;
    }

    public BigDecimal getAprPol() {
        return aprPol;
    }

    public void setAprPol(BigDecimal aprPol) {
        this.aprPol = aprPol;
    }

    public BigDecimal getMayProd() {
        return mayProd;
    }

    public void setMayProd(BigDecimal mayProd) {
        this.mayProd = mayProd;
    }

    public BigDecimal getMayPol() {
        return mayPol;
    }

    public void setMayPol(BigDecimal mayPol) {
        this.mayPol = mayPol;
    }

    public BigDecimal getJunProd() {
        return junProd;
    }

    public void setJunProd(BigDecimal junProd) {
        this.junProd = junProd;
    }

    public BigDecimal getJunPol() {
        return junPol;
    }

    public void setJunPol(BigDecimal junPol) {
        this.junPol = junPol;
    }

    public BigDecimal getJulProd() {
        return julProd;
    }

    public void setJulProd(BigDecimal julProd) {
        this.julProd = julProd;
    }

    public BigDecimal getJulPol() {
        return julPol;
    }

    public void setJulPol(BigDecimal julPol) {
        this.julPol = julPol;
    }

    public BigDecimal getAugProd() {
        return augProd;
    }

    public void setAugProd(BigDecimal augProd) {
        this.augProd = augProd;
    }

    public BigDecimal getAugPol() {
        return augPol;
    }

    public void setAugPol(BigDecimal augPol) {
        this.augPol = augPol;
    }

    public BigDecimal getSepProd() {
        return sepProd;
    }

    public void setSepProd(BigDecimal sepProd) {
        this.sepProd = sepProd;
    }

    public BigDecimal getSepPol() {
        return sepPol;
    }

    public void setSepPol(BigDecimal sepPol) {
        this.sepPol = sepPol;
    }

    public BigDecimal getOctProd() {
        return octProd;
    }

    public void setOctProd(BigDecimal octProd) {
        this.octProd = octProd;
    }

    public BigDecimal getOctPol() {
        return octPol;
    }

    public void setOctPol(BigDecimal octPol) {
        this.octPol = octPol;
    }

    public BigDecimal getNovProd() {
        return novProd;
    }

    public void setNovProd(BigDecimal novProd) {
        this.novProd = novProd;
    }

    public BigDecimal getNovPol() {
        return novPol;
    }

    public void setNovPol(BigDecimal novPol) {
        this.novPol = novPol;
    }

    public BigDecimal getDecProd() {
        return decProd;
    }

    public void setDecProd(BigDecimal decProd) {
        this.decProd = decProd;
    }

    public BigDecimal getDecPol() {
        return decPol;
    }

    public void setDecPol(BigDecimal decPol) {
        this.decPol = decPol;
    }


    public Integer getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public AccountDef getAccountDef() {
        return accountDef;
    }

    public void setAccountDef(AccountDef accountDef) {
        this.accountDef = accountDef;
    }

}
