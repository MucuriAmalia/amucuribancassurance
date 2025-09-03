package com.brokersystems.brokerapp.trans.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.brokersystems.brokerapp.common.Constants.*;

public class ChequeTransDTO {

    private static final Logger logger = LoggerFactory.getLogger(ChequeTransDTO.class);

    private Long paymentModeId;
    private BigDecimal amount;
    private Long ctNo;
    private Long payee;
    private Long bankActCode;
    private Long branchCode;
    private Long curId;
    private String paymentType;
    private String narration;
    private String invoiceNo;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date invoiceDate;
    private Date requistionDate;
    private String refNo;
    private String bankAcctName;
    private String currency;
    private String branch;
    private String raisedUser;
    private String paymentMethod;
    private String payeeName;
    private Long sourcePostedUser;
    private Date sourcePostedDate;
    private String accountNo;
    private String origin;
    private String originType;

    public String getOrigin() {
        return origin;
    }


    public String getOriginType() {
        return originType;
    }


    public String getAccountNo() {
        return accountNo;
    }


    public Long getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(Long branchCode) {
        this.branchCode = branchCode;
    }

    public Date getRequistionDate() {
        return requistionDate;
    }

    public void setRequistionDate(Date requistionDate) {
        this.requistionDate = requistionDate;
    }

    public String getRefNo() {
        return refNo;
    }


    public String getBankAcctName() {
        return bankAcctName;
    }


    public String getCurrency() {
        return currency;
    }


    public String getBranch() {
        return branch;
    }


    public String getRaisedUser() {
        return raisedUser;
    }


    public String getPaymentMethod() {
        return paymentMethod;
    }


    public String getPayeeName() {
        return payeeName;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    private List<PettyCashDtlsDTO> pettyCashTrans;
    private List<ChequeTransDtlsDTO> glTrans;

    public List<PettyCashDtlsDTO> getPettyCashTrans() {
        return pettyCashTrans;
    }

    public void setPettyCashTrans(List<PettyCashDtlsDTO> pettyCashTrans) {
        this.pettyCashTrans = pettyCashTrans;
    }

    public List<ChequeTransDtlsDTO> getGlTrans() {
        return glTrans;
    }

    public void setGlTrans(List<ChequeTransDtlsDTO> glTrans) {
        this.glTrans = glTrans;
    }

    private String source;

    public String getSource() {
        return source;
    }


    public Long getSourcePostedUser() {
        return sourcePostedUser;
    }

    public void setSourcePostedUser(Long sourcePostedUser) {
        this.sourcePostedUser = sourcePostedUser;
    }

    public Date getSourcePostedDate() {
        return sourcePostedDate;
    }

    public void setSourcePostedDate(Date sourcePostedDate) {
        this.sourcePostedDate = sourcePostedDate;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }


    public String getNarration() {
        return narration;
    }


    public String getPaymentType() {
        return paymentType;
    }


    public Long getCurId() {
        return curId;
    }

    public void setCurId(Long curId) {
        this.curId = curId;
    }

    public Long getBankActCode() {
        return bankActCode;
    }

    public void setBankActCode(Long bankActCode) {
        this.bankActCode = bankActCode;
    }

    public Long getPayee() {
        return payee;
    }

    public void setPayee(Long payee) {
        this.payee = payee;
    }

    public Long getCtNo() {
        return ctNo;
    }

    public void setCtNo(Long ctNo) {
        this.ctNo = ctNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getPaymentModeId() {
        return paymentModeId;
    }

    public void setPaymentModeId(Long paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

    @Override
    public String toString() {
        return "ChequeTransDTO{" +
                "paymentModeId=" + paymentModeId +
                ", amount=" + amount +
                ", ctNo=" + ctNo +
                ", payee=" + payee +
                ", bankActCode=" + bankActCode +
                ", curId=" + curId +
                ", paymentType='" + paymentType + '\'' +
                ", narration='" + narration + '\'' +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", sourcePostedUser=" + sourcePostedUser +
                ", sourcePostedDate=" + sourcePostedDate +
                ", pettyCashTrans=" + pettyCashTrans +
                ", glTrans=" + glTrans +
                ", source='" + source + '\'' +
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

    public void setPaymentType(String paymentType) {
        validateAndSanitize(paymentType, REF_PATTERN, "Payment Type");
        this.paymentType = paymentType == null ? null : StringEscapeUtils.escapeHtml4(paymentType.trim());
    }

    public void setNarration(String narration) {
        validateAndSanitize(narration, DESC_PATTERN, "Narration");
        this.narration = narration == null ? null : StringEscapeUtils.escapeHtml4(narration.trim());
    }

    public void setInvoiceNo(String invoiceNo) {
        validateAndSanitize(invoiceNo, REF_PATTERN, "Invoice Number");
        this.invoiceNo = invoiceNo == null ? null : StringEscapeUtils.escapeHtml4(invoiceNo.trim());
    }

    public void setRefNo(String refNo) {
        validateAndSanitize(refNo, REF_PATTERN, "Reference Number");
        this.refNo = refNo == null ? null : StringEscapeUtils.escapeHtml4(refNo.trim());
    }

    public void setBankAcctName(String bankAcctName) {
        validateAndSanitize(bankAcctName, NAME_PATTERN, "Bank Account Name");
        this.bankAcctName = bankAcctName == null ? null : StringEscapeUtils.escapeHtml4(bankAcctName.trim());
    }

    public void setCurrency(String currency) {
        validateAndSanitize(currency, NAME_PATTERN, "Currency");
        this.currency = currency == null ? null : StringEscapeUtils.escapeHtml4(currency.trim());
    }

    public void setBranch(String branch) {
        validateAndSanitize(branch, NAME_PATTERN, "Branch");
        this.branch = branch == null ? null : StringEscapeUtils.escapeHtml4(branch.trim());
    }

    public void setRaisedUser(String raisedUser) {
        validateAndSanitize(raisedUser, CREATED_BY_PATTERN, "Raised User");
        this.raisedUser = raisedUser == null ? null : StringEscapeUtils.escapeHtml4(raisedUser.trim());
    }

    public void setPaymentMethod(String paymentMethod) {
        validateAndSanitize(paymentMethod, REF_PATTERN, "Payment Method");
        this.paymentMethod = paymentMethod == null ? null : StringEscapeUtils.escapeHtml4(paymentMethod.trim());
    }

    public void setPayeeName(String payeeName) {
        validateAndSanitize(payeeName, NAME_PATTERN, "Payee Name");
        this.payeeName = payeeName == null ? null : StringEscapeUtils.escapeHtml4(payeeName.trim());
    }

    public void setAccountNo(String accountNo) {
        validateAndSanitize(accountNo, REF_PATTERN, "Account Number");
        this.accountNo = accountNo == null ? null : StringEscapeUtils.escapeHtml4(accountNo.trim());
    }

    public void setOrigin(String origin) {
        validateAndSanitize(origin, REF_PATTERN, "Origin");
        this.origin = origin == null ? null : StringEscapeUtils.escapeHtml4(origin.trim());
    }

    public void setOriginType(String originType) {
        validateAndSanitize(originType, REF_PATTERN, "Origin Type");
        this.originType = originType == null ? null : StringEscapeUtils.escapeHtml4(originType.trim());
    }

    public void setSource(String source) {
        validateAndSanitize(source, REF_PATTERN, "Source");
        this.source = source == null ? null : StringEscapeUtils.escapeHtml4(source.trim());
    }
}
