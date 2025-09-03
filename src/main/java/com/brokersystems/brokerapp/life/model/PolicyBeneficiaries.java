package com.brokersystems.brokerapp.life.model;

import com.brokersystems.brokerapp.setup.model.RelationshipTypes;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by waititu on 03/12/2017.
 */

@Entity
@Table(name="sys_brk_pol_beneficiaries")
public class PolicyBeneficiaries {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="pben_code")
    private Long beneficiaryCode;

    @Column(name="pben_name")
    private String beneficiaryName;

    @Column(name="pben_post_address")
    private String beneficiarypostalAddress;

    @Column(name="pben_email_address")
    private String beneficiaryemailAddress;

    @Column(name="pben_tel_no")
    private String beneficiarytelNo;

    @Column(name="pben_allocation")
    private BigDecimal benAllocation;

    @Column(name="pben_date_reg")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date dateRegistered;

    @Column(name="pben_Id_reg_no")
    private String beneficiaryregNo;

    @Column(name="pben_dt_created")
    private Date dateCreated;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="pben_pol_id",nullable = false)
    private PolicyTrans policy;

    @ManyToOne
    @JoinColumn(name="pben_created_by",nullable=false)
    private User user;


    @ManyToOne
    @JoinColumn(name="pben_relation_type")
    private RelationshipTypes relationshipType;

    @Column(name="pben_relationship")
    private String beneficiaryRelationship;


    public String getBeneficiaryRelationship() {
        return beneficiaryRelationship;
    }

    public void setBeneficiaryRelationship(String beneficiaryRelationship) {
        this.beneficiaryRelationship = beneficiaryRelationship;
    }

    public Date getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(Date dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public Long getBeneficiaryCode() {
        return beneficiaryCode;
    }

    public void setBeneficiaryCode(Long beneficiaryCode) {
        this.beneficiaryCode = beneficiaryCode;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }
    public String getBeneficiarypostalAddress() {
        return beneficiarypostalAddress;
    }

    public void setBeneficiarypostalAddress(String beneficiarypostalAddress) {
        this.beneficiarypostalAddress = beneficiarypostalAddress;
    }

    public String getBeneficiaryemailAddress() {
        return beneficiaryemailAddress;
    }

    public void setBeneficiaryemailAddress(String beneficiaryemailAddress) {
        this.beneficiaryemailAddress = beneficiaryemailAddress;
    }

    public String getBeneficiarytelNo() {
        return beneficiarytelNo;
    }

    public void setBeneficiarytelNo(String beneficiarytelNo) {
        this.beneficiarytelNo = beneficiarytelNo;
    }

    public BigDecimal getBenAllocation() {
        return benAllocation;
    }

    public void setBenAllocation(BigDecimal benAllocation) {
        this.benAllocation = benAllocation;
    }

    public String getBeneficiaryregNo() {
        return beneficiaryregNo;
    }

    public void setBeneficiaryregNo(String beneficiaryregNo) {
        this.beneficiaryregNo = beneficiaryregNo;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PolicyTrans getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyTrans policy) {
        this.policy = policy;
    }

    public RelationshipTypes getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(RelationshipTypes relationshipType) {
        this.relationshipType = relationshipType;
    }
}
