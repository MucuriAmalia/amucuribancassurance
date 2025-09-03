package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;
import static net.sf.jasperreports.types.date.FixedDate.DATE_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskTransDTO {

    private static final Logger logger = LoggerFactory.getLogger(RiskTransDTO.class);

    private Long riskId;
    private String riskShtDesc;
    private String riskDesc;
    private Date wefDate;
    private Date wetDate;
    private Long subId;
    private Long prodId;
    private String subDesc;
    private Long covId;
    private String covName;
    private BigDecimal sumInsured;
    private BigDecimal premium;
    private BigDecimal commRate;
    private BigDecimal butchargePrem;
    private BigDecimal installAmount;
    private Long installmentNo;
    private String installmentPerc;
    private Long riskIdentifier;
    private String transType;
    private String authStatus;
    private String autogenCert;
    private Long insuredId;
    private Long binderDetId;
    private String fname;
    private String othernames;
    private String prorata;
    private Long tenId;
    private Long polBindId;
    private String startDate;
    private String endDate;
    private Integer age;
    private String computeType;
    private String idNo;

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

    public void setInstallmentPerc(String installmentPerc) {
        validateAndSanitize(installmentPerc, REF_PATTERN, "Installment Percentage");
        this.installmentPerc = installmentPerc == null ? null : StringEscapeUtils.escapeHtml4(installmentPerc.trim());
    }

    public void setTransType(String transType) {
        validateAndSanitize(transType, NAME_PATTERN, "Transaction Type");
        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
    }

    public void setAuthStatus(String authStatus) {
        validateAndSanitize(authStatus, NAME_PATTERN, "Authorization Status");
        this.authStatus = authStatus == null ? null : StringEscapeUtils.escapeHtml4(authStatus.trim());
    }

    public void setAutogenCert(String autogenCert) {
        validateAndSanitize(autogenCert, NAME_PATTERN, "Auto-generate Certificate");
        this.autogenCert = autogenCert == null ? null : StringEscapeUtils.escapeHtml4(autogenCert.trim());
    }

    public void setFname(String fname) {
        validateAndSanitize(fname, NAME_PATTERN, "First Name");
        this.fname = fname == null ? null : StringEscapeUtils.escapeHtml4(fname.trim());
    }

    public void setOthernames(String othernames) {
        validateAndSanitize(othernames, NAME_PATTERN, "Other Names");
        this.othernames = othernames == null ? null : StringEscapeUtils.escapeHtml4(othernames.trim());
    }

    public void setProrata(String prorata) {
        validateAndSanitize(prorata, NAME_PATTERN, "Prorata");
        this.prorata = prorata == null ? null : StringEscapeUtils.escapeHtml4(prorata.trim());
    }

    public void setStartDate(String startDate) {
        //validateAndSanitize(startDate, "dd/MM/yyyy", "Start Date");
        this.startDate = startDate == null ? null : StringEscapeUtils.escapeHtml4(startDate.trim());
    }

    public void setEndDate(String endDate) {
       // validateAndSanitize(endDate, "dd/MM/yyyy", "End Date");
        this.endDate = endDate == null ? null : StringEscapeUtils.escapeHtml4(endDate.trim());
    }

    public void setComputeType(String computeType) {
        validateAndSanitize(computeType, NAME_PATTERN, "Compute Type");
        this.computeType = computeType == null ? null : StringEscapeUtils.escapeHtml4(computeType.trim());
    }

    public void setIdNo(String idNo) {
        validateAndSanitize(idNo, ID_NUMBER_PATTERN, "ID Number");
        this.idNo = idNo == null ? null : StringEscapeUtils.escapeHtml4(idNo.trim());
    }

}
