package com.brokersystems.brokerapp.uw.dtos;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Data;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
public class ClientsDto {

    private static final Logger logger = LoggerFactory.getLogger(ClientsDto.class);

    private  Long tenId;
    private  String tenantNumber;
    private  String fname;
    private  String otherNames;
    private  String idNo;
    private String pinNo;

    public Long getTenId() {
        return tenId;
    }

    public void setTenId(Long tenId) {
        this.tenId = tenId;
    }

    public String getTenantNumber() {
        return tenantNumber;
    }

    public String getFname() {
        return fname;
    }

    public String getOtherNames() {
        return otherNames;
    }

    public String getIdNo() {
        return idNo;
    }


    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        //logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s: '%s' does not match pattern %s", fieldName, trimmedInput, pattern)
            );
        }
    }

    public void setTenantNumber(String tenantNumber) {
        validateAndSanitize(tenantNumber, REF_PATTERN, "Tenant Number");
        this.tenantNumber = tenantNumber == null ? null : StringEscapeUtils.escapeHtml4(tenantNumber.trim());
    }

    public void setFname(String fname) {
        validateAndSanitize(fname, NAME_PATTERN, "First Name");
        this.fname = fname == null ? null : StringEscapeUtils.escapeHtml4(fname.trim());
    }

    public void setOtherNames(String otherNames) {
        validateAndSanitize(otherNames, NAME_PATTERN, "Other Names");
        this.otherNames = otherNames == null ? null : StringEscapeUtils.escapeHtml4(otherNames.trim());
    }

    public void setIdNo(String idNo) {
        validateAndSanitize(idNo, ID_NUMBER_PATTERN, "ID Number");
        this.idNo = idNo == null ? null : StringEscapeUtils.escapeHtml4(idNo.trim());
    }

    public void setPinNo(String pinNo) {
        validateAndSanitize(pinNo, ID_NUMBER_PATTERN, "PIN Number");
        this.pinNo = pinNo == null ? null : StringEscapeUtils.escapeHtml4(pinNo.trim());
    }
}