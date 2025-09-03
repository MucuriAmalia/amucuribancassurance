package com.brokersystems.brokerapp.setup.dto.cif;
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
public class DocumentDirectoryEntryInstanceReference {

    private static final Logger logger = LoggerFactory.getLogger(DocumentDirectoryEntryInstanceReference.class);

    private String customerIdentificationDocTypeCode;
    private String customerIdentificationNumber;
    private String identificationDocumentIssueDate;
    private String identificationDocumentIssuedBy;
    private String identificationDocumentIssuingPlace;
    private String initialDepositSegLimit;
    private String initialFundsSource;
    private String signature;
    private String transactionDetailsVolume;

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

    public void setCustomerIdentificationDocTypeCode(String customerIdentificationDocTypeCode) {
        validateAndSanitize(customerIdentificationDocTypeCode, REF_PATTERN, "Customer Identification Doc Type Code");
        this.customerIdentificationDocTypeCode = customerIdentificationDocTypeCode == null ? null : StringEscapeUtils.escapeHtml4(customerIdentificationDocTypeCode.trim());
    }

    public void setCustomerIdentificationNumber(String customerIdentificationNumber) {
        validateAndSanitize(customerIdentificationNumber, REF_PATTERN, "Customer Identification Number");
        this.customerIdentificationNumber = customerIdentificationNumber == null ? null : StringEscapeUtils.escapeHtml4(customerIdentificationNumber.trim());
    }

    public void setIdentificationDocumentIssueDate(String identificationDocumentIssueDate) {
        validateAndSanitize(identificationDocumentIssueDate, DESC_PATTERN, "Identification Document Issue Date");
        this.identificationDocumentIssueDate = identificationDocumentIssueDate == null ? null : StringEscapeUtils.escapeHtml4(identificationDocumentIssueDate.trim());
    }

    public void setIdentificationDocumentIssuedBy(String identificationDocumentIssuedBy) {
        validateAndSanitize(identificationDocumentIssuedBy, NAME_PATTERN, "Identification Document Issued By");
        this.identificationDocumentIssuedBy = identificationDocumentIssuedBy == null ? null : StringEscapeUtils.escapeHtml4(identificationDocumentIssuedBy.trim());
    }

    public void setIdentificationDocumentIssuingPlace(String identificationDocumentIssuingPlace) {
        validateAndSanitize(identificationDocumentIssuingPlace, DESC_PATTERN, "Identification Document Issuing Place");
        this.identificationDocumentIssuingPlace = identificationDocumentIssuingPlace == null ? null : StringEscapeUtils.escapeHtml4(identificationDocumentIssuingPlace.trim());
    }

    public void setInitialDepositSegLimit(String initialDepositSegLimit) {
        validateAndSanitize(initialDepositSegLimit, REF_PATTERN, "Initial Deposit Seg Limit");
        this.initialDepositSegLimit = initialDepositSegLimit == null ? null : StringEscapeUtils.escapeHtml4(initialDepositSegLimit.trim());
    }

    public void setInitialFundsSource(String initialFundsSource) {
        validateAndSanitize(initialFundsSource, REF_PATTERN, "Initial Funds Source");
        this.initialFundsSource = initialFundsSource == null ? null : StringEscapeUtils.escapeHtml4(initialFundsSource.trim());
    }

    public void setSignature(String signature) {
        validateAndSanitize(signature, REF_PATTERN, "Signature");
        this.signature = signature == null ? null : StringEscapeUtils.escapeHtml4(signature.trim());
    }

    public void setTransactionDetailsVolume(String transactionDetailsVolume) {
        validateAndSanitize(transactionDetailsVolume, REF_PATTERN, "Transaction Details Volume");
        this.transactionDetailsVolume = transactionDetailsVolume == null ? null : StringEscapeUtils.escapeHtml4(transactionDetailsVolume.trim());
    }

}
