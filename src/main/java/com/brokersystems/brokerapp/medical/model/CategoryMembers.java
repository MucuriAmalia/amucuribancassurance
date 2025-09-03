
package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by peter on 4/27/2017.
 */
@Entity
@Table(name="sys_brk_cat_members")
public class CategoryMembers {

    @Id
    @SequenceGenerator(name = "medMemberSeq",sequenceName = "med_member_seq",allocationSize=1)
    @GeneratedValue(generator = "medMemberSeq")
    @Column(name="cm_id")
    private Long sectId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cm_cat_id",nullable = false)
    private MedicalCategory category;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cm_client_id",nullable = false)
    private ClientDef client;

    @Column(name="cm_dep_type",nullable = false)
    private String dependentTypes;

    @Column(name="cm_status" )
    private String memberStatus;

    @Column(name="cm_hascard" )
    private String memberHasCard;

    @Column(name="cm_generatecard" )
    private String autoGenerateCard;

    @Column(name="cm_child_type")
    private String childType;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cm_main_clnt_id")
    private ClientDef mainClient;

    @Column(name="cm_premium")
    private BigDecimal premium;

    @Column(name="cm_prev_premium")
    private BigDecimal prevPremium;

    @Column(name="cm_computed_premium")
    private BigDecimal computedPremium;

    @Column(name="cm_age")
    private Long age;

    @Column(name="cm_member_no")
    private String  memberShipNo;

    @Column(name="cm_card_no")
    private String  cardNo;



    @Column(name="cm_wef_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @Column(name="cm_wet_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;

    @Column(name="cm_prev_wef")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date prevwefDate;

    @Column(name="cm_prev_wet")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date prevwetDate;

    @Column(name="cm_cm_id")
    private Long prevsectId;

    @Column(name="cm_stamp_duty")
    private BigDecimal stampDuty;

    @Column(name="cm_ph_fund")
    private BigDecimal phfFund;

    @Column(name="cm_tl")
    private BigDecimal trainingLevy;

    @Column(name="cm_whtx")
    private BigDecimal whtax;

    @Column(name="cm_extras")
    private BigDecimal extras;

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
    }

    public MedicalCategory getCategory() {
        return category;
    }

    public void setCategory(MedicalCategory category) {
        this.category = category;
    }

    public ClientDef getClient() {
        return client;
    }

    public void setClient(ClientDef client) {
        this.client = client;
    }

    public String getDependentTypes() {
        return dependentTypes;
    }

    public void setDependentTypes(String dependentTypes) {
        this.dependentTypes = dependentTypes;
    }

    public ClientDef getMainClient() {
        return mainClient;
    }

    public void setMainClient(ClientDef mainClient) {
        this.mainClient = mainClient;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public Long getAge() {
        return age;
    }

    public BigDecimal getPrevPremium() {
        return prevPremium;
    }

    public void setPrevPremium(BigDecimal prevPremium) {
        this.prevPremium = prevPremium;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getChildType() {
        return childType;
    }

    public void setChildType(String childType) {
        this.childType = childType;
    }

    public String getMemberShipNo() {
        return memberShipNo;
    }

    public void setMemberShipNo(String memberShipNo) {
        this.memberShipNo = memberShipNo;
    }

    public Date getWetDate() {
        return wetDate;
    }

    public void setWetDate(Date wetDate) {
        this.wetDate = wetDate;
    }

    public Date getWefDate() {
        return wefDate;
    }

    public void setWefDate(Date wefDate) {
        this.wefDate = wefDate;
    }

    public String getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }

    public BigDecimal getComputedPremium() {
        return computedPremium;
    }

    public void setComputedPremium(BigDecimal computedPremium) {
        this.computedPremium = computedPremium;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getAutoGenerateCard() {
        return autoGenerateCard;
    }

    public void setAutoGenerateCard(String autoGenerateCard) {
        this.autoGenerateCard = autoGenerateCard;
    }

    public String getMemberHasCard() {
        return memberHasCard;
    }

    public void setMemberHasCard(String memberHasCard) {
        this.memberHasCard = memberHasCard;
    }

    public Date getPrevwefDate() {
        return prevwefDate;
    }

    public void setPrevwefDate(Date prevwefDate) {
        this.prevwefDate = prevwefDate;
    }

    public Date getPrevwetDate() {
        return prevwetDate;
    }

    public void setPrevwetDate(Date prevwetDate) {
        this.prevwetDate = prevwetDate;
    }

    public Long getPrevsectId() {
        return prevsectId;
    }

    public void setPrevsectId(Long prevsectId) {
        this.prevsectId = prevsectId;
    }

    public BigDecimal getStampDuty() {
        return stampDuty;
    }

    public void setStampDuty(BigDecimal stampDuty) {
        this.stampDuty = stampDuty;
    }

    public BigDecimal getPhfFund() {
        return phfFund;
    }

    public void setPhfFund(BigDecimal phfFund) {
        this.phfFund = phfFund;
    }

    public BigDecimal getTrainingLevy() {
        return trainingLevy;
    }

    public void setTrainingLevy(BigDecimal trainingLevy) {
        this.trainingLevy = trainingLevy;
    }

    public BigDecimal getWhtax() {
        return whtax;
    }

    public void setWhtax(BigDecimal whtax) {
        this.whtax = whtax;
    }

    public BigDecimal getExtras() {
        return extras;
    }

    public void setExtras(BigDecimal extras) {
        this.extras = extras;
    }
}

