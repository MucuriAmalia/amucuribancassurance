package com.brokersystems.brokerapp.users.dto.netreveal;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonDetails {

    private static final Logger logger = LoggerFactory.getLogger(CommonDetails.class);

    private String customerType;
    private String customerId;
    private String identificationType;
    private String controlId;
    private String sbu;
    private String teller;
    private String branch;
    private String orgUnit;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        //logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s", fieldName)
            );
        }
    }

    public void setCustomerType(String customerType) {
        validateAndSanitize(customerType, REF_PATTERN, "Customer Type");
        this.customerType = customerType == null ? null : StringEscapeUtils.escapeHtml4(customerType.trim());
    }

    public void setCustomerId(String customerId) {
        validateAndSanitize(customerId, ID_NUMBER_PATTERN, "Customer ID");
        this.customerId = customerId == null ? null : StringEscapeUtils.escapeHtml4(customerId.trim());
    }

    public void setIdentificationType(String identificationType) {
        validateAndSanitize(identificationType, REF_PATTERN, "Identification Type");
        this.identificationType = identificationType == null ? null : StringEscapeUtils.escapeHtml4(identificationType.trim());
    }

    public void setControlId(String controlId) {
        validateAndSanitize(controlId, REF_PATTERN, "Control ID");
        this.controlId = controlId == null ? null : StringEscapeUtils.escapeHtml4(controlId.trim());
    }

    public void setSbu(String sbu) {
        validateAndSanitize(sbu, REF_PATTERN, "SBU");
        this.sbu = sbu == null ? null : StringEscapeUtils.escapeHtml4(sbu.trim());
    }

    public void setTeller(String teller) {
        validateAndSanitize(teller, CREATED_BY_PATTERN, "Teller");
        this.teller = teller == null ? null : StringEscapeUtils.escapeHtml4(teller.trim());
    }

    public void setBranch(String branch) {
        validateAndSanitize(branch, NAME_PATTERN, "Branch");
        this.branch = branch == null ? null : StringEscapeUtils.escapeHtml4(branch.trim());
    }

    public void setOrgUnit(String orgUnit) {
        validateAndSanitize(orgUnit, REF_PATTERN, "Organization Unit");
        this.orgUnit = orgUnit == null ? null : StringEscapeUtils.escapeHtml4(orgUnit.trim());
    }
}
