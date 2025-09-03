package com.brokersystems.brokerapp.auditlogs.model;

import com.brokersystems.brokerapp.setup.model.RejectedReasons;
import com.brokersystems.brokerapp.setup.model.User;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import springfox.documentation.spring.web.json.Json;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@TypeDefs(@TypeDef(name = "json", typeClass = JsonBinaryType.class))
@Table(name = "sys_brk_audit_trails")
@Data
public class RtsAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "audit_id")
    private Long id;

    @Column(name = "field_name", length = 100)
    private String fieldName;

    @Column(name = "old_value", length = 1000)
    private String oldValue;

    @Column(name = "new_value", length = 1000)
    private String newValue;

    @Column(name = "audit_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date auditTime;

    public RejectedReasons getRejectionReason() {
        return rejectionReason;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id")
    private RejectedReasons rejectionReason;

    @Column(name = "resubmission_comment", length = 1000)
    private String resubmissionComment;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "task_code")
    private Long taskCode;

    @Column(name = "user_rejected_reason", length = 1000) // New field for user-entered rejection reason
    private String userRejectedReason;

    @Column(name = "task_pol_id")
    private Long policyId;



    @Column(name = "rts_task_pol_id")
    private Long rtsPolicyId;

    public String getPolicyInfo() {
        return policyInfo;
    }

    public void setPolicyInfo(String policyInfo) {
        this.policyInfo = policyInfo;
    }

    @Column(name = "json_data")
    private String policyInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id")
    private User makerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checker_id")
    private User checkerId;

    public void setResubmissionComment(String resubmissionComment) {
        this.resubmissionComment = resubmissionComment;
    }

    public void setPolicyId(long policyId) {this.policyId = policyId;}

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public void setMakerId(User makerId) {
        this.makerId = makerId;
    }

    public void setRejectionReason(RejectedReasons rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setTaskCode(long taskCode) {
        this.taskCode = taskCode;
    }

    public void setCheckerId(User checkerId) {
        this.checkerId = checkerId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public void setId(long audit_id) {
        this.id = audit_id;
    }

    public void setRrtsPolId(Long polCode) {
    }

    public void setUserRejectedReason(String userRejectedReason) {
        this.userRejectedReason = userRejectedReason;
    }

    public Long getRtsPolicyId() {
        return rtsPolicyId;
    }

    public void setRtsPolicyId(Long rtsPolicyId) {
        this.rtsPolicyId = rtsPolicyId;
    }
}