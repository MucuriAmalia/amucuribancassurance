package com.brokersystems.brokerapp.trans.model;

import com.brokersystems.brokerapp.uw.model.PolicyTrans;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_ws_receipt")
public class WebServiceReceipts {

    @Id
    @SequenceGenerator(name = "webServiceReceiptSeq",sequenceName = "web_service_receipt_seq",allocationSize=1)
    @GeneratedValue(generator = "webServiceReceiptSeq")
    @Column(name="wsr_no")
    private Long wsrNo;

    @Column(name="wsr_pymt_mode",length = 30)
    private String paymentMode;
    @Column(name = "wsr_amt_paid")
    private BigDecimal amountPaid;
    @Column(name="wsr_payment_ref",length = 80,nullable = false)
    private String paymentRef;
    @Column(name="wsr_payee", nullable = false,length = 200)
    private String payeeName;
    @Column(name = "wsr_receipted",length = 1)
    private String receipted;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="wsr_receipt_id")
    private ReceiptTrans receipt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="wsr_pol_id")
    private PolicyTrans policyTrans;

    public Long getWsrNo() {
        return wsrNo;
    }

    public void setWsrNo(Long wsrNo) {
        this.wsrNo = wsrNo;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getReceipted() {
        return receipted;
    }

    public void setReceipted(String receipted) {
        this.receipted = receipted;
    }

    public ReceiptTrans getReceipt() {
        return receipt;
    }

    public void setReceipt(ReceiptTrans receipt) {
        this.receipt = receipt;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
    }
}
