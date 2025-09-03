package com.brokersystems.brokerapp.users.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static com.brokersystems.brokerapp.common.Constants.*;

public class PermissionDTO {

    private static final Logger logger = LoggerFactory.getLogger(PermissionDTO.class);

    private Long permId;
    private String permName;
    private String permDesc;
    private String accessType;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String moduleName;
    private String makerCheckerRequired;

    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getPermId() {
        return permId;
    }

    public void setPermId(Long permId) {
        this.permId = permId;
    }

    public String getPermName() {
        return permName;
    }


    public String getPermDesc() {
        return permDesc;
    }


    public String getAccessType() {
        return accessType;
    }


    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getModuleName() {
        return moduleName;
    }


    public String getMakerCheckerRequired() {
        return makerCheckerRequired;
    }

   

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        //logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s", fieldName)
            );
        }
    }

    public void setPermName(String permName) {
        validateAndSanitize(permName, NAME_PATTERN, "Permission Name");
        this.permName = permName == null ? null : StringEscapeUtils.escapeHtml4(permName.trim());
    }

    public void setPermDesc(String permDesc) {
        validateAndSanitize(permDesc, DESC_PATTERN, "Permission Description");
        this.permDesc = permDesc == null ? null : StringEscapeUtils.escapeHtml4(permDesc.trim());
    }

    public void setAccessType(String accessType) {
        validateAndSanitize(accessType, REF_PATTERN, "Access Type");
        this.accessType = accessType == null ? null : StringEscapeUtils.escapeHtml4(accessType.trim());
    }

    public void setModuleName(String moduleName) {
        validateAndSanitize(moduleName, NAME_PATTERN, "Module Name");
        this.moduleName = moduleName == null ? null : StringEscapeUtils.escapeHtml4(moduleName.trim());
    }

    public void setMakerCheckerRequired(String makerCheckerRequired) {
        validateAndSanitize(makerCheckerRequired, REF_PATTERN, "Maker Checker Required");
        this.makerCheckerRequired = makerCheckerRequired == null ? null : StringEscapeUtils.escapeHtml4(makerCheckerRequired.trim());
    }
}
