package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.AccountDef;
import com.brokersystems.brokerapp.setup.model.AuditBaseEntity;
import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
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
@Table(name = "sys_brk_bulk_staff_motors")
public class BulkStaffMotorsCreation extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "bulk_policy_id")
    private Long bulkPolicyId;

    @Column(name = "bulk_policy_cover_id", nullable = false)
    private String coverId;

    @Column(name="bulk_policy_insured_name")
    private String insuredName;

    @Column(name = "bulk_policy_ab_number")
    private String aBNumber;

    @Column(name = "bulk_policy_clnt_id_no")
    private String clientIdNo;

    @Column(name = "bulk_policy_client_kra_pin")
    private String clientKraPin;

    @Column(name = "bulk_policy_client_acc_no")
    private String accountNo;

    @Column(name = "bulk_policy_client_email")
    private String clientEmail;

    @Column(name = "bulk_policy_client_phone_no")
    private String phoneNumber;

    @Column(name = "motor_body_type")
    private String motorBodyType;

    @Column(name = "motor_color")
    private String motorColor;

    @Column(name = "motor_load_capacity")
    private BigDecimal motorLoadCapacity;

    @Column(name = "tare")
    private String tare;

    @Column(name = "motor_no_of_passengers")
    private double numberOfPassengers;

    @Column(name = "motor_registration_no")
    private String registrationNo;

    @Column(name = "motor_make_model")
    private String makeModel;

    @Column(name = "motor_yom")
    private String yom;

    @Column(name = "motor_rating")
    private double rating;

    @Column(name = "motor_sum_insured")
    private BigDecimal sumInsured;

    @Column(name = "motor_policy_wef")
    private Date coverDateFrom;

    @Column(name = "motor_chasis_no")
    private String chasisNo;

    @Column(name = "motor_engine_no")
    private String engineNo;

    @Column(name = "underwriter")
    private String underwriter;

    @Column(name = "product_group")
    private String productGroup;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "cover_type")
    private String coverType;

    @Column(name = "cover_option")
    private String coverOption;

    @Column(name = "excess_buy_back_option")
    private String  buyBackOption;

    @Column(name = "excess_buy_back")
    private String  buyBack;

    @Column(name = "courtsey_cars_option")
    private String  coutseyCarsOption;

    @Column(name = "loss_of_use")
    private BigDecimal  lossOfUse;

    @Column(name = "aa_service_option")
    private String aaServiceOption;

    @Column(name = "aa_service")
    private BigDecimal aaService;

    @Column(name = "amref_option")
    private String amrefOption;

    @Column(name = "amref")
    private BigDecimal amref;

    @Column(name = "basic_prem")
    private BigDecimal basicPrem;

    @Column(name = "excess_protector")
    private BigDecimal excessProtector;

    @Column(name = "pvt")
    private BigDecimal pvt;

    @Column(name = "tax")
    private BigDecimal tax;

    @Column(name = "pll")
    private BigDecimal pll;

    @Column(name = "total_prem")
    private BigDecimal totalPrem;

    @Column(name = "cover_status")
    private int coverStatus;

    @Column(name = "application_type")
    private String applicationType;

    @Column(name = "date_submitted")
    private String dateSubmitted;


    @Column(name = "bulk_policy_renew_date")
    private Date renewDate;

    @Column(name = "bulk_policy_currency")
    private String currency;


    @Column(name = "bulk_policy_authorized")
    private String policyAuthorized;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "bulk_policy_underwriter")
    private AccountDef insurerCode;


    @Column(name = "bulk_policy_processed", length = 10)
    private String transProcessed;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "bulk_policy_uploaded_by")
    private User uploadedBy;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "bulk_policy_processed_by")
    private User processedBy;

    @Column(name = "bulk_policy_date_uploaded")
    private Date dateUploaded;

    @Column(name = "bulk_policy_date_processed")
    private Date dateProcessed;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "bulk_policy_pol_id")
    private PolicyTrans policyTrans;


    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bulk_policy_loaded_by")
    private User loadedBy;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bulk_policy_branch")
    private OrgBranch branch;

    @Column(name = "bulk_policy_frequency")
    private String frequency;

    @Column(name="bulk_policy_policy_type")
    private String policyType;

    @Column(name="bulk_policy_business_type")
    private String businessType;

    @Column(name="bulk_policy_ref_code")
    private String refCode;

    @Column(name = "bulk_policy_contract_name")
    private String contractName;

    @Column(name="pol_accrual_instl_date")
    @Temporal(TemporalType.DATE)
    private Date accrualInstDate;

    @Column(name = "accrual_payment_type")
    private String accrualPaymentType;
}
