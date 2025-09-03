package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.trans.model.CommissionPayments;
import com.brokersystems.brokerapp.trans.model.ReceiptSettlementDetails;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by peter on 3/26/2017.
 */
@Entity
@Table(name="sys_brk_payment_audit")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentAudit {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="pa_Id")
    @Setter
    private Long paId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="pa_trans_no")
    @Setter
    private SystemTransactions transNo;

    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="pa_settle_cr_id")
    @Setter
    private ReceiptSettlementDetails settlements;

    @Column(name = "pa_settle_rec_ref_no")
    private String receiptTransNo;

    @Column(name = "pa_settle_dr_ref_no")
    private String debitTransNo;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="pa_other_trans")
    @Setter
    private SystemTransactions otherTransNo;

    @Column(name="pa_comm")
    @Setter
    private BigDecimal commAmount;

    @Column(name="pa_whtx")
    @Setter
    private BigDecimal whtxAmount;

    @Column(name="pa_amount")
    @Setter
    private BigDecimal paymentAmount;

    @Column(name="pa_posted")
    private String posted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="pa_posted_by")
    @Setter
    private User postedBy;

    @Column(name="pa_post_dt")
    @Setter
    private Date postDate;

    @Column(name="pa_cancelled")
    private String cancelled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="pa_cancelled_by")
    @Setter
    private User cancelledBy;

    @Column(name="pa_canc_dt")
    @Setter
    private Date cancDate;

    @Column(name="pa_trans_type")
    private String transType;

    @Column(name="pa_paid_status")
    private String paidStatus;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="pa_comm_trans_id")
    private CommissionPayments commissionPayments;


    private void validateAndSanitize(String input, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setReceiptTransNo(String receiptTransNo) {
        validateAndSanitize(receiptTransNo, "Receipt Transaction Number");
        this.receiptTransNo = receiptTransNo == null ? null : StringEscapeUtils.escapeHtml4(receiptTransNo.trim());
    }

    public void setDebitTransNo(String debitTransNo) {
        validateAndSanitize(debitTransNo, "Debit Transaction Number");
        this.debitTransNo = debitTransNo == null ? null : StringEscapeUtils.escapeHtml4(debitTransNo.trim());
    }

    public void setPosted(String posted) {
        validateAndSanitize(posted, "Posted");
        this.posted = posted == null ? null : StringEscapeUtils.escapeHtml4(posted.trim());
    }

    public void setCancelled(String cancelled) {
        validateAndSanitize(cancelled, "Cancelled");
        this.cancelled = cancelled == null ? null : StringEscapeUtils.escapeHtml4(cancelled.trim());
    }

    public void setTransType(String transType) {
        validateAndSanitize(transType, "Transaction Type");
        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
    }

    public void setPaidStatus(String paidStatus) {
        validateAndSanitize(paidStatus, "Paid Status");
        this.paidStatus = paidStatus == null ? null : StringEscapeUtils.escapeHtml4(paidStatus.trim());
    }
}