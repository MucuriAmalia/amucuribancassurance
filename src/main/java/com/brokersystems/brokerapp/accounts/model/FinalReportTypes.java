package com.brokersystems.brokerapp.accounts.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringEscapeUtils;

@Getter
public enum FinalReportTypes {
    TB("TB","Trial Balance","Y","N",3,"N","N","N","N","N",true),
    BL("BL","Balance Sheet","Y","N",3,"N","N","N","N","N",false),
    ME("ME","Management Expenses","Y","N",3,"N","N","N","N","N",false),
    CF("CF","Cash Flow Statement","Y","N",3,"N","N","N","N","N",false),
    PL("PL","Profit and Loss","Y","N",3,"N","N","N","N","N",false),
    ;

    private String code;
    private String value;
    private String cummulative;
    private String showAllmonths;
    @Setter
    private Integer prevYearsToShow;
    private String showMonthlyTotal;
    private String showPrevMonthlyTotal;
    private String showBusinessClasses;
    private String showEquity;
    private String consolidated;
    @Setter
    private boolean tb;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    FinalReportTypes(String code, String value, String cummulative, String showAllmonths,
                     Integer prevYearsToShow, String showMonthlyTotal, String showPrevMonthlyTotal,
                     String showBusinessClasses, String showEquity, String consolidated, boolean tb) {
        validateAndSanitize(code, "Code");
        validateAndSanitize(value, "Value");
        validateAndSanitize(cummulative, "Cumulative");
        validateAndSanitize(showAllmonths, "Show All Months");
        validateAndSanitize(showMonthlyTotal, "Show Monthly Total");
        validateAndSanitize(showPrevMonthlyTotal, "Show Previous Monthly Total");
        validateAndSanitize(showBusinessClasses, "Show Business Classes");
        validateAndSanitize(showEquity, "Show Equity");
        validateAndSanitize(consolidated, "Consolidated");
        this.code = code == null ? null : StringEscapeUtils.escapeHtml4(code.trim());
        this.value = value == null ? null : StringEscapeUtils.escapeHtml4(value.trim());
        this.cummulative = cummulative == null ? null : StringEscapeUtils.escapeHtml4(cummulative.trim());
        this.showAllmonths = showAllmonths == null ? null : StringEscapeUtils.escapeHtml4(showAllmonths.trim());
        this.prevYearsToShow = prevYearsToShow;
        this.showMonthlyTotal = showMonthlyTotal == null ? null : StringEscapeUtils.escapeHtml4(showMonthlyTotal.trim());
        this.showPrevMonthlyTotal = showPrevMonthlyTotal == null ? null : StringEscapeUtils.escapeHtml4(showPrevMonthlyTotal.trim());
        this.showBusinessClasses = showBusinessClasses == null ? null : StringEscapeUtils.escapeHtml4(showBusinessClasses.trim());
        this.showEquity = showEquity == null ? null : StringEscapeUtils.escapeHtml4(showEquity.trim());
        this.consolidated = consolidated == null ? null : StringEscapeUtils.escapeHtml4(consolidated.trim());
        this.tb = tb;
    }

    public void setCode(String code) {
        validateAndSanitize(code, "Code");
        this.code = code == null ? null : StringEscapeUtils.escapeHtml4(code.trim());
    }

    public void setValue(String value) {
        validateAndSanitize(value, "Value");
        this.value = value == null ? null : StringEscapeUtils.escapeHtml4(value.trim());
    }

    public void setCummulative(String cummulative) {
        validateAndSanitize(cummulative, "Cumulative");
        this.cummulative = cummulative == null ? null : StringEscapeUtils.escapeHtml4(cummulative.trim());
    }

    public void setShowAllmonths(String showAllmonths) {
        validateAndSanitize(showAllmonths, "Show All Months");
        this.showAllmonths = showAllmonths == null ? null : StringEscapeUtils.escapeHtml4(showAllmonths.trim());
    }

    public void setShowMonthlyTotal(String showMonthlyTotal) {
        validateAndSanitize(showMonthlyTotal, "Show Monthly Total");
        this.showMonthlyTotal = showMonthlyTotal == null ? null : StringEscapeUtils.escapeHtml4(showMonthlyTotal.trim());
    }

    public void setShowPrevMonthlyTotal(String showPrevMonthlyTotal) {
        validateAndSanitize(showPrevMonthlyTotal, "Show Previous Monthly Total");
        this.showPrevMonthlyTotal = showPrevMonthlyTotal == null ? null : StringEscapeUtils.escapeHtml4(showPrevMonthlyTotal.trim());
    }

    public void setShowBusinessClasses(String showBusinessClasses) {
        validateAndSanitize(showBusinessClasses, "Show Business Classes");
        this.showBusinessClasses = showBusinessClasses == null ? null : StringEscapeUtils.escapeHtml4(showBusinessClasses.trim());
    }

    public void setShowEquity(String showEquity) {
        validateAndSanitize(showEquity, "Show Equity");
        this.showEquity = showEquity == null ? null : StringEscapeUtils.escapeHtml4(showEquity.trim());
    }

    public void setConsolidated(String consolidated) {
        validateAndSanitize(consolidated, "Consolidated");
        this.consolidated = consolidated == null ? null : StringEscapeUtils.escapeHtml4(consolidated.trim());
    }
}