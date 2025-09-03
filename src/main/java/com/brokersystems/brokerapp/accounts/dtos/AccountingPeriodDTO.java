package com.brokersystems.brokerapp.accounts.dtos;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import java.util.Date;
import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountingPeriodDTO {

    private String branch;

    private String periodName;

    private String transacted;

    @Setter
    private Long periodId;

    @Setter
    private Date wef;

    @Setter
    private Date wet;

    private String status;

    private String userClosed;

    @Setter
    private Date closedDate;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setPeriodName(String periodName) {
        validateAndSanitize(periodName, NAME_PATTERN, "Period Name");
        this.periodName = periodName == null ? null : StringEscapeUtils.escapeHtml4(periodName.trim());
    }

    public void setTransacted(String transacted) {
        validateAndSanitize(transacted, DESC_PATTERN, "Transacted");
        this.transacted = transacted == null ? null : StringEscapeUtils.escapeHtml4(transacted.trim());
    }

    public void setBranch(String branch) {
        validateAndSanitize(branch, NAME_PATTERN, "Branch");
        this.branch = branch == null ? null : StringEscapeUtils.escapeHtml4(branch.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, NAME_PATTERN, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }

    public void setUserClosed(String userClosed) {
        validateAndSanitize(userClosed, NAME_PATTERN, "User Closed");
        this.userClosed = userClosed == null ? null : StringEscapeUtils.escapeHtml4(userClosed.trim());
    }

}