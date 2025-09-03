package com.brokersystems.brokerapp.users.model;

import com.brokersystems.brokerapp.setup.model.RejectedReasons;
import com.brokersystems.brokerapp.setup.model.User;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@TypeDefs(@TypeDef(name = "json", typeClass = JsonBinaryType.class))
@Table(name ="sys_brk_maker_checker")
@Data
public class MakerChecker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="mck_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiatorId;

    @Column(nullable = false,name = "task_name")
    private String taskName;

    @Type(type = "json")
    @Column(columnDefinition = "json",name = "task_json")
    private String taskJson;

    @Column(name = "made_on_date",nullable = false)
    private Date madeOnDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="maker_id")
    private User makerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="checker_id")
    private User checkerId;

    @Column(name = "check_on_date")
    private Date checkDate;

    @Column(name = "check_status", length = 1)
    private String status;

    @Column(name = "maker_task_code", nullable = false)
    private Long taskCode;

    @Column(name = "maker_task_type",length = 100,nullable = false)
    private String taskType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id")
    private RejectedReasons rejectionReason;

    @Column(name = "rejected_reason",length = 1000)
    private String rejectedReason;

    @Column(name = "resubmission_comment", length = 1000)
    private String resubmissionComment;

    @Column(name = "maker_modified_date")
    private Date modifiedDate;

    @Type(type = "json")
    @Column(name = "checkers", columnDefinition = "json")
    private String assignedCheckers;

    @Column(name = "mck_overdue")
    private String taskOverdue;

    @Column(name = "task_pol_id")
    private Long policyId;

    @Column(name = "task_acct_id")
    private Long acctId;

    public void setResubmissionComment(String resubmissionComment) {
        this.resubmissionComment = resubmissionComment;
    }
}
