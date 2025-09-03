package com.brokersystems.brokerapp.accounts.model;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;

@Entity
@Table(name="sys_brk_acct_classes")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountsBusinessClasses {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="bac_Id")
    @Setter
    private Long bacId;

    @Column(name="bac_desc")
    private String bclDesc;

    @Column(name="bac_order")
    @Setter
    private Integer order;

    private void validateAndSanitize(String input) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.DESC_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + "Business Class Description");
        }
    }

    public void setBclDesc(String bclDesc) {
        validateAndSanitize(bclDesc);
        this.bclDesc = bclDesc == null ? null : StringEscapeUtils.escapeHtml4(bclDesc.trim());
    }
}