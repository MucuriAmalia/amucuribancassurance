package com.brokersystems.brokerapp.trans.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static com.brokersystems.brokerapp.common.Constants.*;

public class ChequeTransDtlsDTO {

    private static final Logger logger = LoggerFactory.getLogger(ChequeTransDtlsDTO.class);

    private Long ctdNo;
    private BigDecimal transAmount;
    private String narrative;
    private Long glId;
    private String drcr;
    private Long branchCode;
    private String glAcctNo;
    private String glAcctName;
    private String branchName;

    public String getBranchName() {
        return branchName;
    }

    public String getGlAcctNo() {
        return glAcctNo;
    }

    public String getGlAcctName() {
        return glAcctName;
    }

    public Long getCtdNo() {
        return ctdNo;
    }

    public void setCtdNo(Long ctdNo) {
        this.ctdNo = ctdNo;
    }

    public BigDecimal getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(BigDecimal transAmount) {
        this.transAmount = transAmount;
    }

    public String getNarrative() {
        return narrative;
    }

    public Long getGlId() {
        return glId;
    }

    public void setGlId(Long glId) {
        this.glId = glId;
    }

    public String getDrcr() {
        return drcr;
    }

    public Long getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(Long branchCode) {
        this.branchCode = branchCode;
    }

    @Override
    public String toString() {
        return "ChequeTransDtlsDTO{" +
                "ctdNo=" + ctdNo +
                ", transAmount=" + transAmount +
                ", narrative='" + narrative + '\'' +
                ", glId=" + glId +
                ", drcr='" + drcr + '\'' +
                ", branchCode=" + branchCode +
                '}';
    }

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


    public void setNarrative(String narrative) {
        validateAndSanitize(narrative, DESC_PATTERN, "Narrative");
        this.narrative = narrative == null ? null : StringEscapeUtils.escapeHtml4(narrative.trim());
    }

    public void setDrcr(String drcr) {
        validateAndSanitize(drcr, REF_PATTERN, "Debit/Credit Indicator");
        this.drcr = drcr == null ? null : StringEscapeUtils.escapeHtml4(drcr.trim());
    }

    public void setGlAcctNo(String glAcctNo) {
        validateAndSanitize(glAcctNo, REF_PATTERN, "GL Account Number");
        this.glAcctNo = glAcctNo == null ? null : StringEscapeUtils.escapeHtml4(glAcctNo.trim());
    }

    public void setGlAcctName(String glAcctName) {
        validateAndSanitize(glAcctName, NAME_PATTERN, "GL Account Name");
        this.glAcctName = glAcctName == null ? null : StringEscapeUtils.escapeHtml4(glAcctName.trim());
    }

    public void setBranchName(String branchName) {
        validateAndSanitize(branchName, NAME_PATTERN, "Branch Name");
        this.branchName = branchName == null ? null : StringEscapeUtils.escapeHtml4(branchName.trim());
    }
}
