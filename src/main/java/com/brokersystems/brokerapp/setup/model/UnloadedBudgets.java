package com.brokersystems.brokerapp.setup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name="sys_brk_failed_budgets")
public class UnloadedBudgets {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @JsonIgnore
    @Column(name="budget_id")
    private Long id;

    @Column(name="budget_error")
    private String error;

    @Column(name="budget_name")
    private String name;

    @Column(name="budget_product")
    private String productReportGroup;

    @Column(name="branch")
    private String branch;

    @Column(name="agent_id")
    private String accountDef;

    @Column(name="year")
    private String year;

    @Column(name="january_prod")
    private String janProd;

    @Column(name="january_pol")
    private String janPol;

    @Column(name="february_prod")
    private String febProd;

    @Column(name="february_pol")
    private String febPol;

    @Column(name="march_prod")
    private String marProd;

    @Column(name="march_pol")
    private String marPol;

    @Column(name="april_prod")
    private String aprProd;

    @Column(name="april_pol")
    private String aprPol;

    @Column(name="may_prod")
    private String mayProd;

    @Column(name="may_pol")
    private String mayPol;

    @Column(name="june_prod")
    private String junProd;

    @Column(name="june_pol")
    private String junPol;

    @Column(name="july_prod")
    private String julProd;

    @Column(name="july_pol")
    private String julPol;

    @Column(name="august_prod")
    private String augProd;

    @Column(name="august_pol")
    private String augPol;

    @Column(name="september_prod")
    private String sepProd;

    @Column(name="september_pol")
    private String sepPol;

    @Column(name="october_prod")
    private String octProd;

    @Column(name="october_pol")
    private String octPol;

    @Column(name="november_prod")
    private String novProd;

    @Column(name="november_pol")
    private String novPol;

    @Column(name="december_prod")
    private String decProd;

    @Column(name="december_pol")
    private String decPol;

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

    public String getProductReportGroup() {
        return productReportGroup;
    }

    public void setProductReportGroup(String productReportGroup) {
        this.productReportGroup = productReportGroup;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getAccountDef() {
        return accountDef;
    }

    public void setAccountDef(String accountDef) {
        this.accountDef = accountDef;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public String getJanProd() {
        return janProd;
    }

    public void setJanProd(String janProd) {
        this.janProd = janProd;
    }

    public String getJanPol() {
        return janPol;
    }

    public void setJanPol(String janPol) {
        this.janPol = janPol;
    }

    public String getFebProd() {
        return febProd;
    }

    public void setFebProd(String febProd) {
        this.febProd = febProd;
    }

    public String getFebPol() {
        return febPol;
    }

    public void setFebPol(String febPol) {
        this.febPol = febPol;
    }

    public String getMarProd() {
        return marProd;
    }

    public void setMarProd(String marProd) {
        this.marProd = marProd;
    }

    public String getMarPol() {
        return marPol;
    }

    public void setMarPol(String marPol) {
        this.marPol = marPol;
    }

    public String getAprProd() {
        return aprProd;
    }

    public void setAprProd(String aprProd) {
        this.aprProd = aprProd;
    }

    public String getAprPol() {
        return aprPol;
    }

    public void setAprPol(String aprPol) {
        this.aprPol = aprPol;
    }

    public String getMayProd() {
        return mayProd;
    }

    public void setMayProd(String mayProd) {
        this.mayProd = mayProd;
    }

    public String getMayPol() {
        return mayPol;
    }

    public void setMayPol(String mayPol) {
        this.mayPol = mayPol;
    }

    public String getJunProd() {
        return junProd;
    }

    public void setJunProd(String junProd) {
        this.junProd = junProd;
    }

    public String getJunPol() {
        return junPol;
    }

    public void setJunPol(String junPol) {
        this.junPol = junPol;
    }

    public String getJulProd() {
        return julProd;
    }

    public void setJulProd(String julProd) {
        this.julProd = julProd;
    }

    public String getJulPol() {
        return julPol;
    }

    public void setJulPol(String julPol) {
        this.julPol = julPol;
    }

    public String getAugProd() {
        return augProd;
    }

    public void setAugProd(String augProd) {
        this.augProd = augProd;
    }

    public String getAugPol() {
        return augPol;
    }

    public void setAugPol(String augPol) {
        this.augPol = augPol;
    }

    public String getSepProd() {
        return sepProd;
    }

    public void setSepProd(String sepProd) {
        this.sepProd = sepProd;
    }

    public String getSepPol() {
        return sepPol;
    }

    public void setSepPol(String sepPol) {
        this.sepPol = sepPol;
    }

    public String getOctProd() {
        return octProd;
    }

    public void setOctProd(String octProd) {
        this.octProd = octProd;
    }

    public String getOctPol() {
        return octPol;
    }

    public void setOctPol(String octPol) {
        this.octPol = octPol;
    }

    public String getNovProd() {
        return novProd;
    }

    public void setNovProd(String novProd) {
        this.novProd = novProd;
    }

    public String getNovPol() {
        return novPol;
    }

    public void setNovPol(String novPol) {
        this.novPol = novPol;
    }

    public String getDecProd() {
        return decProd;
    }

    public void setDecProd(String decProd) {
        this.decProd = decProd;
    }

    public String getDecPol() {
        return decPol;
    }

    public void setDecPol(String decPol) {
        this.decPol = decPol;
    }
}
