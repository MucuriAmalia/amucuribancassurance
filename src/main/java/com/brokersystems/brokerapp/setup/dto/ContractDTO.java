package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractDTO {

    private static final Logger logger = LoggerFactory.getLogger(ContractDTO.class);

    private Long contractId;
    private String contractName;
    private String contractPolicyNo;
    private String currency;
    private String intermediary;
    private String intermediaryType;
    private String status;
    private String authStatus;

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

    public void setContractName(String contractName) {
        validateAndSanitize(contractName, NAME_PATTERN, "Contract Name");
        this.contractName = contractName == null ? null : StringEscapeUtils.escapeHtml4(contractName.trim());
    }

    public void setContractPolicyNo(String contractPolicyNo) {
        validateAndSanitize(contractPolicyNo, REF_PATTERN, "Contract Policy Number");
        this.contractPolicyNo = contractPolicyNo == null ? null : StringEscapeUtils.escapeHtml4(contractPolicyNo.trim());
    }

    public void setCurrency(String currency) {
        validateAndSanitize(currency, NAME_PATTERN, "Currency");
        this.currency = currency == null ? null : StringEscapeUtils.escapeHtml4(currency.trim());
    }

    public void setIntermediary(String intermediary) {
        validateAndSanitize(intermediary, NAME_PATTERN, "Intermediary");
        this.intermediary = intermediary == null ? null : StringEscapeUtils.escapeHtml4(intermediary.trim());
    }

    public void setIntermediaryType(String intermediaryType) {
        validateAndSanitize(intermediaryType, REF_PATTERN, "Intermediary Type");
        this.intermediaryType = intermediaryType == null ? null : StringEscapeUtils.escapeHtml4(intermediaryType.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, REF_PATTERN, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }

    public void setAuthStatus(String authStatus) {
        validateAndSanitize(authStatus, REF_PATTERN, "Authorization Status");
        this.authStatus = authStatus == null ? null : StringEscapeUtils.escapeHtml4(authStatus.trim());
    }

}
