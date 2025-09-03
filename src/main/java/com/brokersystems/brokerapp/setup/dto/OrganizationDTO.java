package com.brokersystems.brokerapp.setup.dto;


import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationDTO.class);

    private Long orgCode;
    private String orgDesc;
    private String orgFax;
    private String orgLogo;
    private String orgLogoContentType;
    private String orgMobile;
    private String orgName;

    @JsonIgnore
    private String orgPhone;
    private String orgShtDesc;
    private String orgWebsite;
    private Date dateIncorp;
    private String certNumber;
    private String addAddress;
    private String phyAddress;

    @JsonIgnore
    private String emailAddress;
    private Long couCode;
    private String couName;
    private Long curCode;
    private String curName;

    @JsonIgnore
    private String pinNo;
    private String createdBy;
    private Date createdDate;
    private String modifiedBy;
    private String formAction;
    private Date modifiedDate;

    @JsonIgnore
    private MultipartFile file;

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

    public void setOrgDesc(String orgDesc) {
        validateAndSanitize(orgDesc, DESC_PATTERN, "Organization Description");
        this.orgDesc = orgDesc == null ? null : StringEscapeUtils.escapeHtml4(orgDesc.trim());
    }

    public void setOrgFax(String orgFax) {
        validateAndSanitize(orgFax, PHONE_PATTERN, "Organization Fax");
        this.orgFax = orgFax == null ? null : StringEscapeUtils.escapeHtml4(orgFax.trim());
    }

    public void setOrgLogo(String orgLogo) {
        validateAndSanitize(orgLogo, FILENAME_PATTERN, "Organization Logo");
        this.orgLogo = orgLogo == null ? null : StringEscapeUtils.escapeHtml4(orgLogo.trim());
    }

    public void setOrgLogoContentType(String orgLogoContentType) {
        validateAndSanitize(orgLogoContentType, CONTENT_TYPE_PATTERN, "Organization Logo Content Type");
        this.orgLogoContentType = orgLogoContentType == null ? null : StringEscapeUtils.escapeHtml4(orgLogoContentType.trim());
    }

    public void setOrgMobile(String orgMobile) {
        validateAndSanitize(orgMobile, MOBILE_PATTERN, "Organization Mobile");
        this.orgMobile = orgMobile == null ? null : StringEscapeUtils.escapeHtml4(orgMobile.trim());
    }

    public void setOrgName(String orgName) {
        validateAndSanitize(orgName, NAME_PATTERN, "Organization Name");
        this.orgName = orgName == null ? null : StringEscapeUtils.escapeHtml4(orgName.trim());
    }

    public void setOrgPhone(String orgPhone) {
        validateAndSanitize(orgPhone, PHONE_PATTERN, "Organization Phone");
        this.orgPhone = orgPhone == null ? null : StringEscapeUtils.escapeHtml4(orgPhone.trim());
    }

    public void setOrgShtDesc(String orgShtDesc) {
        validateAndSanitize(orgShtDesc, DESC_PATTERN, "Organization Short Description");
        this.orgShtDesc = orgShtDesc == null ? null : StringEscapeUtils.escapeHtml4(orgShtDesc.trim());
    }

    public void setOrgWebsite(String orgWebsite) {
        validateAndSanitize(orgWebsite, DESC_PATTERN, "Organization Website");
        this.orgWebsite = orgWebsite == null ? null : StringEscapeUtils.escapeHtml4(orgWebsite.trim());
    }

    public void setCertNumber(String certNumber) {
        validateAndSanitize(certNumber, REF_PATTERN, "Certificate Number");
        this.certNumber = certNumber == null ? null : StringEscapeUtils.escapeHtml4(certNumber.trim());
    }

    public void setAddAddress(String addAddress) {
        validateAndSanitize(addAddress, ADDRESS_PATTERN, "Additional Address");
        this.addAddress = addAddress == null ? null : StringEscapeUtils.escapeHtml4(addAddress.trim());
    }

    public void setPhyAddress(String phyAddress) {
        validateAndSanitize(phyAddress, ADDRESS_PATTERN, "Physical Address");
        this.phyAddress = phyAddress == null ? null : StringEscapeUtils.escapeHtml4(phyAddress.trim());
    }

    public void setEmailAddress(String emailAddress) {
        validateAndSanitize(emailAddress, EMAIL_PATTERN, "Email Address");
        this.emailAddress = emailAddress == null ? null : StringEscapeUtils.escapeHtml4(emailAddress.trim());
    }

    public void setCouName(String couName) {
        validateAndSanitize(couName, NAME_PATTERN, "Country Name");
        this.couName = couName == null ? null : StringEscapeUtils.escapeHtml4(couName.trim());
    }

    public void setCurName(String curName) {
        validateAndSanitize(curName, NAME_PATTERN, "Currency Name");
        this.curName = curName == null ? null : StringEscapeUtils.escapeHtml4(curName.trim());
    }

    public void setPinNo(String pinNo) {
        validateAndSanitize(pinNo, REF_PATTERN, "PIN Number");
        this.pinNo = pinNo == null ? null : StringEscapeUtils.escapeHtml4(pinNo.trim());
    }

    public void setCreatedBy(String createdBy) {
        validateAndSanitize(createdBy, CREATED_BY_PATTERN, "Created By");
        this.createdBy = createdBy == null ? null : StringEscapeUtils.escapeHtml4(createdBy.trim());
    }

    public void setModifiedBy(String modifiedBy) {
        validateAndSanitize(modifiedBy, CREATED_BY_PATTERN, "Modified By");
        this.modifiedBy = modifiedBy == null ? null : StringEscapeUtils.escapeHtml4(modifiedBy.trim());
    }

    public void setFormAction(String formAction) {
        validateAndSanitize(formAction, DESC_PATTERN, "Form Action");
        this.formAction = formAction == null ? null : StringEscapeUtils.escapeHtml4(formAction.trim());
    }
}
