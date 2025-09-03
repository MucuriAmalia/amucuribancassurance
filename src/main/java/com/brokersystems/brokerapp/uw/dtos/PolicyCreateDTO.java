package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brokersystems.brokerapp.server.utils.CustomDateDeserializer;
import com.brokersystems.brokerapp.uw.model.RiskSectionBean;
import com.brokersystems.brokerapp.uw.model.RiskTransBean;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PolicyCreateDTO {

    private static final Logger logger = LoggerFactory.getLogger(PolicyCreateDTO.class);

    private Long policyId;
    private String polNo;
    private String proposalNo;
    private Integer totalInstalments;
    private Integer paidInsts;
    private Integer polTerm;
    private String frequency;
    private String coinsuranceBusiness;
    private String error;
    private String OldpolNo;
    private String clientPolNo;
    private String polRevNo;
    private String polRevStatus;
    private String resubmissionComment;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date polPaidToDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date coverFrom;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date coverTo;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date renewalDate;
    private boolean importRisks;
    private String adminFeePolicy;
    private Long clientId;
    private BigDecimal commRate;
    private BigDecimal subAgentCommRate;
    private BigDecimal introducerCommRate;
    private BigDecimal marketerCommRate;
    private Long agentId;
    private Long branchId;
    private Long paymentId;
    private Long currencyId;
    private Long subAgentId;
    private Long prodId;
    private Long bindCode;
    private Long[] bindCodes;
    private String bindName;
    private String productName;
    private Long cardId;
    private String quizTaken;
    private String insuranceCompany;
    private RiskTransBean riskBean;
    private List<RiskSectionBean> sections;
    private Long prevPolicy;
    private String interfaceType;
    private String status;
    private String businessType;
    private String transType;
    private BigDecimal negotiatedPremium;
    private String topUpFreq;
    private BigDecimal investment;
    private BigDecimal topUp;
    private String investmentFreq;
    private Integer investmentTerm;
    private boolean allowTopUps;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date firstTopUp;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date lastTopUp;
    private Long introducerAgentId;
    private Long marketerAgentId;
    private Long leadsManId;
    private String absaNoIntroducer;
    private String absaNoLeadsMan;
    private String absaNoSubAgent;
    private String absaNoMarketer;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date accrualInstDate;
    private String schemePolicy;
    private List<String> checkerIds;
    private String accrualPaymentType;
    private String cover_option_fpp;
    private String cover_option_pa;
    private String cover_option_en;
    private String cover_option_up;
    private String bulkUpload;

    public String getResubmissionComment() {
        return resubmissionComment;
    }

    public void setResubmissionComment(String resubmissionComment) {
        this.resubmissionComment = resubmissionComment == null ? null : StringEscapeUtils.escapeHtml4(resubmissionComment.trim());
    }
//    private void validateAndSanitize(String input, String pattern, String fieldName) {
//        if (input == null) {
//            return;
//        }
//        //logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
//        String trimmedInput = input.trim();
//        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
//            throw new IllegalArgumentException(
//                    String.format("Invalid characters in %s: '%s' does not match pattern %s", fieldName, trimmedInput, pattern)
//            );
//        }
//    }
//
//    public void setPolNo(String polNo) {
//        validateAndSanitize(polNo, REF_PATTERN, "Policy Number");
//        this.polNo = polNo == null ? null : StringEscapeUtils.escapeHtml4(polNo.trim());
//    }
//
//    public void setProposalNo(String proposalNo) {
//        validateAndSanitize(proposalNo, REF_PATTERN, "Proposal Number");
//        this.proposalNo = proposalNo == null ? null : StringEscapeUtils.escapeHtml4(proposalNo.trim());
//    }
//
//    public void setFrequency(String frequency) {
//        validateAndSanitize(frequency, NAME_PATTERN, "Frequency");
//        this.frequency = frequency == null ? null : StringEscapeUtils.escapeHtml4(frequency.trim());
//    }
//
//    public void setCoinsuranceBusiness(String coinsuranceBusiness) {
//        validateAndSanitize(coinsuranceBusiness, NAME_PATTERN, "Coinsurance Business");
//        this.coinsuranceBusiness = coinsuranceBusiness == null ? null : StringEscapeUtils.escapeHtml4(coinsuranceBusiness.trim());
//    }
//
//    public void setError(String error) {
//        validateAndSanitize(error, DESC_PATTERN, "Error");
//        this.error = error == null ? null : StringEscapeUtils.escapeHtml4(error.trim());
//    }
//
//    public void setOldpolNo(String OldpolNo) {
//        validateAndSanitize(OldpolNo, REF_PATTERN, "Old Policy Number");
//        this.OldpolNo = OldpolNo == null ? null : StringEscapeUtils.escapeHtml4(OldpolNo.trim());
//    }
//
//    public void setClientPolNo(String clientPolNo) {
//        validateAndSanitize(clientPolNo, REF_PATTERN, "Client Policy Number");
//        this.clientPolNo = clientPolNo == null ? null : StringEscapeUtils.escapeHtml4(clientPolNo.trim());
//    }
//
//    public void setPolRevNo(String polRevNo) {
//        validateAndSanitize(polRevNo, REF_PATTERN, "Policy Revision Number");
//        this.polRevNo = polRevNo == null ? null : StringEscapeUtils.escapeHtml4(polRevNo.trim());
//    }
//
//    public void setPolRevStatus(String polRevStatus) {
//        validateAndSanitize(polRevStatus, NAME_PATTERN, "Policy Revision Status");
//        this.polRevStatus = polRevStatus == null ? null : StringEscapeUtils.escapeHtml4(polRevStatus.trim());
//    }
//
//    public void setAdminFeePolicy(String adminFeePolicy) {
//        validateAndSanitize(adminFeePolicy, NAME_PATTERN, "Admin Fee Policy");
//        this.adminFeePolicy = adminFeePolicy == null ? null : StringEscapeUtils.escapeHtml4(adminFeePolicy.trim());
//    }
//
//    public void setBindName(String bindName) {
//        validateAndSanitize(bindName, NAME_PATTERN, "Binder Name");
//        this.bindName = bindName == null ? null : StringEscapeUtils.escapeHtml4(bindName.trim());
//    }
//
//    public void setProductName(String productName) {
//        validateAndSanitize(productName, NAME_PATTERN, "Product Name");
//        this.productName = productName == null ? null : StringEscapeUtils.escapeHtml4(productName.trim());
//    }
//
//    public void setQuizTaken(String quizTaken) {
//        validateAndSanitize(quizTaken, NAME_PATTERN, "Quiz Taken");
//        this.quizTaken = quizTaken == null ? null : StringEscapeUtils.escapeHtml4(quizTaken.trim());
//    }
//
//    public void setInsuranceCompany(String insuranceCompany) {
//        validateAndSanitize(insuranceCompany, NAME_PATTERN, "Insurance Company");
//        this.insuranceCompany = insuranceCompany == null ? null : StringEscapeUtils.escapeHtml4(insuranceCompany.trim());
//    }
//
//    public void setInterfaceType(String interfaceType) {
//        validateAndSanitize(interfaceType, NAME_PATTERN, "Interface Type");
//        this.interfaceType = interfaceType == null ? null : StringEscapeUtils.escapeHtml4(interfaceType.trim());
//    }
//
//    public void setStatus(String status) {
//        validateAndSanitize(status, NAME_PATTERN, "Status");
//        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
//    }
//
//    public void setBusinessType(String businessType) {
//        validateAndSanitize(businessType, NAME_PATTERN, "Business Type");
//        this.businessType = businessType == null ? null : StringEscapeUtils.escapeHtml4(businessType.trim());
//    }
//
//    public void setTransType(String transType) {
//        validateAndSanitize(transType, NAME_PATTERN, "Transaction Type");
//        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
//    }
//
//    public void setTopUpFreq(String topUpFreq) {
//        validateAndSanitize(topUpFreq, NAME_PATTERN, "Top Up Frequency");
//        this.topUpFreq = topUpFreq == null ? null : StringEscapeUtils.escapeHtml4(topUpFreq.trim());
//    }
//
//    public void setInvestmentFreq(String investmentFreq) {
//        validateAndSanitize(investmentFreq, NAME_PATTERN, "Investment Frequency");
//        this.investmentFreq = investmentFreq == null ? null : StringEscapeUtils.escapeHtml4(investmentFreq.trim());
//    }
//
//    public void setAbsaNoIntroducer(String absaNoIntroducer) {
//        validateAndSanitize(absaNoIntroducer, REF_PATTERN, "Absa Number Introducer");
//        this.absaNoIntroducer = absaNoIntroducer == null ? null : StringEscapeUtils.escapeHtml4(absaNoIntroducer.trim());
//    }
//
//    public void setAbsaNoLeadsMan(String absaNoLeadsMan) {
//        validateAndSanitize(absaNoLeadsMan, REF_PATTERN, "Absa Number Leads Man");
//        this.absaNoLeadsMan = absaNoLeadsMan == null ? null : StringEscapeUtils.escapeHtml4(absaNoLeadsMan.trim());
//    }
//
//    public void setAbsaNoSubAgent(String absaNoSubAgent) {
//        validateAndSanitize(absaNoSubAgent, REF_PATTERN, "Absa Number Sub Agent");
//        this.absaNoSubAgent = absaNoSubAgent == null ? null : StringEscapeUtils.escapeHtml4(absaNoSubAgent.trim());
//    }
//
//    public void setAbsaNoMarketer(String absaNoMarketer) {
//        validateAndSanitize(absaNoMarketer, REF_PATTERN, "Absa Number Marketer");
//        this.absaNoMarketer = absaNoMarketer == null ? null : StringEscapeUtils.escapeHtml4(absaNoMarketer.trim());
//    }
//
//    public void setSchemePolicy(String schemePolicy) {
//        validateAndSanitize(schemePolicy, NAME_PATTERN, "Scheme Policy");
//        this.schemePolicy = schemePolicy == null ? null : StringEscapeUtils.escapeHtml4(schemePolicy.trim());
//    }
//
//    public void setAccrualPaymentType(String accrualPaymentType) {
//        validateAndSanitize(accrualPaymentType, NAME_PATTERN, "Accrual Payment Type");
//        this.accrualPaymentType = accrualPaymentType == null ? null : StringEscapeUtils.escapeHtml4(accrualPaymentType.trim());
//    }
//
//    public void setCover_option_fpp(String cover_option_fpp) {
//        validateAndSanitize(cover_option_fpp, NAME_PATTERN, "Cover Option FPP");
//        this.cover_option_fpp = cover_option_fpp == null ? null : StringEscapeUtils.escapeHtml4(cover_option_fpp.trim());
//    }
//
//    public void setCover_option_pa(String cover_option_pa) {
//        validateAndSanitize(cover_option_pa, NAME_PATTERN, "Cover Option PA");
//        this.cover_option_pa = cover_option_pa == null ? null : StringEscapeUtils.escapeHtml4(cover_option_pa.trim());
//    }
//
//    public void setCover_option_en(String cover_option_en) {
//        validateAndSanitize(cover_option_en, NAME_PATTERN, "Cover Option EN");
//        this.cover_option_en = cover_option_en == null ? null : StringEscapeUtils.escapeHtml4(cover_option_en.trim());
//    }
//
//    public void setCover_option_up(String cover_option_up) {
//        validateAndSanitize(cover_option_up, NAME_PATTERN, "Cover Option UP");
//        this.cover_option_up = cover_option_up == null ? null : StringEscapeUtils.escapeHtml4(cover_option_up.trim());
//    }
//
//    public void setBulkUpload(String bulkUpload) {
//        validateAndSanitize(bulkUpload, NAME_PATTERN, "Bulk Upload");
//        this.bulkUpload = bulkUpload == null ? null : StringEscapeUtils.escapeHtml4(bulkUpload.trim());
//    }

}
