package com.brokersystems.brokerapp.accounts.dtos;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import java.math.BigDecimal;
import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FinalReportFormatDTO {

    private String rowCode;

    private String description;

    private String detailFormat;

    private String summaryFormat;

    private String type;

    @Setter
    private Integer order;

    private String pickedFrom;

    private String dependsOn;

    private String assetLiability;

    @Setter
    private Integer assetLiabilitySign;

    @Setter
    private BigDecimal allocation;

    @Setter
    private Long rfId;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setRowCode(String rowCode) {
        validateAndSanitize(rowCode, NAME_PATTERN, "Row Code");
        this.rowCode = rowCode == null ? null : StringEscapeUtils.escapeHtml4(rowCode.trim());
    }

    public void setDescription(String description) {
        validateAndSanitize(description, DESC_PATTERN, "Description");
        this.description = description == null ? null : StringEscapeUtils.escapeHtml4(description.trim());
    }

    public void setDetailFormat(String detailFormat) {
        validateAndSanitize(detailFormat, DESC_PATTERN, "Detail Format");
        this.detailFormat = detailFormat == null ? null : StringEscapeUtils.escapeHtml4(detailFormat.trim());
    }

    public void setSummaryFormat(String summaryFormat) {
        validateAndSanitize(summaryFormat, DESC_PATTERN, "Summary Format");
        this.summaryFormat = summaryFormat == null ? null : StringEscapeUtils.escapeHtml4(summaryFormat.trim());
    }

    public void setType(String type) {
        validateAndSanitize(type, NAME_PATTERN, "Type");
        this.type = type == null ? null : StringEscapeUtils.escapeHtml4(type.trim());
    }

    public void setPickedFrom(String pickedFrom) {
        validateAndSanitize(pickedFrom, NAME_PATTERN, "Picked From");
        this.pickedFrom = pickedFrom == null ? null : StringEscapeUtils.escapeHtml4(pickedFrom.trim());
    }

    public void setDependsOn(String dependsOn) {
        validateAndSanitize(dependsOn, NAME_PATTERN, "Depends On");
        this.dependsOn = dependsOn == null ? null : StringEscapeUtils.escapeHtml4(dependsOn.trim());
    }

    public void setAssetLiability(String assetLiability) {
        validateAndSanitize(assetLiability, NAME_PATTERN, "Asset Liability");
        this.assetLiability = assetLiability == null ? null : StringEscapeUtils.escapeHtml4(assetLiability.trim());
    }

}