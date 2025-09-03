package com.brokersystems.brokerapp.quotes.model;

import javax.annotation.Generated;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

/**
 * Created by waititu on 16/09/2017.
 */
@Entity
@Table(name="sys_brk_quot_cat_fam_dtls")
public class MedQuotCatFamilyDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "qcfd_id")
    private Long familyId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "qcfd_cat_id" )
    private MedicalQuoteCategory category;

    @Column(name = "qcfd_fam_size")
    private String familySize;

    @Column(name="qcfd_families")
    private Long families;

    @Column(name="qcfd_req_cards")
    private Long requireCards;

    @Column(name = "qcfd_premium")
    private BigDecimal premium;

    @Column(name = "qcfd_age_bracket")
    private String agebracket;

    public Long getRequireCards() {
        return requireCards;
    }

    public void setRequireCards(Long requireCards) {
        this.requireCards = requireCards;
    }

    public String getAgebracket() {
        return agebracket;
    }

    public void setAgebracket(String agebracket) {
        this.agebracket = agebracket;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(Long familyId) {
        this.familyId = familyId;
    }

    public MedicalQuoteCategory getCategory() {
        return category;
    }

    public void setCategory(MedicalQuoteCategory category) {
        this.category = category;
    }

    public String getFamilySize() {
        return familySize;
    }

    public void setFamilySize(String familySize) {
        this.familySize = familySize;
    }

    public Long getFamilies() {
        return families;
    }

    public void setFamilies(Long families) {
        this.families = families;
    }
}
