package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BinderDTO {

    private static final Logger logger = LoggerFactory.getLogger(BinderDTO.class);

    private Long binId;
    private String binName;
    private String binPolNo;
    private Long proCode;
    private String proDesc;
    private String ageApplicable;
    private Long acctId;
    private String name;
    private boolean motorProduct;
    private boolean active;
    private String binShtDesc;
    private String binStatus;
    private boolean binDefault;
    private String binRemarks;
    private String binType;
    private Long currencyCode;
    private String currencyName;
    private BigDecimal minPrem;
    private String fundBinder;
    private Integer minTerm;
    private Integer maxTerm;
    private String premiumAgeType;
    private BigDecimal maxExposure;
    private String medicalCoverType;

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

    public void setBinName(String binName) {
        validateAndSanitize(binName, NAME_PATTERN, "Binder Name");
        this.binName = binName == null ? null : StringEscapeUtils.escapeHtml4(binName.trim());
    }

    public void setBinPolNo(String binPolNo) {
        validateAndSanitize(binPolNo, REF_PATTERN, "Binder Policy Number");
        this.binPolNo = binPolNo == null ? null : StringEscapeUtils.escapeHtml4(binPolNo.trim());
    }

    public void setProDesc(String proDesc) {
        validateAndSanitize(proDesc, DESC_PATTERN, "Product Description");
        this.proDesc = proDesc == null ? null : StringEscapeUtils.escapeHtml4(proDesc.trim());
    }

    public void setAgeApplicable(String ageApplicable) {
        validateAndSanitize(ageApplicable, NAME_PATTERN, "Age Applicable");
        this.ageApplicable = ageApplicable == null ? null : StringEscapeUtils.escapeHtml4(ageApplicable.trim());
    }

    public void setName(String name) {
        validateAndSanitize(name, NAME_PATTERN, "Name");
        this.name = name == null ? null : StringEscapeUtils.escapeHtml4(name.trim());
    }

    public void setBinShtDesc(String binShtDesc) {
        validateAndSanitize(binShtDesc, DESC_PATTERN, "Binder Short Description");
        this.binShtDesc = binShtDesc == null ? null : StringEscapeUtils.escapeHtml4(binShtDesc.trim());
    }

    public void setBinStatus(String binStatus) {
        validateAndSanitize(binStatus, NAME_PATTERN, "Binder Status");
        this.binStatus = binStatus == null ? null : StringEscapeUtils.escapeHtml4(binStatus.trim());
    }

    public void setBinRemarks(String binRemarks) {
        validateAndSanitize(binRemarks, DESC_PATTERN, "Binder Remarks");
        this.binRemarks = binRemarks == null ? null : StringEscapeUtils.escapeHtml4(binRemarks.trim());
    }

    public void setBinType(String binType) {
        validateAndSanitize(binType, NAME_PATTERN, "Binder Type");
        this.binType = binType == null ? null : StringEscapeUtils.escapeHtml4(binType.trim());
    }

    public void setCurrencyName(String currencyName) {
        validateAndSanitize(currencyName, NAME_PATTERN, "Currency Name");
        this.currencyName = currencyName == null ? null : StringEscapeUtils.escapeHtml4(currencyName.trim());
    }

    public void setFundBinder(String fundBinder) {
        validateAndSanitize(fundBinder, NAME_PATTERN, "Fund Binder");
        this.fundBinder = fundBinder == null ? null : StringEscapeUtils.escapeHtml4(fundBinder.trim());
    }

    public void setPremiumAgeType(String premiumAgeType) {
        validateAndSanitize(premiumAgeType, NAME_PATTERN, "Premium Age Type");
        this.premiumAgeType = premiumAgeType == null ? null : StringEscapeUtils.escapeHtml4(premiumAgeType.trim());
    }

    public void setMedicalCoverType(String medicalCoverType) {
        validateAndSanitize(medicalCoverType, NAME_PATTERN, "Medical Cover Type");
        this.medicalCoverType = medicalCoverType == null ? null : StringEscapeUtils.escapeHtml4(medicalCoverType.trim());
    }


}
