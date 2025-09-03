package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.*;

public class CoaSubAccountsDTO {

    private static final Logger logger = LoggerFactory.getLogger(CoaSubAccountsDTO.class);

    private Long coId;
    private String code;
    private String name;
    private String integration;
    private String accountsOrder;
    private Long mainAcctId;
    private Long mainAcctName;
    private Long acctTypeId;
    private String accTypeName;
    private String controlAccount;
    private String applicableToScl;
    private String sublass;
    private Long scId;

    private String sapGlAccount;
    private String sapGlAccountBusiness;
    private String sapGlAccountCorporate;

    public String getSapGlAccountBusiness() {
        return sapGlAccountBusiness;
    }

    public void setSapGlAccountBusiness(String sapGlAccountBusiness) {
        this.sapGlAccountBusiness = sapGlAccountBusiness;
    }

    public String getSapGlAccountCorporate() {
        return sapGlAccountCorporate;
    }

    public void setSapGlAccountCorporate(String sapGlAccountCorporate) {
        this.sapGlAccountCorporate = sapGlAccountCorporate;
    }

    public String getSapGlAccount() {
        return sapGlAccount;
    }

    public void setSapGlAccount(String sapGlAccount) {
        this.sapGlAccount = sapGlAccount;
    }

    public Long getScId() {
        return scId;
    }

    public void setScId(Long scId) {
        this.scId = scId;
    }

    public String getApplicableToScl() {
        return applicableToScl;
    }

    public String getSublass() {
        return sublass;
    }

    public String getIntegration() {
        return integration;
    }

    public String getAccountsOrder() {
        return accountsOrder;
    }

    public Long getMainAcctId() {
        return mainAcctId;
    }

    public void setMainAcctId(Long mainAcctId) {
        this.mainAcctId = mainAcctId;
    }

    public Long getMainAcctName() {
        return mainAcctName;
    }

    public void setMainAcctName(Long mainAcctName) {
        this.mainAcctName = mainAcctName;
    }

    public Long getAcctTypeId() {
        return acctTypeId;
    }

    public void setAcctTypeId(Long acctTypeId) {
        this.acctTypeId = acctTypeId;
    }

    public String getAccTypeName() {
        return accTypeName;
    }

    public String getControlAccount() {
        return controlAccount;
    }

    public Long getCoId() {
        return coId;
    }

    public void setCoId(Long coId) {
        this.coId = coId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
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

    public void setCode(String code) {
        validateAndSanitize(code, REF_PATTERN, "Code");
        this.code = code == null ? null : StringEscapeUtils.escapeHtml4(code.trim());
    }

    public void setName(String name) {
        validateAndSanitize(name, NAME_PATTERN, "Name");
        this.name = name == null ? null : StringEscapeUtils.escapeHtml4(name.trim());
    }

    public void setIntegration(String integration) {
        validateAndSanitize(integration, DESC_PATTERN, "Integration");
        this.integration = integration == null ? null : StringEscapeUtils.escapeHtml4(integration.trim());
    }

    public void setAccountsOrder(String accountsOrder) {
        validateAndSanitize(accountsOrder, REF_PATTERN, "Accounts Order");
        this.accountsOrder = accountsOrder == null ? null : StringEscapeUtils.escapeHtml4(accountsOrder.trim());
    }

    public void setAccTypeName(String accTypeName) {
        validateAndSanitize(accTypeName, NAME_PATTERN, "Account Type Name");
        this.accTypeName = accTypeName == null ? null : StringEscapeUtils.escapeHtml4(accTypeName.trim());
    }

    public void setControlAccount(String controlAccount) {
        validateAndSanitize(controlAccount, REF_PATTERN, "Control Account");
        this.controlAccount = controlAccount == null ? null : StringEscapeUtils.escapeHtml4(controlAccount.trim());
    }

    public void setApplicableToScl(String applicableToScl) {
        validateAndSanitize(applicableToScl, REF_PATTERN, "Applicable To SCL");
        this.applicableToScl = applicableToScl == null ? null : StringEscapeUtils.escapeHtml4(applicableToScl.trim());
    }

    public void setSublass(String sublass) {
        validateAndSanitize(sublass, REF_PATTERN, "Sublass");
        this.sublass = sublass == null ? null : StringEscapeUtils.escapeHtml4(sublass.trim());
    }
}
