package com.brokersystems.brokerapp.jobs.jobs.receipttracker;

import com.brokersystems.brokerapp.trans.model.ReceiptTrans;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import lombok.*;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "sys_brk_fcr_receipt_tracker")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long policyId;

    private Long receiptId;

    private String transactionRef;

    private String planType;

    private String comments;

    private BigDecimal premium;

    private BigDecimal amount;

    private Integer term;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)")
    private ReceiptStatus receiptStatus;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(columnDefinition = "DATE")
    @Temporal(TemporalType.DATE)
    private Date receiptDate;

    @PrePersist
    public void prePersist() {
        this.createdAt = DateTime.now().toDate();
    }

    @PreUpdate
    public void preUpdate() { this.updatedAt = DateTime.now().toDate(); }

    public static ReceiptTracker buildTracker(ReceiptTrans receiptTrans, PolicyTrans policyTrans,
                                              String comments, ReceiptStatus receiptStatus) {
        return ReceiptTracker.builder()
                .policyId(policyTrans != null ? policyTrans.getPolicyId() : null)
                .receiptId(receiptTrans != null ? receiptTrans.getReceiptId() : null)
                .transactionRef(receiptTrans != null ? receiptTrans.getPaymentRef() : null)
                .planType(policyTrans != null && policyTrans.getProduct() != null ? policyTrans.getProduct().getProShtDesc() : null)
                .comments(comments)
                .premium(policyTrans != null ? policyTrans.getPremium() : null)
                .amount(receiptTrans != null ? receiptTrans.getReceiptAmount() : null)
                .term(policyTrans != null ? policyTrans.getPolTerm() : null)
                .receiptStatus(receiptStatus)
                .build();
    }
}
