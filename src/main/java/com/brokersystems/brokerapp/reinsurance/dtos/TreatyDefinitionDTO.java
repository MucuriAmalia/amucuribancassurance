package com.brokersystems.brokerapp.reinsurance.dtos;

import java.math.BigDecimal;
import java.util.Date;


public class TreatyDefinitionDTO {

    private  Long treatyId;
    private  String treatyType;
    private  Date wef;
    private  Date wet;
    private  BigDecimal cashCall;
    private  BigDecimal cessionRate;
    private  String rateType;
    private  BigDecimal profitCommission;
    private  BigDecimal managementFeeRate;
    private  BigDecimal premiumPortfolio;
    private  BigDecimal claimsPortfolio;
    private  BigDecimal limit;
    private  BigDecimal sumInsuredFrom;
    private  BigDecimal commRate;
    private  BigDecimal facCedeRate;
    private  Long currencyId;
    private  String currencyDesc;
    private  String raisedBy;
    private  String status;
    private  String authBy;


    public TreatyDefinitionDTO() {
    }

    private TreatyDefinitionDTO(final Long treatyId,
                                final String treatyType,
                                final Date wef,
                                final Date wet,
                                final BigDecimal cashCall,
                                final BigDecimal cessionRate,
                                final String rateType,
                                final BigDecimal profitCommission,
                                final BigDecimal managementFeeRate,
                                final BigDecimal premiumPortfolio,
                                final BigDecimal claimsPortfolio,
                                final BigDecimal limit,
                                final BigDecimal commRate,
                                final BigDecimal facCedeRate,
                                final Long currencyId,
                                final String currencyDesc,
                                final String raisedBy,
                                final String status,
                                final String authBy) {
        this.treatyId = treatyId;
        this.treatyType = treatyType;
        this.wef = wef;
        this.wet = wet;
        this.cashCall = cashCall;
        this.cessionRate = cessionRate;
        this.rateType = rateType;
        this.profitCommission = profitCommission;
        this.managementFeeRate = managementFeeRate;
        this.premiumPortfolio = premiumPortfolio;
        this.claimsPortfolio = claimsPortfolio;
        this.limit = limit;
        this.commRate = commRate;
        this.facCedeRate = facCedeRate;
        this.currencyId = currencyId;
        this.currencyDesc = currencyDesc;
        this.raisedBy = raisedBy;
        this.status = status;
        this.authBy = authBy;
    }

    public static TreatyDefinitionDTO data(final Long treatyId,
                                           final String treatyType,
                                           final Date wef,
                                           final Date wet,
                                           final BigDecimal cashCall,
                                           final BigDecimal cessionRate,
                                           final String rateType,
                                           final BigDecimal profitCommission,
                                           final BigDecimal managementFeeRate,
                                           final BigDecimal premiumPortfolio,
                                           final BigDecimal claimsPortfolio,
                                           final BigDecimal limit,
                                           final BigDecimal commRate,
                                           final BigDecimal facCedeRate,
                                           final Long currencyId,
                                           final String currencyDesc,
                                           final String raisedBy,
                                           final String status,
                                           final String authBy){
        return new TreatyDefinitionDTO(treatyId, treatyType, wef, wet, cashCall, cessionRate, rateType, profitCommission,
                managementFeeRate, premiumPortfolio, claimsPortfolio, limit, commRate, facCedeRate, currencyId,
                currencyDesc, raisedBy, status, authBy);

    }

    public BigDecimal getSumInsuredFrom() {
        return sumInsuredFrom;
    }

    public void setSumInsuredFrom(BigDecimal sumInsuredFrom) {
        this.sumInsuredFrom = sumInsuredFrom;
    }

    public Long getTreatyId() {
        return treatyId;
    }

    public String getTreatyType() {
        return treatyType;
    }

    public Date getWef() {
        return wef;
    }

    public Date getWet() {
        return wet;
    }

    public BigDecimal getCashCall() {
        return cashCall;
    }

    public BigDecimal getCessionRate() {
        return cessionRate;
    }

    public String getRateType() {
        return rateType;
    }

    public BigDecimal getProfitCommission() {
        return profitCommission;
    }

    public BigDecimal getManagementFeeRate() {
        return managementFeeRate;
    }

    public BigDecimal getPremiumPortfolio() {
        return premiumPortfolio;
    }

    public BigDecimal getClaimsPortfolio() {
        return claimsPortfolio;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public BigDecimal getCommRate() {
        return commRate;
    }

    public BigDecimal getFacCedeRate() {
        return facCedeRate;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public String getCurrencyDesc() {
        return currencyDesc;
    }

    public String getRaisedBy() {
        return raisedBy;
    }

    public String getStatus() {
        return status;
    }

    public String getAuthBy() {
        return authBy;
    }


    public void setTreatyId(Long treatyId) {
        this.treatyId = treatyId;
    }

    public void setTreatyType(String treatyType) {
        this.treatyType = treatyType;
    }

    public void setWef(Date wef) {
        this.wef = wef;
    }

    public void setWet(Date wet) {
        this.wet = wet;
    }

    public void setCashCall(BigDecimal cashCall) {
        this.cashCall = cashCall;
    }

    public void setCessionRate(BigDecimal cessionRate) {
        this.cessionRate = cessionRate;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public void setProfitCommission(BigDecimal profitCommission) {
        this.profitCommission = profitCommission;
    }

    public void setManagementFeeRate(BigDecimal managementFeeRate) {
        this.managementFeeRate = managementFeeRate;
    }

    public void setPremiumPortfolio(BigDecimal premiumPortfolio) {
        this.premiumPortfolio = premiumPortfolio;
    }

    public void setClaimsPortfolio(BigDecimal claimsPortfolio) {
        this.claimsPortfolio = claimsPortfolio;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public void setCommRate(BigDecimal commRate) {
        this.commRate = commRate;
    }

    public void setFacCedeRate(BigDecimal facCedeRate) {
        this.facCedeRate = facCedeRate;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public void setCurrencyDesc(String currencyDesc) {
        this.currencyDesc = currencyDesc;
    }

    public void setRaisedBy(String raisedBy) {
        this.raisedBy = raisedBy;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAuthBy(String authBy) {
        this.authBy = authBy;
    }
}

//package com.brokersystems.brokerapp.reinsurance.dtos;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.apache.commons.lang3.StringEscapeUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.math.BigDecimal;
//import java.util.Date;
//
//import static com.brokersystems.brokerapp.common.Constants.*;
//
//@Getter
//public class TreatyDefinitionDTO {
//
//    private static final Logger logger = LoggerFactory.getLogger(TreatyDefinitionDTO.class);
//
//    @Setter
//    private Long treatyId;
//    private String treatyType;
//    @Setter
//    private Date wef;
//    @Setter
//    private Date wet;
//    @Setter
//    private BigDecimal cashCall;
//    @Setter
//    private BigDecimal cessionRate;
//    private String rateType;
//    @Setter
//    private BigDecimal profitCommission;
//    @Setter
//    private BigDecimal managementFeeRate;
//    @Setter
//    private BigDecimal premiumPortfolio;
//    @Setter
//    private BigDecimal claimsPortfolio;
//    @Setter
//    private BigDecimal limit;
//    @Setter
//    private BigDecimal sumInsuredFrom;
//    @Setter
//    private BigDecimal commRate;
//    @Setter
//    private BigDecimal facCedeRate;
//    @Setter
//    private Long currencyId;
//    private String currencyDesc;
//    private String raisedBy;
//    private String status;
//    private String authBy;
//
//    public TreatyDefinitionDTO() {
//    }
//
//    private TreatyDefinitionDTO(final Long treatyId,
//                                final String treatyType,
//                                final Date wef,
//                                final Date wet,
//                                final BigDecimal cashCall,
//                                final BigDecimal cessionRate,
//                                final String rateType,
//                                final BigDecimal profitCommission,
//                                final BigDecimal managementFeeRate,
//                                final BigDecimal premiumPortfolio,
//                                final BigDecimal claimsPortfolio,
//                                final BigDecimal limit,
//                                final BigDecimal sumInsuredFrom,
//                                final BigDecimal commRate,
//                                final BigDecimal facCedeRate,
//                                final Long currencyId,
//                                final String currencyDesc,
//                                final String raisedBy,
//                                final String status,
//                                final String authBy) {
//        validateAndSanitize(treatyType, REF_PATTERN, "Treaty Type");
//        validateAndSanitize(rateType, REF_PATTERN, "Rate Type");
//        validateAndSanitize(currencyDesc, REF_PATTERN, "Currency Description");
//        validateAndSanitize(raisedBy, REF_PATTERN, "Raised By");
//        validateAndSanitize(status, REF_PATTERN, "Status");
//        validateAndSanitize(authBy, REF_PATTERN, "Authorized By");
//        this.treatyId = treatyId;
//        this.treatyType = treatyType == null ? null : StringEscapeUtils.escapeHtml4(treatyType.trim());
//        this.wef = wef;
//        this.wet = wet;
//        this.cashCall = cashCall;
//        this.cessionRate = cessionRate;
//        this.rateType = rateType == null ? null : StringEscapeUtils.escapeHtml4(rateType.trim());
//        this.profitCommission = profitCommission;
//        this.managementFeeRate = managementFeeRate;
//        this.premiumPortfolio = premiumPortfolio;
//        this.claimsPortfolio = claimsPortfolio;
//        this.limit = limit;
//        this.sumInsuredFrom = sumInsuredFrom;
//        this.commRate = commRate;
//        this.facCedeRate = facCedeRate;
//        this.currencyId = currencyId;
//        this.currencyDesc = currencyDesc == null ? null : StringEscapeUtils.escapeHtml4(currencyDesc.trim());
//        this.raisedBy = raisedBy == null ? null : StringEscapeUtils.escapeHtml4(raisedBy.trim());
//        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
//        this.authBy = authBy == null ? null : StringEscapeUtils.escapeHtml4(authBy.trim());
//    }
//
//    public static TreatyDefinitionDTO data(final Long treatyId,
//                                           final String treatyType,
//                                           final Date wef,
//                                           final Date wet,
//                                           final BigDecimal cashCall,
//                                           final BigDecimal cessionRate,
//                                           final String rateType,
//                                           final BigDecimal profitCommission,
//                                           final BigDecimal managementFeeRate,
//                                           final BigDecimal premiumPortfolio,
//                                           final BigDecimal claimsPortfolio,
//                                           final BigDecimal limit,
//                                           final BigDecimal sumInsuredFrom,
//                                           final BigDecimal commRate,
//                                           final BigDecimal facCedeRate,
//                                           final Long currencyId,
//                                           final String currencyDesc,
//                                           final String raisedBy,
//                                           final String status,
//                                           final String authBy) {
//        return new TreatyDefinitionDTO(treatyId, treatyType, wef, wet, cashCall, cessionRate, rateType, profitCommission,
//                managementFeeRate, premiumPortfolio, claimsPortfolio, limit, sumInsuredFrom, commRate, facCedeRate,
//                currencyId, currencyDesc, raisedBy, status, authBy);
//    }
//
//    private void validateAndSanitize(String input, String pattern, String fieldName) {
//        if (input == null) {
//            return;
//        }
//        logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
//        String trimmedInput = input.trim();
//        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
//            throw new IllegalArgumentException(
//                    String.format("Invalid characters in %s: '%s' does not match pattern %s", fieldName, trimmedInput, pattern)
//            );
//        }
//    }
//
//
//
//    public void setTreatyType(String treatyType) {
//        validateAndSanitize(treatyType, REF_PATTERN, "Treaty Type");
//        this.treatyType = treatyType == null ? null : StringEscapeUtils.escapeHtml4(treatyType.trim());
//    }
//
//    public void setRateType(String rateType) {
//        validateAndSanitize(rateType, REF_PATTERN, "Rate Type");
//        this.rateType = rateType == null ? null : StringEscapeUtils.escapeHtml4(rateType.trim());
//    }
//
//    public void setCurrencyDesc(String currencyDesc) {
//        validateAndSanitize(currencyDesc, REF_PATTERN, "Currency Description");
//        this.currencyDesc = currencyDesc == null ? null : StringEscapeUtils.escapeHtml4(currencyDesc.trim());
//    }
//
//    public void setRaisedBy(String raisedBy) {
//        validateAndSanitize(raisedBy, REF_PATTERN, "Raised By");
//        this.raisedBy = raisedBy == null ? null : StringEscapeUtils.escapeHtml4(raisedBy.trim());
//    }
//
//    public void setStatus(String status) {
//        validateAndSanitize(status, REF_PATTERN, "Status");
//        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
//    }
//
//    public void setAuthBy(String authBy) {
//        validateAndSanitize(authBy, REF_PATTERN, "Authorized By");
//        this.authBy = authBy == null ? null : StringEscapeUtils.escapeHtml4(authBy.trim());
//    }
//}