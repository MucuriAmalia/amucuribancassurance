package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.AccountTypes;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

/**
 * Created by peter on 4/7/2017.
 */
@Entity
@Table(name="sys_brk_coa_sub")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CoaSubAccounts {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="co_Id")
    @Setter
    private Long coId;

    @Column(name="co_code",nullable = false,unique = true)
    private String code;

    @Column(name="co_name",nullable = false)
    private String name;

    @Column(name="co_int_account",nullable = false)
    private String integration;

    @Column(name="co_accounts_order",nullable = false)
    private String accountsOrder;

    @ManyToOne
    @JoinColumn(name="co_main_acc_id")
    @Setter
    private CoaMainAccounts mainAccounts;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="co_acct_type_id")
    @Setter
    private AccountTypes accountTypes;

    @Column(name="co_control_acc",length = 1)
    private String controlAccount;

    @Column(name="co_mapped_to_scl",length = 1)
    private String mappedToSubclass;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="co_sc_id")
    @Setter
    private SubClassDef subClassDef;

    @Column(name="co_sap_gl_account")
    private String sapGlAccount;

    @Column(name="co_sap_gl_account_business")
    private String sapGlAccountBusiness;

    @Column(name="co_sap_gl_account_corporate")
    private String sapGlAccountCorporate;

    @Transient
    private String applLevel;


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
        validateAndSanitize(sapGlAccount, NAME_PATTERN, "SAP GL Account");
        this.sapGlAccount = sapGlAccount == null ? null : StringEscapeUtils.escapeHtml4(sapGlAccount.trim());
    }


    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setCode(String code) {
        validateAndSanitize(code, NAME_PATTERN, "Code");
        this.code = code == null ? null : StringEscapeUtils.escapeHtml4(code.trim());
    }

    public void setName(String name) {
        validateAndSanitize(name, NAME_PATTERN, "Name");
        this.name = name == null ? null : StringEscapeUtils.escapeHtml4(name.trim());
    }

    public void setIntegration(String integration) {
        validateAndSanitize(integration, NAME_PATTERN, "Integration");
        this.integration = integration == null ? null : StringEscapeUtils.escapeHtml4(integration.trim());
    }

    public void setAccountsOrder(String accountsOrder) {
        validateAndSanitize(accountsOrder, NAME_PATTERN, "Accounts Order");
        this.accountsOrder = accountsOrder == null ? null : StringEscapeUtils.escapeHtml4(accountsOrder.trim());
    }

    public void setControlAccount(String controlAccount) {
        validateAndSanitize(controlAccount, NAME_PATTERN, "Control Account");
        this.controlAccount = controlAccount == null ? null : StringEscapeUtils.escapeHtml4(controlAccount.trim());
    }

    public void setMappedToSubclass(String mappedToSubclass) {
        validateAndSanitize(mappedToSubclass, NAME_PATTERN, "Mapped to Subclass");
        this.mappedToSubclass = mappedToSubclass == null ? null : StringEscapeUtils.escapeHtml4(mappedToSubclass.trim());
    }

    public void setApplLevel(String applLevel) {
        validateAndSanitize(applLevel, NAME_PATTERN, "Application Level");
        this.applLevel = applLevel == null ? null : StringEscapeUtils.escapeHtml4(applLevel.trim());
    }
}