package com.brokersystems.brokerapp.quotes.dto;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Getter
public class QuoteProductDTO {

    private static final Logger logger = LoggerFactory.getLogger(QuoteProductDTO.class);

    @Setter
    private Long quoteProductId;
    @Setter
    private Long quoteId;
    private String quoteNo;
    private String insuranceCompany;
    private String product;
    @Setter
    private Date wef;
    @Setter
    private Date wet;
    private String fname;
    private String otherNames;
    @Setter
    private Long tenId;
    private String clientType;
    private String currency;
    private String converted;
    @Setter
    private Long polId;
    private String policyNo;
    private String binder;
    private String bindType;
    @Setter
    private Long binderId;
    @Setter
    private Long productId;
    private String account;
    private String quotStatus;
    @Setter
    private Long acctId;
    @Setter
    private BigDecimal sumInsured;
    @Setter
    private BigDecimal premium;
    @Setter
    private BigDecimal commAmt;
    private String prospShtDesc;
    private String quotConverted;
    private String quotType;

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

    public void setQuoteNo(String quoteNo) {
        validateAndSanitize(quoteNo, REF_PATTERN, "Quote Number");
        this.quoteNo = quoteNo == null ? null : StringEscapeUtils.escapeHtml4(quoteNo.trim());
    }

    public void setInsuranceCompany(String insuranceCompany) {
        validateAndSanitize(insuranceCompany, NAME_PATTERN, "Insurance Company");
        this.insuranceCompany = insuranceCompany == null ? null : StringEscapeUtils.escapeHtml4(insuranceCompany.trim());
    }

    public void setProduct(String product) {
        validateAndSanitize(product, NAME_PATTERN, "Product");
        this.product = product == null ? null : StringEscapeUtils.escapeHtml4(product.trim());
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

    public void setCurrency(String currency) {
        validateAndSanitize(currency, NAME_PATTERN, "Currency");
        this.currency = currency == null ? null : StringEscapeUtils.escapeHtml4(currency.trim());
    }

    public void setConverted(String converted) {
        validateAndSanitize(converted, REF_PATTERN, "Converted");
        this.converted = converted == null ? null : StringEscapeUtils.escapeHtml4(converted.trim());
    }

    public void setPolicyNo(String policyNo) {
        validateAndSanitize(policyNo, REF_PATTERN, "Policy Number");
        this.policyNo = policyNo == null ? null : StringEscapeUtils.escapeHtml4(policyNo.trim());
    }

    public void setBinder(String binder) {
        validateAndSanitize(binder, NAME_PATTERN, "Binder");
        this.binder = binder == null ? null : StringEscapeUtils.escapeHtml4(binder.trim());
    }

    public void setBindType(String bindType) {
        validateAndSanitize(bindType, REF_PATTERN, "Bind Type");
        this.bindType = bindType == null ? null : StringEscapeUtils.escapeHtml4(bindType.trim());
    }

    public void setAccount(String account) {
        validateAndSanitize(account, NAME_PATTERN, "Account");
        this.account = account == null ? null : StringEscapeUtils.escapeHtml4(account.trim());
    }

    public void setQuotStatus(String quotStatus) {
        validateAndSanitize(quotStatus, REF_PATTERN, "Quote Status");
        this.quotStatus = quotStatus == null ? null : StringEscapeUtils.escapeHtml4(quotStatus.trim());
    }

    public void setProspShtDesc(String prospShtDesc) {
        validateAndSanitize(prospShtDesc, DESC_PATTERN, "Prospect Short Description");
        this.prospShtDesc = prospShtDesc == null ? null : StringEscapeUtils.escapeHtml4(prospShtDesc.trim());
    }

    public void setQuotConverted(String quotConverted) {
        validateAndSanitize(quotConverted, REF_PATTERN, "Quote Converted");
        this.quotConverted = quotConverted == null ? null : StringEscapeUtils.escapeHtml4(quotConverted.trim());
    }

    public void setQuotType(String quotType) {
        validateAndSanitize(quotType, REF_PATTERN, "Quote Type");
        this.quotType = quotType == null ? null : StringEscapeUtils.escapeHtml4(quotType.trim());
    }
}
