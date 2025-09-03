package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.AuditBaseEntity;
import com.brokersystems.brokerapp.setup.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "sys_brk_bulk_pol_refunds")
public class BulkRefunds extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "bulk_refund_id")
    private Long Id;

    @Column(name = "batch_id")
    private String batchId;

    @Column(name = "bulk_policy_processed")
    private String policyProcessed;

    @Column(name = "pol_proposal_no")
    private String polProposalNo;

    @Column(name = "original_pol_id")
    private Long oPolId;

    @Column(name = "refund_pol_id")
    private Long rPolId;

    @Column(name = "pol_no")
    private String polNo;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "bulk_policy_cover_type")
    private String coverType;

    @Column(name = "bulk_product_group")
    private String productGroup;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Column(name = "policy_refundable_amount")
    private BigDecimal policyRefundableAmount;

    @Column(name = "bulk_policy_sum_insured")
    private BigDecimal sumInsured;

    @Column(name = "bulk_policy_premium")
    private BigDecimal basicPremium;

    @Column(name = "bulk_policy_wef")
    private Date bulkPolicyWef;

    @Column(name = "bulk_policy_wet")
    private Date bulkPolicyWet;

    @Column(name = "pol_trans_type")
    private String transType;

    @Column(name = "client_cif")
    private String clientCif;

    @Column(name = "client_pin")
    private String clientPin;

    @Column(name = "bulk_policy_clnt_fname")
    private String clientFname;

    @Column(name = "bulk_policy_clnt_other_names")
    private String clientOtherNames;

    @Column(name = "bulk_policy_authorized")
    private String transAuthorized;

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(name = "bulk_policy_date_uploaded")
    private Date bulkPolicyDateUploaded;
}
