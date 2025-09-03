package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.User;
import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "sys_brk_bulk_cn_policy")
public class BulkPolCnCreation {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "bulk_policy_id")
    private Long bulkPolicyId;

    @Column(name = "policy_id")
    private Long  policyId;

    @Column(name = "batch_id")
    private String batchId;

    @Column(name = "pol_no")
    private String polNo;

    @Column(name = "pol_proposal_no")
    private String polProposalNo;

    @Column(name = "en_remarks")
    private String enRemarks;

    @Column(name = "policy_current_status")
    private String policyCurrentStatus;

    @Column(name = "bulk_policy_client_pin")
    private String clientPin;

    @Column(name = "bulk_policy_client_cif")
    private String clientCif;

    @Column(name = "bulk_policy_sum_insured")
    private BigDecimal sumInsured;

    @Column(name = "bulk_policy_premium")
    private BigDecimal premium;

    @Column(name = "bulk_policy_cover_type")
    private String coverType;

    @Column(name = "bulk_policy_clnt_fname")
    private String clientFname;

    @Column(name = "bulk_policy_risk_trans")
    private Long riskTrans;

    @Column(name = "bulk_policy_wef")
    private Date coverDateFrom;

    @Column(name = "bulk_policy_clnt_other_names")
    private String clientOtherNames;

    @Column(name = "bulk_policy_cn_effective_date")
    private Date effectiveDate;

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

    @Column(name = "bulk_policy_processed", length = 10)
    private String transProcessed;

    @Column(name = "refund_amount")
    private BigDecimal refundAmt;

    @Column(name = "bulk_policy_authorized", length = 10)
    private String transAuthorized;

    @Column(name = "bulk_cn_pol_id")
    private Long bulkCnPolId;

    @Column(name = "cancelation_Amt")
    private BigDecimal cancelationAmt;

    @Column(name = "product_group")
    private String productGroup;
}
