package com.brokersystems.brokerapp.quotes.dto;

import com.brokersystems.brokerapp.quotes.model.QuoteProductBean;
import com.brokersystems.brokerapp.uw.model.RiskSectionBean;
import com.brokersystems.brokerapp.uw.model.RiskTransBean;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuoteDTO {

    private static final Logger logger = LoggerFactory.getLogger(CreateQuoteDTO.class);

    private Long quoteId;
    private String clientType;
    private String businessType;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;
    private Long bindCode;
    private String bindName;
    private String productName;
    private String insuranceCompany;
    private Long clientId;
    private Long branchId;
    private Long cardId;
    private Long paymentId;
    private Long currencyId;
    private Long sourceId;
    private RiskTransBean riskBean;
    private QuoteProductBean quoteProductBean;
    private Long prodId;
    private List<RiskSectionBean> sections;
    private String medicalCoverType;
    private String quoteType;
    private Long subAgentId;
    private String subAgent;
    private Long marketerAgentId;
    private String marketerAgent;
    private Long introducerAgentId;
    private String introducerAgent;
    private Long leadsManId;
    private String leadsMan;
    private String activationCode;
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

    public void setClientType(String clientType) {
        validateAndSanitize(clientType, NAME_PATTERN, "Client Type");
        this.clientType = clientType == null ? null : StringEscapeUtils.escapeHtml4(clientType.trim());
    }

    public void setBusinessType(String businessType) {
        validateAndSanitize(businessType, NAME_PATTERN, "Business Type");
        this.businessType = businessType == null ? null : StringEscapeUtils.escapeHtml4(businessType.trim());
    }

    public void setBindName(String bindName) {
        validateAndSanitize(bindName, NAME_PATTERN, "Bind Name");
        this.bindName = bindName == null ? null : StringEscapeUtils.escapeHtml4(bindName.trim());
    }

    public void setProductName(String productName) {
        validateAndSanitize(productName, NAME_PATTERN, "Product Name");
        this.productName = productName == null ? null : StringEscapeUtils.escapeHtml4(productName.trim());
    }

    public void setInsuranceCompany(String insuranceCompany) {
        validateAndSanitize(insuranceCompany, NAME_PATTERN, "Insurance Company");
        this.insuranceCompany = insuranceCompany == null ? null : StringEscapeUtils.escapeHtml4(insuranceCompany.trim());
    }

    public void setMedicalCoverType(String medicalCoverType) {
        validateAndSanitize(medicalCoverType, REF_PATTERN, "Medical Cover Type");
        this.medicalCoverType = medicalCoverType == null ? null : StringEscapeUtils.escapeHtml4(medicalCoverType.trim());
    }

    public void setQuoteType(String quoteType) {
        validateAndSanitize(quoteType, REF_PATTERN, "Quote Type");
        this.quoteType = quoteType == null ? null : StringEscapeUtils.escapeHtml4(quoteType.trim());
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

    public void setActivationCode(String activationCode) {
        validateAndSanitize(activationCode, REF_PATTERN, "Activation Code");
        this.activationCode = activationCode == null ? null : StringEscapeUtils.escapeHtml4(activationCode.trim());
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
