package com.brokersystems.brokerapp.accounts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import java.math.BigDecimal;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

@Entity
@Table(name="sys_brk_rpt_formats")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FinalReportFormats {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="rf_id")
    @Setter
    private Long rfId;

    @Column(name = "rf_row_code",length = 10)
    private String rowCode;

    @Column(name="rf_rpt_type",length = 2)
    @Enumerated(EnumType.STRING)
    @Setter
    private FinalReportTypes reportTypes;

    @Column(name = "rf_description")
    private String description;

    @Column(name = "rf_detail_format")
    private String detailFormat;

    @Column(name = "rf_summary_format")
    private String summaryFormat;

    @Column(name = "rf_type")
    private String type;

    @Column(name = "rf_order")
    @Setter
    private Integer order;

    @Column(name = "rf_picked_from")
    private String pickedFrom;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="rf_depends_on")
    @Setter
    private FinalReportFormats dependsOn;

    @Column(name = "rf_asst_liabl")
    private String assetLiability;

    @Column(name = "rf_asst_liabl_sign")
    @Setter
    private Integer assetLiabilitySign;

    @Column(name = "rf_allocation_perc")
    @Setter
    private BigDecimal allocation;

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

    public void setAssetLiability(String assetLiability) {
        validateAndSanitize(assetLiability, NAME_PATTERN, "Asset Liability");
        this.assetLiability = assetLiability == null ? null : StringEscapeUtils.escapeHtml4(assetLiability.trim());
    }
}