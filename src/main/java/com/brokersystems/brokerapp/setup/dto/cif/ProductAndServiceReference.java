package com.brokersystems.brokerapp.setup.dto.cif;

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
public class ProductAndServiceReference {

    private static final Logger logger = LoggerFactory.getLogger(ProductAndServiceReference.class);

    private String amlRiskRating;
    private boolean restrictPersonalDataUsage;
    private boolean restrictPersonalDataProfiling;
    private String communicationPreferenceToken;
    private String estatement;
    private String financialDetailsCountry;
    private String financialDetailsInternationalTransactionFlag;
    private String geoStatus;
    private String kycRiskLevelCode;
    private int kycRiskScore;
    private String kycStatus;
    private boolean manualKYCFlag;
    private String marketingConsentIndicator;
    private String productProcessorId;
    private String productProcessorTypeCode;
    private boolean systemKYCFlag;
    private int totalAccounts;

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

    public void setAmlRiskRating(String amlRiskRating) {
        validateAndSanitize(amlRiskRating, REF_PATTERN, "AML Risk Rating");
        this.amlRiskRating = amlRiskRating == null ? null : StringEscapeUtils.escapeHtml4(amlRiskRating.trim());
    }

    public void setCommunicationPreferenceToken(String communicationPreferenceToken) {
        validateAndSanitize(communicationPreferenceToken, REF_PATTERN, "Communication Preference Token");
        this.communicationPreferenceToken = communicationPreferenceToken == null ? null : StringEscapeUtils.escapeHtml4(communicationPreferenceToken.trim());
    }

    public void setEstatement(String estatement) {
        validateAndSanitize(estatement, REF_PATTERN, "Estatement");
        this.estatement = estatement == null ? null : StringEscapeUtils.escapeHtml4(estatement.trim());
    }

    public void setFinancialDetailsCountry(String financialDetailsCountry) {
        validateAndSanitize(financialDetailsCountry, NAME_PATTERN, "Financial Details Country");
        this.financialDetailsCountry = financialDetailsCountry == null ? null : StringEscapeUtils.escapeHtml4(financialDetailsCountry.trim());
    }

    public void setFinancialDetailsInternationalTransactionFlag(String financialDetailsInternationalTransactionFlag) {
        validateAndSanitize(financialDetailsInternationalTransactionFlag, REF_PATTERN, "Financial Details International Transaction Flag");
        this.financialDetailsInternationalTransactionFlag = financialDetailsInternationalTransactionFlag == null ? null : StringEscapeUtils.escapeHtml4(financialDetailsInternationalTransactionFlag.trim());
    }

    public void setGeoStatus(String geoStatus) {
        validateAndSanitize(geoStatus, REF_PATTERN, "Geo Status");
        this.geoStatus = geoStatus == null ? null : StringEscapeUtils.escapeHtml4(geoStatus.trim());
    }

    public void setKycRiskLevelCode(String kycRiskLevelCode) {
        validateAndSanitize(kycRiskLevelCode, REF_PATTERN, "KYC Risk Level Code");
        this.kycRiskLevelCode = kycRiskLevelCode == null ? null : StringEscapeUtils.escapeHtml4(kycRiskLevelCode.trim());
    }

    public void setKycStatus(String kycStatus) {
        validateAndSanitize(kycStatus, REF_PATTERN, "KYC Status");
        this.kycStatus = kycStatus == null ? null : StringEscapeUtils.escapeHtml4(kycStatus.trim());
    }

    public void setMarketingConsentIndicator(String marketingConsentIndicator) {
        validateAndSanitize(marketingConsentIndicator, REF_PATTERN, "Marketing Consent Indicator");
        this.marketingConsentIndicator = marketingConsentIndicator == null ? null : StringEscapeUtils.escapeHtml4(marketingConsentIndicator.trim());
    }

    public void setProductProcessorId(String productProcessorId) {
        validateAndSanitize(productProcessorId, REF_PATTERN, "Product Processor ID");
        this.productProcessorId = productProcessorId == null ? null : StringEscapeUtils.escapeHtml4(productProcessorId.trim());
    }

    public void setProductProcessorTypeCode(String productProcessorTypeCode) {
        validateAndSanitize(productProcessorTypeCode, REF_PATTERN, "Product Processor Type Code");
        this.productProcessorTypeCode = productProcessorTypeCode == null ? null : StringEscapeUtils.escapeHtml4(productProcessorTypeCode.trim());
    }
}
