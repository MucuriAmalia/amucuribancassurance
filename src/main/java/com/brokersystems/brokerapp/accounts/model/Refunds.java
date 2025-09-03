package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.model.PaymentModes;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

/**
 * Created by waititu on 29/11/2018.
 */
@Entity
@Table(name="sys_brk_refunds")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Refunds {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ref_Id")
    @Setter
    private Long refId;

    @ManyToOne
    @JoinColumn(name="ref_pol_Id",nullable=false)
    @Setter
    private PolicyTrans policy;

    @ManyToOne
    @JoinColumn(name="ref_trans_no",nullable=false)
    @Setter
    private SystemTransactions transactions;

    @Column(name="ref_amount",nullable=false)
    @Setter
    private BigDecimal amount;

    @Column(name="ref_narrations",nullable=false)
    private String narrations;

    @Column(name="ref_reject_remarks")
    private String rejectionRemarks;

    @Column(name="ref_payee",nullable=false)
    private String payee;

    @ManyToOne
    @JoinColumn(name="ref_client_id",nullable=false)
    @Setter
    private ClientDef client;

    @Column(name="ref_capture_date",nullable=false)
    @Setter
    private Date refundCaptureDate;

    @Column(name="ref_authorized_date")
    @Setter
    private Date refundAuthDate;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="ref_auth_user")
    @Setter
    private User authBy;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="ref_makeready_user")
    @Setter
    private User madeReadyBy;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="ref_reject_user")
    @Setter
    private User rejectedBy;

    @Column(name="ref_rejected_date")
    @Setter
    private Date rejectedDate;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="ref_capture_user",nullable=false)
    @Setter
    private User createdUser;

    @Column(name="ref_makeready_date")
    @Setter
    private Date makeReadyDate;

    @Column(name="ref_status",nullable=false)
    private String refundStatus;

    @ManyToOne
    @JoinColumn(name="ref_pm_id")
    @Setter
    private PaymentModes paymentMode;

    @Transient
    @Setter
    private Long transNoToRefund;

    @Column(name="ref_ft_number",length = 50)
    private String ftNo;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setNarrations(String narrations) {
        validateAndSanitize(narrations, DESC_PATTERN, "Narrations");
        this.narrations = narrations == null ? null : StringEscapeUtils.escapeHtml4(narrations.trim());
    }

    public void setRejectionRemarks(String rejectionRemarks) {
        validateAndSanitize(rejectionRemarks, DESC_PATTERN, "Rejection Remarks");
        this.rejectionRemarks = rejectionRemarks == null ? null : StringEscapeUtils.escapeHtml4(rejectionRemarks.trim());
    }

    public void setPayee(String payee) {
        validateAndSanitize(payee, NAME_PATTERN, "Payee");
        this.payee = payee == null ? null : StringEscapeUtils.escapeHtml4(payee.trim());
    }

    public void setRefundStatus(String refundStatus) {
        validateAndSanitize(refundStatus, NAME_PATTERN, "Refund Status");
        this.refundStatus = refundStatus == null ? null : StringEscapeUtils.escapeHtml4(refundStatus.trim());
    }

    public void setFtNo(String ftNo) {
        validateAndSanitize(ftNo, NAME_PATTERN, "FT Number");
        this.ftNo = ftNo == null ? null : StringEscapeUtils.escapeHtml4(ftNo.trim());
    }
}