package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.AuditBaseEntity;
import com.brokersystems.brokerapp.setup.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "sys_brk_bulk_pol_renewals")
public class BulkRenewal  extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "bulk_renewal_id")
    private Long Id;

    @Column(name = "policy_id")
    private Long policyId;

    @Column(name = "policy_number")
    private String policyNumber;

    @Column(name = "bulk_policy_processed")
    private String policyProcessed;

    @Column(name = "pol_proposal_no")
    private String polProposalNo;

    @Column(name = "pol_no")
    private String polNo;

    @Column(name = "bulk_policy_premium")
    private BigDecimal oldPrem;

    @Column(name = "bulk_policy_sum_insured")
    private BigDecimal oldSumInsured;

    @Column(name = "new_prem")
    private BigDecimal newPrem;

    @Column(name = "new_pol_id")
    private Long newPolId;

    @Column(name = "new_sum_insured")
    private BigDecimal newSumInsured;

    @Column(name = "client_cif")
    private String clientCif;

    @Column(name = "client_pin")
    private String clientPin;

    @Column(name = "bulk_policy_clnt_fname")
    private String clientFname;

    @Column(name = "bulk_policy_clnt_other_names")
    private String clientOtherNames;

    @Column(name = "bulk_policy_authorized")
    private String transProcessed;

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(name = "bulk_policy_date_uploaded")
    private Date bulkPolicyDateUploaded;

    @Column(name = "bulk_policy_wef")
    private Date bulkPolicyWef;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "bulk_policy_cover_type")
    private String coverType;

    @Column(name = "bulk_product_group")
    private String productGroup;

    @Column(name = "new_interface")
    private String newInterface;

    @Column(name="accrual_instl_date")
    @Temporal(TemporalType.DATE)
    private Date accrualInstDate;

    @Column(name = "accrual_payment_type")
    private String accrualPaymentType;

    @Column(name = "bulk_renewal_dt")
    private Date bulkPolicyRenewalDate;
}
