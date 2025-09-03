package com.brokersystems.brokerapp.users.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MakerCheckDTO {

    private static final Logger logger = LoggerFactory.getLogger(MakerCheckDTO.class);

    private String taskName;
    private String json;
    private String status;
    private Long taskCode;
    private String taskType;
    private Long taskId;
    private Long checkerId;
    private Date madeOnDate;
    private String madeBy;
    private Long initiatorId;
    private String initiatorName;
    private String checkedBy;
    private Date checkedOnDate;
    private String rejectedReason;
    private String rejectionReasonDesc;
    private Long policyId;
    private Long acctId;
    private String assignedCheckers;
    private String userEmail;
    private String abNo;
    private Long makerId;
    private Long claimId;
    private Long referenceId;
    private String policyNumber;
    private Long riskId;
    private String claimNumber;
    private Date NextReviewDate;
    private String claimStatus;
    private String clientName;
    private String transType;
    private String resubmissionComment;

// With appropriate getters and setters


    public String getPolicyNumber() {
        return policyNumber;
    }


    // Getters and setters
    public Long getReferenceId() {
        return referenceId;
    }
    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Long getMakerId() {
        return makerId;
    }

    public void setMakerId(Long makerId) {
        this.makerId = makerId;
    }
    public void setInitiatorId(Long initiatorName) {
        this.initiatorId = initiatorId;
    }

    public String getResubmissionComment() {
        return resubmissionComment;
    }

    public void setResubmissionComment(String resubmissionComment) {
        this.resubmissionComment = resubmissionComment == null ? null : StringEscapeUtils.escapeHtml4(resubmissionComment.trim());
    }

  
//
//
//    private void validateAndSanitize(String input, String pattern, String fieldName) {
//        if (input == null) {
//            return;
//        }
//        //logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
//        String trimmedInput = input.trim();
//        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
//            throw new IllegalArgumentException(
//                    String.format("Invalid characters in %s", fieldName)
//            );
//        }
//    }
//
//    public void setTaskName(String taskName) {
//        validateAndSanitize(taskName, NAME_PATTERN, "Task Name");
//        this.taskName = taskName == null ? null : StringEscapeUtils.escapeHtml4(taskName.trim());
//    }
//
//    public void setJson(String json) {
//        validateAndSanitize(json, REF_PATTERN, "JSON");
//        this.json = json == null ? null : StringEscapeUtils.escapeHtml4(json.trim());
//    }
//
//    public void setStatus(String status) {
//        validateAndSanitize(status, REF_PATTERN, "Status");
//        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
//    }
//
//    public void setTaskType(String taskType) {
//        validateAndSanitize(taskType, REF_PATTERN, "Task Type");
//        this.taskType = taskType == null ? null : StringEscapeUtils.escapeHtml4(taskType.trim());
//    }
//
//    public void setMadeBy(String madeBy) {
//        validateAndSanitize(madeBy, CREATED_BY_PATTERN, "Made By");
//        this.madeBy = madeBy == null ? null : StringEscapeUtils.escapeHtml4(madeBy.trim());
//    }
//
//    public void setInitiatorName(String initiatorName) {
//        validateAndSanitize(initiatorName, NAME_PATTERN, "Initiator Name");
//        this.initiatorName = initiatorName == null ? null : StringEscapeUtils.escapeHtml4(initiatorName.trim());
//    }
//
//    public void setCheckedBy(String checkedBy) {
//        validateAndSanitize(checkedBy, CREATED_BY_PATTERN, "Checked By");
//        this.checkedBy = checkedBy == null ? null : StringEscapeUtils.escapeHtml4(checkedBy.trim());
//    }
//
//    public void setRejectedReason(String rejectedReason) {
//        validateAndSanitize(rejectedReason, DESC_PATTERN, "Rejected Reason");
//        this.rejectedReason = rejectedReason == null ? null : StringEscapeUtils.escapeHtml4(rejectedReason.trim());
//    }
//
//    public void setRejectionReasonDesc(String rejectionReasonDesc) {
//        validateAndSanitize(rejectionReasonDesc, DESC_PATTERN, "Rejection Reason Description");
//        this.rejectionReasonDesc = rejectionReasonDesc == null ? null : StringEscapeUtils.escapeHtml4(rejectionReasonDesc.trim());
//    }
//
//    public void setAssignedCheckers(String assignedCheckers) {
//        validateAndSanitize(assignedCheckers, REF_PATTERN, "Assigned Checkers");
//        this.assignedCheckers = assignedCheckers == null ? null : StringEscapeUtils.escapeHtml4(assignedCheckers.trim());
//    }
//
//    public void setUserEmail(String userEmail) {
//        validateAndSanitize(userEmail, EMAIL_PATTERN, "User Email");
//        this.userEmail = userEmail == null ? null : StringEscapeUtils.escapeHtml4(userEmail.trim());
//    }
//
//    public void setAbNo(String abNo) {
//        validateAndSanitize(abNo, REF_PATTERN, "AB Number");
//        this.abNo = abNo == null ? null : StringEscapeUtils.escapeHtml4(abNo.trim());
//    }
//
//    public void setPolicyNumber(String policyNumber) {
//        validateAndSanitize(policyNumber, REF_PATTERN, "Policy Number");
//        this.policyNumber = policyNumber == null ? null : StringEscapeUtils.escapeHtml4(policyNumber.trim());
//    }
//
//    public void setClaimNumber(String claimNumber) {
//        validateAndSanitize(claimNumber, REF_PATTERN, "Claim Number");
//        this.claimNumber = claimNumber == null ? null : StringEscapeUtils.escapeHtml4(claimNumber.trim());
//    }
//
//    public void setClaimStatus(String claimStatus) {
//        validateAndSanitize(claimStatus, NAME_PATTERN, "Claim Status");
//        this.claimStatus = claimStatus == null ? null : StringEscapeUtils.escapeHtml4(claimStatus.trim());
//    }
//
//    public void setClientName(String clientName) {
//        validateAndSanitize(clientName, NAME_PATTERN, "Client Name");
//        this.clientName = clientName == null ? null : StringEscapeUtils.escapeHtml4(clientName.trim());
//    }
//
//    public void setTransType(String transType) {
//        validateAndSanitize(transType, NAME_PATTERN, "Transaction Type");
//        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
//    }




}
