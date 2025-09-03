package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "sys_brk_credit_shield_bulk_upload")
public class CreditShield extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "shield_id")
    private Long sheildId;

    @Column(name = "card_client_fname")
    private String clientFName;

    @Column(name = "card_client_othernames")
    private String clientOtherNames;

    @Column(name = "card_client_cif")
    private String clientCIF;

    @Column(name = "card_client_dob")
    private Date clientDOB;

    @Column(name = "card_client_pin")
    private String clientPin;

    @Column(name = "client_phone")
    private String clientPhone;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_id")
    private String clientID;

    @Column(name = "card_cover_type")
    private String coverType;

    @Column(name = "card_product_group")
    private String productGroup;

    @Column(name = "card_product_name")
    private String productName;

    @Column(name = "card_trans_date")
    private Date transDate;

    @Column(name = "card_sum_insured")
    private BigDecimal sumInsured;

    @Column(name = "card_premium_amount")
    private BigDecimal cardPremium;

    @Column(name = "card_payment_frequency")
    private String frequency;

    @Column(name = "card_cover_from")
    private Date coverFrom;

    @Column(name = "card_cover_to")
    private Date coverTo;

    @Column(name = "card_upload_date")
    private Date uploadedDate;

    @Column(name = "card_processed_date")
    private Date processedDate;

    @Column(name = "card_trans_status")
    private String transStatus;

    @Column(name = "card_trans_code")
    private String transCode;

    @Column(name = "card_trans_type")
    private String transType;

    @Column(name = "card_sales_agent")
    private String salesAgent;

    @Column(name = "card_sales_manager")
    private String salesManager;

    @Column(name = "card_sales_code")
    private String salesCode;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "card_acct_no")
    private String cardAccount;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="card_client_type")
    private ClientTypes clientType;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "card_insurer_code")
    private AccountDef insurerCode;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "card_currecy_code")
    private Currencies currencyCode;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "card_processed_by")
    private User processedBy;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "card_uploaded_by")
    private User uploadedBy;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "card_branch")
    private OrgBranch branch;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "card_pol_id")
    private PolicyTrans policyTrans;

    @Column(name = "credit_shield_term")
    private Integer shieldTerm;

    @Column(name = "bulk_policy_authorized")
    private String policyAuthorized;
}
