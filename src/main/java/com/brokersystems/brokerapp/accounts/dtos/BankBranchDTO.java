package com.brokersystems.brokerapp.accounts.dtos;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BankBranchDTO {

    @Setter
    private Long bbId;

    private String branchName;

    private void validateAndSanitize(String input) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + "Branch Name");
        }
    }

    public void setBranchName(String branchName) {
        validateAndSanitize(branchName);
        this.branchName = branchName == null ? null : StringEscapeUtils.escapeHtml4(branchName.trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankBranchDTO that = (BankBranchDTO) o;
        return bbId != null && bbId.equals(that.bbId);
    }

    @Override
    public int hashCode() {
        return bbId != null ? bbId.hashCode() : 0;
    }
}