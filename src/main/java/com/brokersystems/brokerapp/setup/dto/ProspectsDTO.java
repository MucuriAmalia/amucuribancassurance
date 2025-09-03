package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProspectsDTO {

    private static final Logger logger = LoggerFactory.getLogger(ProspectsDTO.class);

    private Long tenId;

    private String prospShtDesc;
    private String fname;
    private String otherNames;

    @JsonIgnore
    private String phoneNo;

    private String clientType;
    private Long clientTypeId;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dob;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date polStartDate;

    @JsonIgnore
    private String dateofBirth;
    private String comment;
    private String status;
    private String category;
    private Long acctId;
    private String acctName;
    private Long branchId;
    private String branchName;

    @JsonIgnore
    private String emailAddress;

    private Long prefixId;
    private String prefix;
    private String gender;
    private Long prodId;
    private Long quotationId;
    private Long clientTitle;
    private String username;

    @JsonIgnore
    private String pinNo;

    @JsonIgnore
    private String idNo;

    @JsonIgnore
    private String address;

    @JsonIgnore
    private String officeTel;

    @JsonIgnore
    private String passportNo;

    @JsonIgnore
    private String smsNumber;
    private Long clientSector;
    private Long country;
    private Long occupation;
    private Long town;

    @JsonIgnore
    private String phoneNumber;

    @JsonIgnore
    private String residentStatus;
    private Long segmentId;
    private String segmentName;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s: '%s' does not match pattern %s", fieldName, trimmedInput, pattern)
            );
        }
    }

    public void setProspShtDesc(String prospShtDesc) {
        validateAndSanitize(prospShtDesc, DESC_PATTERN, "Prospect Short Description");
        this.prospShtDesc = prospShtDesc == null ? null : StringEscapeUtils.escapeHtml4(prospShtDesc.trim());
    }

    public void setFname(String fname) {
        validateAndSanitize(fname, NAME_PATTERN, "First Name");
        this.fname = fname == null ? null : StringEscapeUtils.escapeHtml4(fname.trim());
    }

    public void setOtherNames(String otherNames) {
        validateAndSanitize(otherNames, NAME_PATTERN, "Other Names");
        this.otherNames = otherNames == null ? null : StringEscapeUtils.escapeHtml4(otherNames.trim());
    }

    public void setPhoneNo(String phoneNo) {
        validateAndSanitize(phoneNo, PHONE_PATTERN, "Phone Number");
        this.phoneNo = phoneNo == null ? null : StringEscapeUtils.escapeHtml4(phoneNo.trim());
    }

    public void setClientType(String clientType) {
        validateAndSanitize(clientType, REF_PATTERN, "Client Type");
        this.clientType = clientType == null ? null : StringEscapeUtils.escapeHtml4(clientType.trim());
    }

    public void setDateofBirth(String dateofBirth) {
        validateAndSanitize(dateofBirth, DESC_PATTERN, "Date of Birth");
        this.dateofBirth = dateofBirth == null ? null : StringEscapeUtils.escapeHtml4(dateofBirth.trim());
    }

    public void setComment(String comment) {
        validateAndSanitize(comment, DESC_PATTERN, "Comment");
        this.comment = comment == null ? null : StringEscapeUtils.escapeHtml4(comment.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, REF_PATTERN, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }

    public void setCategory(String category) {
        validateAndSanitize(category, REF_PATTERN, "Category");
        this.category = category == null ? null : StringEscapeUtils.escapeHtml4(category.trim());
    }

    public void setAcctName(String acctName) {
        validateAndSanitize(acctName, NAME_PATTERN, "Account Name");
        this.acctName = acctName == null ? null : StringEscapeUtils.escapeHtml4(acctName.trim());
    }

    public void setBranchName(String branchName) {
        validateAndSanitize(branchName, NAME_PATTERN, "Branch Name");
        this.branchName = branchName == null ? null : StringEscapeUtils.escapeHtml4(branchName.trim());
    }

    public void setEmailAddress(String emailAddress) {
        validateAndSanitize(emailAddress, EMAIL_PATTERN, "Email Address");
        this.emailAddress = emailAddress == null ? null : StringEscapeUtils.escapeHtml4(emailAddress.trim());
    }

    public void setPrefix(String prefix) {
        validateAndSanitize(prefix, DESC_PATTERN, "Prefix");
        this.prefix = prefix == null ? null : StringEscapeUtils.escapeHtml4(prefix.trim());
    }

    public void setGender(String gender) {
        validateAndSanitize(gender, REF_PATTERN, "Gender");
        this.gender = gender == null ? null : StringEscapeUtils.escapeHtml4(gender.trim());
    }

    public void setUsername(String username) {
        validateAndSanitize(username, CREATED_BY_PATTERN, "Username");
        this.username = username == null ? null : StringEscapeUtils.escapeHtml4(username.trim());
    }

    public void setPinNo(String pinNo) {
        validateAndSanitize(pinNo, REF_PATTERN, "PIN Number");
        this.pinNo = pinNo == null ? null : StringEscapeUtils.escapeHtml4(pinNo.trim());
    }

    public void setIdNo(String idNo) {
        validateAndSanitize(idNo, ID_NUMBER_PATTERN, "ID Number");
        this.idNo = idNo == null ? null : StringEscapeUtils.escapeHtml4(idNo.trim());
    }

    public void setAddress(String address) {
        validateAndSanitize(address, ADDRESS_PATTERN, "Address");
        this.address = address == null ? null : StringEscapeUtils.escapeHtml4(address.trim());
    }

    public void setOfficeTel(String officeTel) {
        validateAndSanitize(officeTel, PHONE_PATTERN, "Office Telephone");
        this.officeTel = officeTel == null ? null : StringEscapeUtils.escapeHtml4(officeTel.trim());
    }

    public void setPassportNo(String passportNo) {
        validateAndSanitize(passportNo, ID_NUMBER_PATTERN, "Passport Number");
        this.passportNo = passportNo == null ? null : StringEscapeUtils.escapeHtml4(passportNo.trim());
    }

    public void setSmsNumber(String smsNumber) {
        validateAndSanitize(smsNumber, MOBILE_PATTERN, "SMS Number");
        this.smsNumber = smsNumber == null ? null : StringEscapeUtils.escapeHtml4(smsNumber.trim());
    }

    public void setPhoneNumber(String phoneNumber) {
        validateAndSanitize(phoneNumber, PHONE_PATTERN, "Phone Number");
        this.phoneNumber = phoneNumber == null ? null : StringEscapeUtils.escapeHtml4(phoneNumber.trim());
    }

    public void setResidentStatus(String residentStatus) {
        validateAndSanitize(residentStatus, REF_PATTERN, "Resident Status");
        this.residentStatus = residentStatus == null ? null : StringEscapeUtils.escapeHtml4(residentStatus.trim());
    }

    public void setSegmentName(String segmentName) {
        validateAndSanitize(segmentName, NAME_PATTERN, "Segment Name");
        this.segmentName = segmentName == null ? null : StringEscapeUtils.escapeHtml4(segmentName.trim());
    }



}
