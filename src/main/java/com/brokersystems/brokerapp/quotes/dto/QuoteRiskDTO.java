package com.brokersystems.brokerapp.quotes.dto;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Getter
public class QuoteRiskDTO {

    private static final Logger logger = LoggerFactory.getLogger(QuoteRiskDTO.class);

    private String riskShtDesc;
    private String riskDesc;
    @Setter
    private Date wefDate;
    @Setter
    private Date wetDate;
    @Setter
    private Long subId;
    private String subDesc;
    @Setter
    private Long covId;
    private String covName;
    @Setter
    private BigDecimal sumInsured;
    @Setter
    private BigDecimal premium;
    private String quotStatus;
    @Setter
    private Long riskId;
    @Setter
    private BigDecimal butchargePrem;
    private String prorata;
    @Setter
    private Long binderId;
    @Setter
    private Long tenId;
    private String fname;
    private String otherNames;
    @Setter
    private BigDecimal commRate;
    private String clientType;
    private String idNo;

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

    public void setRiskShtDesc(String riskShtDesc) {
        validateAndSanitize(riskShtDesc, DESC_PATTERN, "Risk Short Description");
        this.riskShtDesc = riskShtDesc == null ? null : StringEscapeUtils.escapeHtml4(riskShtDesc.trim());
    }

    public void setRiskDesc(String riskDesc) {
        validateAndSanitize(riskDesc, DESC_PATTERN, "Risk Description");
        this.riskDesc = riskDesc == null ? null : StringEscapeUtils.escapeHtml4(riskDesc.trim());
    }

    public void setSubDesc(String subDesc) {
        validateAndSanitize(subDesc, DESC_PATTERN, "Sub Description");
        this.subDesc = subDesc == null ? null : StringEscapeUtils.escapeHtml4(subDesc.trim());
    }

    public void setCovName(String covName) {
        validateAndSanitize(covName, NAME_PATTERN, "Cover Name");
        this.covName = covName == null ? null : StringEscapeUtils.escapeHtml4(covName.trim());
    }

    public void setQuotStatus(String quotStatus) {
        validateAndSanitize(quotStatus, REF_PATTERN, "Quote Status");
        this.quotStatus = quotStatus == null ? null : StringEscapeUtils.escapeHtml4(quotStatus.trim());
    }

    public void setFname(String fname) {
        validateAndSanitize(fname, NAME_PATTERN, "First Name");
        this.fname = fname == null ? null : StringEscapeUtils.escapeHtml4(fname.trim());
    }

    public void setOtherNames(String otherNames) {
        validateAndSanitize(otherNames, NAME_PATTERN, "Other Names");
        this.otherNames = otherNames == null ? null : StringEscapeUtils.escapeHtml4(otherNames.trim());
    }

    public void setClientType(String clientType) {
        validateAndSanitize(clientType, REF_PATTERN, "Client Type");
        this.clientType = clientType == null ? null : StringEscapeUtils.escapeHtml4(clientType.trim());
    }

    public void setIdNo(String idNo) {
        validateAndSanitize(idNo, REF_PATTERN, "ID Number");
        this.idNo = idNo == null ? null : StringEscapeUtils.escapeHtml4(idNo.trim());
    }

    public void setProrata(String prorata) {
        validateAndSanitize(prorata, REF_PATTERN, "Prorata");
        this.prorata = prorata == null ? null : StringEscapeUtils.escapeHtml4(prorata.trim());
    }
}
