package com.brokersystems.brokerapp.accounts.dtos;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountYearDTO {

    @Setter
    private Long yearId;

    @Setter
    private Long year;

    private String branch;

    @Setter
    private Date wef;

    @Setter
    private Date wet;

    private String status;

    @Setter
    private Integer noofMonths;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setBranch(String branch) {
        validateAndSanitize(branch, "Branch");
        this.branch = branch == null ? null : StringEscapeUtils.escapeHtml4(branch.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }

}