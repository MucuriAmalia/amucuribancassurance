package com.brokersystems.brokerapp.trans.model;

import com.brokersystems.brokerapp.accounts.model.PaymentAudit;
import com.brokersystems.brokerapp.medical.model.SelfFundParams;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;


@Entity
@Table(name="sys_brk_receipt_dtls")
public class ReceiptTransDtls {
	
	@Id
	@SequenceGenerator(name = "receiptDetSeq",sequenceName = "receipt_det_seq",allocationSize=1)
	@GeneratedValue(generator = "receiptDetSeq")
	@Column(name="rect_id")
	private Long receiptId;
	
	@XmlTransient
	@ManyToOne
	@JoinColumn(name="rect_receipt_no")
	private ReceiptTrans receipt;
	
	@Column(name="rect_account")
	private String account;
	
	@Column(name="rect_endorse_no")
	private String endorsementNumber;
	
	@Column(name="rect_amount")
	private BigDecimal rctAmount;
	
	@Column(name="rect_narration")
	private String narration;
	
	@Column(name="rect_allocated")
	private String allocated;
	
	@XmlTransient
	@ManyToOne
	@JoinColumn(name="rect_trans_no")
	private SystemTransactions transaction;

	@XmlTransient
	@ManyToOne
	@JoinColumn(name="rect_temp_trans_no")
	private SystemTransactionsTemp transactionsTemp;


	@XmlTransient
	@ManyToOne
	@JoinColumn(name="rect_pol_id")
	private PolicyTrans policy;

	@XmlTransient
	@ManyToOne
	@JoinColumn(name="rect_sfp_id")
	private SelfFundParams fundParams;
	
	@Transient
	private Long transNo;

	@Transient
	private Long transTempNo;
	
	@Column(name="rect_dc", nullable=false)
	private String rctDC;
	
	@Column(name="rect_type")
	private String rctType;

	@Column(name = "rect_withdrawn")
	private String withdrawn;

	@XmlTransient
	@ManyToOne
	@JoinColumn(name = "rect_withdrawn_by")
	private User userWithdrawn;

	@Column(name = "rect_withdrawn_date")
	private Date withdrawnDate;

	@ManyToOne
	@JoinColumn(name="rect_payment_id")
	private PaymentAudit paymentAudit;

	public PaymentAudit getPaymentAudit() {
		return paymentAudit;
	}

	public void setPaymentAudit(PaymentAudit paymentAudit) {
		this.paymentAudit = paymentAudit;
	}

	public String getWithdrawn() {
		return withdrawn;
	}

	public void setWithdrawn(String withdrawn) {
		this.withdrawn = withdrawn;
	}

	public User getUserWithdrawn() {
		return userWithdrawn;
	}

	public void setUserWithdrawn(User userWithdrawn) {
		this.userWithdrawn = userWithdrawn;
	}

	public Date getWithdrawnDate() {
		return withdrawnDate;
	}

	public void setWithdrawnDate(Date withdrawnDate) {
		this.withdrawnDate = withdrawnDate;
	}

	public Long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(Long receiptId) {
		this.receiptId = receiptId;
	}

	public ReceiptTrans getReceipt() {
		return receipt;
	}

	public void setReceipt(ReceiptTrans receipt) {
		this.receipt = receipt;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	

	public String getEndorsementNumber() {
		return endorsementNumber;
	}

	public void setEndorsementNumber(String endorsementNumber) {
		this.endorsementNumber = endorsementNumber;
	}

	public BigDecimal getRctAmount() {
		return rctAmount;
	}

	public void setRctAmount(BigDecimal rctAmount) {
		this.rctAmount = rctAmount;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	

	public SystemTransactions getTransaction() {
		return transaction;
	}

	public void setTransaction(SystemTransactions transaction) {
		this.transaction = transaction;
	}

	public Long getTransNo() {
		return transNo;
	}

	public void setTransNo(Long transNo) {
		this.transNo = transNo;
	}

    public Long getTransTempNo() {
        return transTempNo;
    }

    public void setTransTempNo(Long transTempNo) {
        this.transTempNo = transTempNo;
    }

    public String getRctDC() {
		return rctDC;
	}

	public void setRctDC(String rctDC) {
		this.rctDC = rctDC;
	}

	public String getRctType() {
		return rctType;
	}

	public void setRctType(String rctType) {
		this.rctType = rctType;
	}

	public String getAllocated() {
		return allocated;
	}

	public void setAllocated(String allocated) {
		this.allocated = allocated;
	}

	public PolicyTrans getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyTrans policy) {
		this.policy = policy;
	}

	public SelfFundParams getFundParams() {
		return fundParams;
	}

	public void setFundParams(SelfFundParams fundParams) {
		this.fundParams = fundParams;
	}

	public SystemTransactionsTemp getTransactionsTemp() {
		return transactionsTemp;
	}

	public void setTransactionsTemp(SystemTransactionsTemp transactionsTemp) {
		this.transactionsTemp = transactionsTemp;
	}

	@Override
	public String toString() {
		return "ReceiptTransDtls{" +
				"receiptId=" + receiptId +
				", receipt=" + receipt +
				", account='" + account + '\'' +
				", endorsementNumber='" + endorsementNumber + '\'' +
				", rctAmount=" + rctAmount +
				", narration='" + narration + '\'' +
				", allocated='" + allocated + '\'' +
				", transaction=" + transaction +
				", fundParams=" + fundParams +
				", transNo=" + transNo +
				", rctDC='" + rctDC + '\'' +
				", rctType='" + rctType + '\'' +
				'}';
	}
}
