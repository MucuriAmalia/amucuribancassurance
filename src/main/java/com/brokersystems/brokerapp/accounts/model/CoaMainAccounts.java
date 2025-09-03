package com.brokersystems.brokerapp.accounts.model;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

/**
 * Created by peter on 4/7/2017.
 */
@Entity
@Table(name="sys_brk_coa_main")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CoaMainAccounts {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="co_Id")
    @Setter
    private Long coId;

    @Column(name="co_code",nullable = false)
    private String code;

    @Column(name="co_name",nullable = false)
    private String name;

    @Column(name="co_header",nullable = false)
    private String header;

    @Column(name="co_footer",nullable = false)
    private String footer;

    @Column(name="co_acc_type",nullable = false)
    private String accountType;

    @Column(name="co_pl_bs",nullable = false)
    private String plBalSheet;

    @Column(name="co_accounts_order",nullable = false)
    private String accountsOrder;

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

    public void setHeader(String header) {
        validateAndSanitize(header, DESC_PATTERN, "Header");
        this.header = header == null ? null : StringEscapeUtils.escapeHtml4(header.trim());
    }

    public void setFooter(String footer) {
        validateAndSanitize(footer, DESC_PATTERN, "Footer");
        this.footer = footer == null ? null : StringEscapeUtils.escapeHtml4(footer.trim());
    }

    public void setAccountType(String accountType) {
        validateAndSanitize(accountType, NAME_PATTERN, "Account Type");
        this.accountType = accountType == null ? null : StringEscapeUtils.escapeHtml4(accountType.trim());
    }

    public void setPlBalSheet(String plBalSheet) {
        validateAndSanitize(plBalSheet, NAME_PATTERN, "P/L Balance Sheet");
        this.plBalSheet = plBalSheet == null ? null : StringEscapeUtils.escapeHtml4(plBalSheet.trim());
    }

    public void setAccountsOrder(String accountsOrder) {
        validateAndSanitize(accountsOrder, NAME_PATTERN, "Accounts Order");
        this.accountsOrder = accountsOrder == null ? null : StringEscapeUtils.escapeHtml4(accountsOrder.trim());
    }
}