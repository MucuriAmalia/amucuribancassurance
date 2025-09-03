package com.brokersystems.brokerapp.trans.model;

import com.brokersystems.brokerapp.setup.model.AccountDef;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@ToString
@Entity
@Table(name="sys_brk_comm_process_trans")
public class CommissionTrans {

    @Id
    @SequenceGenerator(name = "commissionProcessSeq",sequenceName = "commissionProcessSeq",allocationSize=1)
    @GeneratedValue(generator = "commissionProcessSeq")
    @Column(name="trans_id")
    private Long transId;

    @Column(name = "trans_debit_no", nullable = false,unique = true)
    private String debitNoteNo;

    @Column(name = "trans_policy_no")
    private String policyNo;

    @Column(name = "trans_receipt_no")
    private String receiptNo;

    @Column(name = "trans_amount")
    private BigDecimal amount;

    @Column(name = "trans_ins_policy_no")
    private String inspolicyNo;

    @Column(name = "trans_status")
    private String status;

    @Column(name = "trans_insurance")
    private Long insurance;

    @Column(name = "trans_receipt")
    private Long receipt;

    @Column(name = "trans_date")
    private Date date;

    public Long getTransId() {
        return transId;
    }

    public void setTransId(Long transId) {
        this.transId = transId;
    }

    public String getDebitNoteNo() {
        return debitNoteNo;
    }

    public void setDebitNoteNo(String debitNoteNo) {
        this.debitNoteNo = debitNoteNo;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getInspolicyNo() {
        return inspolicyNo;
    }

    public void setInspolicyNo(String inspolicyNo) {
        this.inspolicyNo = inspolicyNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
