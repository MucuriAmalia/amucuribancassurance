package com.brokersystems.brokerapp.accounts.model;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

/**
 * Created by peter on 4/10/2017.
 */
@Entity
@Table(name="sys_brk_banks")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Banks {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="bn_Id")
    @Setter
    private Long bnId;

    @Column(name="bn_name",nullable = false,unique = true)
    private String bankName;

    @Column(name="bn_sht_desc",nullable = false,unique = true)
    private String bankShtDesc;

    @Column(name="bn_remarks",length = 1500)
    private String bankRemarks;

    @Column(name="bn_eft_support")
    @Setter
    private boolean eftSupported;

    @Column(name="bn_active")
    @Setter
    private boolean active;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setBankName(String bankName) {
        validateAndSanitize(bankName, NAME_PATTERN, "Bank Name");
        this.bankName = bankName == null ? null : StringEscapeUtils.escapeHtml4(bankName.trim());
    }

    public void setBankShtDesc(String bankShtDesc) {
        validateAndSanitize(bankShtDesc, NAME_PATTERN, "Bank Short Description");
        this.bankShtDesc = bankShtDesc == null ? null : StringEscapeUtils.escapeHtml4(bankShtDesc.trim());
    }

    public void setBankRemarks(String bankRemarks) {
        validateAndSanitize(bankRemarks, DESC_PATTERN, "Bank Remarks");
        this.bankRemarks = bankRemarks == null ? null : StringEscapeUtils.escapeHtml4(bankRemarks.trim());
    }
}