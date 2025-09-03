package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;
import static net.sf.jasperreports.types.date.FixedDate.DATE_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    private static final Logger logger = LoggerFactory.getLogger(ClientDTO.class);

    private String tenantNumber;
    private String clientName;
    private String fname;
    private String otherNames;
    @Temporal(TemporalType.DATE)
    private Date dob;
    private String dateOfBirth;
    private String gender;

    //@JsonIgnore
    private String idNo;

    //@JsonIgnore
    private String pinNo;

    private String emailAddress;

    //@JsonIgnore
    private String phoneNo;

    private Long clientTypeId;
    private String clientType;
    private String clientTypeDesc;
    private String status;
    private Date dateCreated;
    private Long tenId;
    private String createdBy;
    private String authBy;
    private String username;

    //@JsonIgnore
    private String passportNo;

    private Long obId;
    private String obName;

    //@JsonIgnore
    private String comment;

    private Date dateterminated;
    private Date dateregistered;
    private String address;
    private String clientRef;
    private String authStatus;
    private String officeTel;
    private Long occCode;
    private String occName;
    private Long sectCode;
    private String sectName;
    private Long titleId;
    private String titleName;
    private Long phonePrefixId;
    private String phonePrefixName;
    private Long smsPrefixId;
    private String smsPrefixName;
    private Long pcode;
    private String postalName;
    private Long ctCode;
    private String ctName;

    //@JsonIgnore
    private transient MultipartFile file;
    private String photoUrl;

    //@JsonIgnore
    private String smsNumber;

    private Long couCode;
    private String couName;

    @JsonIgnore
    private String hashCode;

    private String residentStatus;
    private Boolean receiveMarketingMaterial;
    private String preferredModeOfCommunication;

    //@JsonIgnore
    private String clientCIF;


    private Long segmentId;
    private String segmentName;
    private String pepType;

    //@JsonIgnore
    private String screeningStatus;

    private String screeningDate;
    private String clientSegment;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        //logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s: '%s' does not match pattern %s", fieldName, trimmedInput, pattern)
            );
        }
    }


    public void setTenantNumber(String tenantNumber) {
        validateAndSanitize(tenantNumber, REF_PATTERN, "Tenant Number");
        this.tenantNumber = tenantNumber == null ? null : StringEscapeUtils.escapeHtml4(tenantNumber.trim());
    }

    public void setClientName(String clientName) {
        validateAndSanitize(clientName, NAME_PATTERN, "Client Name");
        this.clientName = clientName == null ? null : StringEscapeUtils.escapeHtml4(clientName.trim());
    }

    public void setFname(String fname) {
        validateAndSanitize(fname, NAME_PATTERN, "First Name");
        this.fname = fname == null ? null : StringEscapeUtils.escapeHtml4(fname.trim());
    }

    public void setOtherNames(String otherNames) {
        validateAndSanitize(otherNames, NAME_PATTERN, "Other Names");
        this.otherNames = otherNames == null ? null : StringEscapeUtils.escapeHtml4(otherNames.trim());
    }

    public void setDateOfBirth(String dateOfBirth) {
        validateAndSanitize(dateOfBirth, DATE_PATTERN, "Date of Birth");
        this.dateOfBirth = dateOfBirth == null ? null : StringEscapeUtils.escapeHtml4(dateOfBirth.trim());
    }

    public void setGender(String gender) {
        validateAndSanitize(gender, NAME_PATTERN, "Gender");
        this.gender = gender == null ? null : StringEscapeUtils.escapeHtml4(gender.trim());
    }

    public void setIdNo(String idNo) {
        validateAndSanitize(idNo, ID_NUMBER_PATTERN, "ID Number");
        this.idNo = idNo == null ? null : StringEscapeUtils.escapeHtml4(idNo.trim());
    }

    public void setPinNo(String pinNo) {
        validateAndSanitize(pinNo, ID_NUMBER_PATTERN, "PIN Number");
        this.pinNo = pinNo == null ? null : StringEscapeUtils.escapeHtml4(pinNo.trim());
    }

    public void setEmailAddress(String emailAddress) {
        validateAndSanitize(emailAddress, EMAIL_PATTERN, "Email Address");
        this.emailAddress = emailAddress == null ? null : StringEscapeUtils.escapeHtml4(emailAddress.trim());
    }

    public void setPhoneNo(String phoneNo) {
        validateAndSanitize(phoneNo, PHONE_PATTERN, "Phone Number");
        this.phoneNo = phoneNo == null ? null : StringEscapeUtils.escapeHtml4(phoneNo.trim());
    }

    public void setClientType(String clientType) {
        validateAndSanitize(clientType, NAME_PATTERN, "Client Type");
        this.clientType = clientType == null ? null : StringEscapeUtils.escapeHtml4(clientType.trim());
    }

    public void setClientTypeDesc(String clientTypeDesc) {
        validateAndSanitize(clientTypeDesc, DESC_PATTERN, "Client Type Description");
        this.clientTypeDesc = clientTypeDesc == null ? null : StringEscapeUtils.escapeHtml4(clientTypeDesc.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, NAME_PATTERN, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }

    public void setCreatedBy(String createdBy) {
        validateAndSanitize(createdBy, NAME_PATTERN, "Created By");
        this.createdBy = createdBy == null ? null : StringEscapeUtils.escapeHtml4(createdBy.trim());
    }

    public void setAuthBy(String authBy) {
        validateAndSanitize(authBy, NAME_PATTERN, "Authorized By");
        this.authBy = authBy == null ? null : StringEscapeUtils.escapeHtml4(authBy.trim());
    }

    public void setUsername(String username) {
        validateAndSanitize(username, NAME_PATTERN, "Username");
        this.username = username == null ? null : StringEscapeUtils.escapeHtml4(username.trim());
    }

    public void setPassportNo(String passportNo) {
        validateAndSanitize(passportNo, ID_NUMBER_PATTERN, "Passport Number");
        this.passportNo = passportNo == null ? null : StringEscapeUtils.escapeHtml4(passportNo.trim());
    }

    public void setObName(String obName) {
        validateAndSanitize(obName, NAME_PATTERN, "Organization Branch Name");
        this.obName = obName == null ? null : StringEscapeUtils.escapeHtml4(obName.trim());
    }

    public void setComment(String comment) {
        validateAndSanitize(comment, DESC_PATTERN, "Comment");
        this.comment = comment == null ? null : StringEscapeUtils.escapeHtml4(comment.trim());
    }

    public void setAddress(String address) {
        validateAndSanitize(address, DESC_PATTERN, "Address");
        this.address = address == null ? null : StringEscapeUtils.escapeHtml4(address.trim());
    }

    public void setClientRef(String clientRef) {
        validateAndSanitize(clientRef, REF_PATTERN, "Client Reference");
        this.clientRef = clientRef == null ? null : StringEscapeUtils.escapeHtml4(clientRef.trim());
    }

    public void setAuthStatus(String authStatus) {
        validateAndSanitize(authStatus, NAME_PATTERN, "Authorization Status");
        this.authStatus = authStatus == null ? null : StringEscapeUtils.escapeHtml4(authStatus.trim());
    }

    public void setOfficeTel(String officeTel) {
        validateAndSanitize(officeTel, PHONE_PATTERN, "Office Telephone");
        this.officeTel = officeTel == null ? null : StringEscapeUtils.escapeHtml4(officeTel.trim());
    }

    public void setOccName(String occName) {
        validateAndSanitize(occName, NAME_PATTERN, "Occupation Name");
        this.occName = occName == null ? null : StringEscapeUtils.escapeHtml4(occName.trim());
    }

    public void setSectName(String sectName) {
        validateAndSanitize(sectName, NAME_PATTERN, "Sector Name");
        this.sectName = sectName == null ? null : StringEscapeUtils.escapeHtml4(sectName.trim());
    }

    public void setTitleName(String titleName) {
        validateAndSanitize(titleName, NAME_PATTERN, "Title Name");
        this.titleName = titleName == null ? null : StringEscapeUtils.escapeHtml4(titleName.trim());
    }

    public void setPhonePrefixName(String phonePrefixName) {
        validateAndSanitize(phonePrefixName, NAME_PATTERN, "Phone Prefix Name");
        this.phonePrefixName = phonePrefixName == null ? null : StringEscapeUtils.escapeHtml4(phonePrefixName.trim());
    }

    public void setSmsPrefixName(String smsPrefixName) {
        validateAndSanitize(smsPrefixName, NAME_PATTERN, "SMS Prefix Name");
        this.smsPrefixName = smsPrefixName == null ? null : StringEscapeUtils.escapeHtml4(smsPrefixName.trim());
    }

    public void setPostalName(String postalName) {
        validateAndSanitize(postalName, NAME_PATTERN, "Postal Name");
        this.postalName = postalName == null ? null : StringEscapeUtils.escapeHtml4(postalName.trim());
    }

    public void setCtName(String ctName) {
        validateAndSanitize(ctName, NAME_PATTERN, "Country Name");
        this.ctName = ctName == null ? null : StringEscapeUtils.escapeHtml4(ctName.trim());
    }

    public void setPhotoUrl(String photoUrl) {
        validateAndSanitize(photoUrl, REF_PATTERN, "Photo URL");
        this.photoUrl = photoUrl == null ? null : StringEscapeUtils.escapeHtml4(photoUrl.trim());
    }

    public void setSmsNumber(String smsNumber) {
        validateAndSanitize(smsNumber, PHONE_PATTERN, "SMS Number");
        this.smsNumber = smsNumber == null ? null : StringEscapeUtils.escapeHtml4(smsNumber.trim());
    }

    public void setCouName(String couName) {
        validateAndSanitize(couName, NAME_PATTERN, "Country Name");
        this.couName = couName == null ? null : StringEscapeUtils.escapeHtml4(couName.trim());
    }

    public void setHashCode(String hashCode) {
        validateAndSanitize(hashCode, REF_PATTERN, "Hash Code");
        this.hashCode = hashCode == null ? null : StringEscapeUtils.escapeHtml4(hashCode.trim());
    }

    public void setResidentStatus(String residentStatus) {
        validateAndSanitize(residentStatus, NAME_PATTERN, "Resident Status");
        this.residentStatus = residentStatus == null ? null : StringEscapeUtils.escapeHtml4(residentStatus.trim());
    }

    public void setPreferredModeOfCommunication(String preferredModeOfCommunication) {
        validateAndSanitize(preferredModeOfCommunication, NAME_PATTERN, "Preferred Mode of Communication");
        this.preferredModeOfCommunication = preferredModeOfCommunication == null ? null : StringEscapeUtils.escapeHtml4(preferredModeOfCommunication.trim());
    }

    public void setClientCIF(String clientCIF) {
        validateAndSanitize(clientCIF, REF_PATTERN, "Client CIF");
        this.clientCIF = clientCIF == null ? null : StringEscapeUtils.escapeHtml4(clientCIF.trim());
    }

    public void setSegmentName(String segmentName) {
        validateAndSanitize(segmentName, NAME_PATTERN, "Segment Name");
        this.segmentName = segmentName == null ? null : StringEscapeUtils.escapeHtml4(segmentName.trim());
    }

    public void setPepType(String pepType) {
        validateAndSanitize(pepType, NAME_PATTERN, "PEP Type");
        this.pepType = pepType == null ? null : StringEscapeUtils.escapeHtml4(pepType.trim());
    }

    public void setScreeningStatus(String screeningStatus) {
        validateAndSanitize(screeningStatus, NAME_PATTERN, "Screening Status");
        this.screeningStatus = screeningStatus == null ? null : StringEscapeUtils.escapeHtml4(screeningStatus.trim());
    }

    public void setScreeningDate(String screeningDate) {
        validateAndSanitize(screeningDate, DATE_PATTERN, "Screening Date");
        this.screeningDate = screeningDate == null ? null : StringEscapeUtils.escapeHtml4(screeningDate.trim());
    }

    public void setClientSegment(String clientSegment) {
        validateAndSanitize(clientSegment, NAME_PATTERN, "Client Segment");
        this.clientSegment = clientSegment == null ? null : StringEscapeUtils.escapeHtml4(clientSegment.trim());
    }

}
