package com.brokersystems.brokerapp.accounts.dtos;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FinalReportFormatTotalDTO {

    @Setter
    private Long rftId;

    @Setter
    private Boolean sign;

    private String affsign;

    private String column;

    private String total;

    @Setter
    private Long totalColId;

    @Setter
    private Long columnColId;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setAffsign(String affsign) {
        validateAndSanitize(affsign, "Affiliated Signing");
        this.affsign = affsign == null ? null : StringEscapeUtils.escapeHtml4(affsign.trim());
    }

    public void setColumn(String column) {
        validateAndSanitize(column, "Column");
        this.column = column == null ? null : StringEscapeUtils.escapeHtml4(column.trim());
    }

    public void setTotal(String total) {
        validateAndSanitize(total, "Total");
        this.total = total == null ? null : StringEscapeUtils.escapeHtml4(total.trim());
    }

}