package com.brokersystems.brokerapp.quotes.dto;

import com.brokersystems.brokerapp.quotes.model.QuotRiskLimits;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.brokersystems.brokerapp.common.Constants.*;

@Getter
public class QuoteDetailsDTO {

    private static final Logger logger = LoggerFactory.getLogger(QuoteDetailsDTO.class);

    @Setter
    private Long quoteId;
    @Setter
    private Long quotProductId;
    private String quotStatus;
    private String fname;
    private String otherNames;
    @Setter
    private Long tenId;
    @Setter
    private Long pmId;
    private String paymentMode;
    @Setter
    private Long sourceGroupId;
    private String sourceGroupName;
    @Setter
    private Long sourceId;
    private String sourceName;
    @Setter
    private Long obId;
    private String branch;
    private String currency;
    @Setter
    private Long curCode;
    private String quotNo;
    private String quotRevNo;
    @Setter
    private BigDecimal sumInsured;
    @Setter
    private BigDecimal premium;
    @Setter
    private BigDecimal basicPrem;
    @Setter
    private BigDecimal netPrem;
    @Setter
    private BigDecimal commAmt;
    @Setter
    private BigDecimal trainingLevy;
    @Setter
    private BigDecimal phcf;
    @Setter
    private BigDecimal stampDuty;
    @Setter
    private BigDecimal whtx;
    @Setter
    private BigDecimal extras;
    @Setter
    private Date quoteWef;
    @Setter
    private Date quoteWet;
    @Setter
    private Date expiryDate;
    private String clientType;
    private String quoteType;
    private String insured;
    private String riskDesc;
    private String riskShtDesc;
    @Setter
    private List<QuotRiskLimits> quotRiskLimitsList;
//    private QuoteRiskTrans quoteRiskTrans;
    private String contract;
    private String insuranceCompany;
    private String product;
    private String classification;
    private String covType;
    private String idNo;
    private String quoteConverted;
    @Setter
    private Long subAgentId;
    private String subAgent;
    @Setter
    private Long marketerAgentId;
    private String marketerAgent;
    @Setter
    private Long introducerAgentId;
    private String introducerAgent;
    @Setter
    private Long leadsManId;
    private String leadsMan;
    private String absaNoIntroducer;
    private String absaNoLeadsMan;
    private String absaNoSubAgent;
    private String absaNoMarketer;

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

    public void setPaymentMode(String paymentMode) {
        validateAndSanitize(paymentMode, DESC_PATTERN, "Payment Mode");
        this.paymentMode = paymentMode == null ? null : StringEscapeUtils.escapeHtml4(paymentMode.trim());
    }

    public void setSourceGroupName(String sourceGroupName) {
        validateAndSanitize(sourceGroupName, NAME_PATTERN, "Source Group Name");
        this.sourceGroupName = sourceGroupName == null ? null : StringEscapeUtils.escapeHtml4(sourceGroupName.trim());
    }

    public void setSourceName(String sourceName) {
        validateAndSanitize(sourceName, NAME_PATTERN, "Source Name");
        this.sourceName = sourceName == null ? null : StringEscapeUtils.escapeHtml4(sourceName.trim());
    }

    public void setBranch(String branch) {
        validateAndSanitize(branch, NAME_PATTERN, "Branch");
        this.branch = branch == null ? null : StringEscapeUtils.escapeHtml4(branch.trim());
    }

    public void setCurrency(String currency) {
        validateAndSanitize(currency, NAME_PATTERN, "Currency");
        this.currency = currency == null ? null : StringEscapeUtils.escapeHtml4(currency.trim());
    }

    public void setQuotNo(String quotNo) {
        validateAndSanitize(quotNo, REF_PATTERN, "Quote Number");
        this.quotNo = quotNo == null ? null : StringEscapeUtils.escapeHtml4(quotNo.trim());
    }

    public void setQuotRevNo(String quotRevNo) {
        validateAndSanitize(quotRevNo, REF_PATTERN, "Quote Revision Number");
        this.quotRevNo = quotRevNo == null ? null : StringEscapeUtils.escapeHtml4(quotRevNo.trim());
    }

    public void setClientType(String clientType) {
        validateAndSanitize(clientType, REF_PATTERN, "Client Type");
        this.clientType = clientType == null ? null : StringEscapeUtils.escapeHtml4(clientType.trim());
    }

    public void setQuoteType(String quoteType) {
        validateAndSanitize(quoteType, REF_PATTERN, "Quote Type");
        this.quoteType = quoteType == null ? null : StringEscapeUtils.escapeHtml4(quoteType.trim());
    }

    public void setInsured(String insured) {
        validateAndSanitize(insured, NAME_PATTERN, "Insured");
        this.insured = insured == null ? null : StringEscapeUtils.escapeHtml4(insured.trim());
    }

    public void setRiskDesc(String riskDesc) {
        validateAndSanitize(riskDesc, DESC_PATTERN, "Risk Description");
        this.riskDesc = riskDesc == null ? null : StringEscapeUtils.escapeHtml4(riskDesc.trim());
    }

    public void setRiskShtDesc(String riskShtDesc) {
        validateAndSanitize(riskShtDesc, DESC_PATTERN, "Risk Short Description");
        this.riskShtDesc = riskShtDesc == null ? null : StringEscapeUtils.escapeHtml4(riskShtDesc.trim());
    }

    public void setContract(String contract) {
        validateAndSanitize(contract, NAME_PATTERN, "Contract");
        this.contract = contract == null ? null : StringEscapeUtils.escapeHtml4(contract.trim());
    }

    public void setInsuranceCompany(String insuranceCompany) {
        validateAndSanitize(insuranceCompany, NAME_PATTERN, "Insurance Company");
        this.insuranceCompany = insuranceCompany == null ? null : StringEscapeUtils.escapeHtml4(insuranceCompany.trim());
    }

    public void setProduct(String product) {
        validateAndSanitize(product, NAME_PATTERN, "Product");
        this.product = product == null ? null : StringEscapeUtils.escapeHtml4(product.trim());
    }

    public void setClassification(String classification) {
        validateAndSanitize(classification, DESC_PATTERN, "Classification");
        this.classification = classification == null ? null : StringEscapeUtils.escapeHtml4(classification.trim());
    }

    public void setCovType(String covType) {
        validateAndSanitize(covType, DESC_PATTERN, "Cover Type");
        this.covType = covType == null ? null : StringEscapeUtils.escapeHtml4(covType.trim());
    }

    public void setIdNo(String idNo) {
        validateAndSanitize(idNo, REF_PATTERN, "ID Number");
        this.idNo = idNo == null ? null : StringEscapeUtils.escapeHtml4(idNo.trim());
    }

    public void setQuoteConverted(String quoteConverted) {
        validateAndSanitize(quoteConverted, REF_PATTERN, "Quote Converted");
        this.quoteConverted = quoteConverted == null ? null : StringEscapeUtils.escapeHtml4(quoteConverted.trim());
    }

    public void setSubAgent(String subAgent) {
        validateAndSanitize(subAgent, NAME_PATTERN, "Sub Agent");
        this.subAgent = subAgent == null ? null : StringEscapeUtils.escapeHtml4(subAgent.trim());
    }

    public void setMarketerAgent(String marketerAgent) {
        validateAndSanitize(marketerAgent, NAME_PATTERN, "Marketer Agent");
        this.marketerAgent = marketerAgent == null ? null : StringEscapeUtils.escapeHtml4(marketerAgent.trim());
    }

    public void setIntroducerAgent(String introducerAgent) {
        validateAndSanitize(introducerAgent, NAME_PATTERN, "Introducer Agent");
        this.introducerAgent = introducerAgent == null ? null : StringEscapeUtils.escapeHtml4(introducerAgent.trim());
    }

    public void setLeadsMan(String leadsMan) {
        validateAndSanitize(leadsMan, NAME_PATTERN, "Leads Man");
        this.leadsMan = leadsMan == null ? null : StringEscapeUtils.escapeHtml4(leadsMan.trim());
    }

    public void setAbsaNoIntroducer(String absaNoIntroducer) {
        validateAndSanitize(absaNoIntroducer, REF_PATTERN, "Absa No Introducer");
        this.absaNoIntroducer = absaNoIntroducer == null ? null : StringEscapeUtils.escapeHtml4(absaNoIntroducer.trim());
    }

    public void setAbsaNoLeadsMan(String absaNoLeadsMan) {
        validateAndSanitize(absaNoLeadsMan, REF_PATTERN, "Absa No Leads Man");
        this.absaNoLeadsMan = absaNoLeadsMan == null ? null : StringEscapeUtils.escapeHtml4(absaNoLeadsMan.trim());
    }

    public void setAbsaNoSubAgent(String absaNoSubAgent) {
        validateAndSanitize(absaNoSubAgent, REF_PATTERN, "Absa No Sub Agent");
        this.absaNoSubAgent = absaNoSubAgent == null ? null : StringEscapeUtils.escapeHtml4(absaNoSubAgent.trim());
    }

    public void setAbsaNoMarketer(String absaNoMarketer) {
        validateAndSanitize(absaNoMarketer, REF_PATTERN, "Absa No Marketer");
        this.absaNoMarketer = absaNoMarketer == null ? null : StringEscapeUtils.escapeHtml4(absaNoMarketer.trim());
    }
}
