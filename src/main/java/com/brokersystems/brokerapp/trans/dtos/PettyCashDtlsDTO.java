package com.brokersystems.brokerapp.trans.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

public class PettyCashDtlsDTO {

    private static final Logger logger = LoggerFactory.getLogger(PettyCashDtlsDTO.class);

    private Long ptdNo;
    private BigDecimal transAmount;
    private String narrative;
    private String drcr;
    private Long bankAcctId;

    public Long getPtdNo() {
        return ptdNo;
    }

    public void setPtdNo(Long ptdNo) {
        this.ptdNo = ptdNo;
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


    public String getDrcr() {
        return drcr;
    }


    public Long getBankAcctId() {
        return bankAcctId;
    }

    public void setBankAcctId(Long bankAcctId) {
        this.bankAcctId = bankAcctId;
    }

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

    public void setNarrative(String narrative) {
        validateAndSanitize(narrative, DESC_PATTERN, "Narrative");
        this.narrative = narrative == null ? null : StringEscapeUtils.escapeHtml4(narrative.trim());
    }

    public void setDrcr(String drcr) {
        validateAndSanitize(drcr, REF_PATTERN, "Debit/Credit Indicator");
        this.drcr = drcr == null ? null : StringEscapeUtils.escapeHtml4(drcr.trim());
    }

}
