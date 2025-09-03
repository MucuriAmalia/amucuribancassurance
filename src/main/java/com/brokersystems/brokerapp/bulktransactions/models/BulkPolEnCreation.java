package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "sys_brk_bulk_en_policy")
public class BulkPolEnCreation {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "bulk_policy_id")
    private Long bulkPolicyId;

    @Column(name = "policy_id")
    private Long  policyId;

    @Column(name = "pol_no")
    private String polNo;

    @Column(name = "pol_proposal_no")
    private String polProposalNo;

    @Column(name = "en_remarks")
    private String enRemarks;

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

    @Column(name = "bulk_effective")
    private Date effectiveDate;

    @Column(name = "bulk_policy_clnt_other_names")
    private String clientOtherNames;

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

    @Column(name = "bulk_policy_authorized", length = 10)
    private String transAuthorized;

    @Column(name = "en_policy_id")
    private Long  enPolicyId;

    @Column(name = "bulk_policy_risk_desc", columnDefinition = "TEXT")
    private String riskDesc;

    @Column(name="bulk_policy_ref_code")
    private String refCode;

    @Column(name = "en_direction")
    private String direction;


    @Column(name = "upload_pro_grp")
    private String productGroup;
}
